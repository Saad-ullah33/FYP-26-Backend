package com.propsightai.Service;

import com.propsightai.Dto.AuctionPublicDTO;
import com.propsightai.Dto.AuctionRequestDTO;
import com.propsightai.Model.Auction;
import com.propsightai.Model.Bid;
import com.propsightai.Model.Image;
import com.propsightai.Model.Property;
import com.propsightai.ModelMapper.AuctionMapper;
import com.propsightai.Repository.AuctionRepository;
import com.propsightai.Repository.BidRepository;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Role.AuctionStatus;
import com.propsightai.Role.AuctionViewType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final PropertyRepository propertyRepository;
    private final BidRepository bidRepository;

    // ---------------- CREATE AUCTION ----------------
    @Override
    public AuctionPublicDTO createAuction(AuctionRequestDTO dto) {

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        boolean exists = auctionRepository.existsByPropertyIdAndStatusIn(
                property.getId(),
                List.of(
                        AuctionStatus.DRAFT,
                        AuctionStatus.PENDING_APPROVAL,
                        AuctionStatus.APPROVED,
                        AuctionStatus.ACTIVE,
                        AuctionStatus.SCHEDULED
                )
        );

        if (exists) {
            throw new RuntimeException("Property already has an active/ongoing auction");
        }

        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        Auction auction = new Auction();
        auction.setProperty(property);
        auction.setStartingPrice(dto.getStartingPrice());
        auction.setReservePrice(dto.getReservePrice());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());
        auction.setStatus(AuctionStatus.PENDING_APPROVAL);
        auction.setCurrentHighestBid(null);

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- UPDATE AUCTION ----------------
    @Override
    public AuctionPublicDTO updateAuction(Integer id, AuctionRequestDTO dto) {

        Auction auction = getAuctionEntity(id);

        if (auction.getStatus() == AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Active auction cannot be updated");
        }

        auction.setStartingPrice(dto.getStartingPrice());
        auction.setReservePrice(dto.getReservePrice());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- GET BY ID ----------------
    @Override
    public AuctionPublicDTO getAuctionById(Integer id) {
        return AuctionMapper.toPublicDTO(getAuctionEntity(id));
    }

    // ---------------- GET ALL ----------------
    @Override
    public List<AuctionPublicDTO> getAllAuctions() {
        return auctionRepository.findAll()
                .stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    // ---------------- DELETE (SOFT) ----------------
    @Override
    public AuctionPublicDTO deleteAuction(Integer id) {

        Auction auction = getAuctionEntity(id);

        auction.setStatus(AuctionStatus.CANCELLED);
        auction.setDeleted(true);

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- APPROVE ----------------
    @Override
    public AuctionPublicDTO approveAuction(Integer id) {

        Auction auction = getAuctionEntity(id);

        validateStatus(auction, List.of(
                AuctionStatus.DRAFT,
                AuctionStatus.PENDING_APPROVAL
        ));
        auction.setStatus(AuctionStatus.APPROVED);

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- REJECT ----------------
    @Override
    public AuctionPublicDTO rejectAuction(Integer id) {

        Auction auction = getAuctionEntity(id);

        if (auction.getStatus() == AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Active auction cannot be rejected");
        }

        auction.setStatus(AuctionStatus.REJECTED);

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- PUBLISH ----------------
    @Override
    public AuctionPublicDTO publishAuction(Integer id) {

        Auction auction = getAuctionEntity(id);

        if (auction.getStatus() != AuctionStatus.APPROVED) {
            throw new IllegalStateException("Only approved auctions can be published");
        }

        auction.setStatus(AuctionStatus.ACTIVE);

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    @Override
    public List<AuctionPublicDTO> getAllAuctionListing() {
        List<Auction> auctions = auctionRepository.findAll();

        return auctions.stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    //  FILTER By Enums
    @Override
    public List<AuctionPublicDTO> getAuctionsByFilter(AuctionViewType type) {

        return auctionRepository.findByStatusIn(type.getStatuses())
                .stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    @Override
    public List<AuctionPublicDTO> getFilteredAuctions(
            AuctionViewType view,
            AuctionStatus status
    ) {

        List<AuctionStatus> allowedStatuses =
                view.getStatuses();

        List<Auction> auctions;

        if (status != null) {

            // Security check
            if (!allowedStatuses.contains(status)) {
                return List.of();
            }

            auctions =
                    auctionRepository.findByStatus(status);

        } else {
            auctions =
                    auctionRepository.findByStatusIn(
                            allowedStatuses
                    );
        }
        return auctions.stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }


    //finding winner and closing auction
    @Override
    public void finalizeAuction(int auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // already closed
        if (auction.getStatus() == AuctionStatus.CLOSED ||
                auction.getStatus() == AuctionStatus.SOLD) {
            return;
        }

        // get highest bid
        Bid highestBid = bidRepository
                .findTopByAuctionIdOrderByAmountDesc(auctionId);

        if (highestBid != null) {
            auction.setWinner(highestBid.getBidder());
            auction.setCurrentHighestBid(highestBid.getAmount());
            auction.setStatus(AuctionStatus.SOLD);
        } else {
            auction.setStatus(AuctionStatus.CLOSED);
        }

        auctionRepository.save(auction);
    }


    // INTERNAL CLEAN HELPERS (ERP STYLE)

    private Auction getAuctionEntity(Integer id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auction not found"));
    }

    private void validateStatus(Auction auction, List<AuctionStatus> allowed) {
        if (!allowed.contains(auction.getStatus())) {
            throw new IllegalStateException(
                    "Invalid status transition from " + auction.getStatus()
            );
        }
    }
}
