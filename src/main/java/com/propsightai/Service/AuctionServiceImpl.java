package com.propsightai.Service;

import com.propsightai.Dto.AuctionPublicDTO;
import com.propsightai.Dto.AuctionRequestDTO;
import com.propsightai.Model.Auction;
import com.propsightai.Model.Bid;
import com.propsightai.Model.Property;
import com.propsightai.ModelMapper.AuctionMapper;
import com.propsightai.Repository.AuctionRepository;
import com.propsightai.Repository.BidRepository;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Role.AuctionStatus;
import com.propsightai.Role.AuctionViewType;
import com.propsightai.AuditEventType;
import com.propsightai.AuditService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Default to read-only for database optimization
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final PropertyRepository propertyRepository;
    private final BidRepository bidRepository;
    private final AuditService auditService;

    // ---------------- CREATE AUCTION ----------------
    @Override
    @Transactional
    public AuctionPublicDTO createAuction(AuctionRequestDTO dto) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + dto.getPropertyId()));

        // Check if the property already has an unresolved active auction lifestyle
        boolean exists = auctionRepository.existsByPropertyIdAndIsDeletedFalseAndStatusIn(
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
            throw new IllegalStateException("This property already has an active or ongoing auction setup.");
        }

        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().isEqual(dto.getEndTime())) {
            throw new IllegalArgumentException("Auction start time must be explicitly before the end time.");
        }

        if (dto.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Auction start time cannot be set in the past.");
        }

        Auction auction = Auction.builder()
                .property(property)
                .startingPrice(dto.getStartingPrice())
                .reservePrice(dto.getReservePrice())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(AuctionStatus.PENDING_APPROVAL)
                .isDeleted(false)
                .build();

        Auction saved = auctionRepository.save(auction);

        // Non-blocking audit log execution strategy
        try {
            auditService.record(AuditEventType.AUCTION_UPDATED, null, "Auction created: " + saved.getId());
        } catch (Exception e) {
            log.error("Failed to record audit log for created auction ID: {}", saved.getId(), e);
        }

        return AuctionMapper.toPublicDTO(saved);
    }

    // ---------------- UPDATE AUCTION ----------------
    @Override
    @Transactional
    public AuctionPublicDTO updateAuction(Integer id, AuctionRequestDTO dto) {
        Auction auction = getActiveAuctionEntity(id);

        if (auction.getStatus() == AuctionStatus.ACTIVE || auction.getStatus() == AuctionStatus.SOLD) {
            throw new IllegalStateException("Live or completed auctions cannot be modified.");
        }

        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
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
        return AuctionMapper.toPublicDTO(getActiveAuctionEntity(id));
    }

    // ---------------- GET ALL (ACTIVE & EXCLUDING DELETED) ----------------
    @Override
    public List<AuctionPublicDTO> getAllAuctions() {
        // Enforce exclusion of soft-deleted records across common lookups
        return auctionRepository.findAllByIsDeletedFalse()
                .stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    // ---------------- DELETE (SOFT) ----------------
    @Override
    @Transactional
    public AuctionPublicDTO deleteAuction(Integer id) {
        Auction auction = getActiveAuctionEntity(id);

        if (auction.getStatus() == AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot delete an active auction. Cancel or stop it first.");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        auction.setDeleted(true);

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- APPROVE ----------------
    @Override
    @Transactional
    public AuctionPublicDTO approveAuction(Integer id) {
        Auction auction = getActiveAuctionEntity(id);

        validateStatus(auction, List.of(AuctionStatus.DRAFT, AuctionStatus.PENDING_APPROVAL));
        auction.setStatus(AuctionStatus.APPROVED);

        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- REJECT ----------------
    @Override
    @Transactional
    public AuctionPublicDTO rejectAuction(Integer id) {
        Auction auction = getActiveAuctionEntity(id);

        if (auction.getStatus() == AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Active auctions cannot be rejected mid-operation.");
        }

        auction.setStatus(AuctionStatus.REJECTED);
        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    // ---------------- PUBLISH ----------------
    @Override
    @Transactional
    public AuctionPublicDTO publishAuction(Integer id) {
        Auction auction = getActiveAuctionEntity(id);

        if (auction.getStatus() != AuctionStatus.APPROVED) {
            throw new IllegalStateException("Only approved auctions can be launched live.");
        }

        auction.setStatus(AuctionStatus.ACTIVE);
        return AuctionMapper.toPublicDTO(auctionRepository.save(auction));
    }

    @Override
    public List<AuctionPublicDTO> getAllAuctionListing() {
        return auctionRepository.findAllByIsDeletedFalse()
                .stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    //  FILTER By Enums
    @Override
    public List<AuctionPublicDTO> getAuctionsByFilter(AuctionViewType type) {
        return auctionRepository.findByStatusInAndIsDeletedFalse(type.getStatuses())
                .stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    @Override
    public List<AuctionPublicDTO> getFilteredAuctions(AuctionViewType view, AuctionStatus status) {
        List<AuctionStatus> allowedStatuses = view.getStatuses();
        List<Auction> auctions;

        if (status != null) {
            if (!allowedStatuses.contains(status)) {
                return List.of(); // Security Boundary Handshake Fallback
            }
            auctions = auctionRepository.findByStatusAndIsDeletedFalse(status);
        } else {
            auctions = auctionRepository.findByStatusInAndIsDeletedFalse(allowedStatuses);
        }

        return auctions.stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    // Finding winner and closing auction
    @Override
    @Transactional
    public void finalizeAuction(int auctionId) {
        Auction auction = getActiveAuctionEntity(auctionId);

        if (auction.getStatus() == AuctionStatus.CLOSED || auction.getStatus() == AuctionStatus.SOLD) {
            return;
        }

        // Pull highest valid bid record
        Bid highestBid = bidRepository.findTopByAuctionIdOrderByAmountDesc(auctionId);

        if (highestBid != null) {
            // Business logic requirement check: Minimum Reserve Price evaluation
            if (auction.getReservePrice() != null && highestBid.getAmount().compareTo(auction.getReservePrice()) < 0) {
                auction.setStatus(AuctionStatus.CLOSED); // Ended but Reserve not met
            } else {
                auction.setWinner(highestBid.getBidder());
                auction.setCurrentHighestBid(highestBid.getAmount());
                auction.setStatus(AuctionStatus.SOLD);
            }
        } else {
            auction.setStatus(AuctionStatus.CLOSED);
        }

        auctionRepository.save(auction);
    }

    @Override
    public int countByUser(Integer userId) {
        return auctionRepository.countByProperty_Owner_IdAndIsDeletedFalse(userId);
    }

    @Override
    public int countByUserAndStatus(Integer userId, AuctionStatus status) {
        return auctionRepository.countByProperty_Owner_IdAndStatusAndIsDeletedFalse(userId, status);
    }

    @Override
    public List<AuctionPublicDTO> getAuctionsByUser(Integer userId) {
        return auctionRepository.findByProperty_Owner_IdAndIsDeletedFalse(userId)
                .stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    @Override
    public List<AuctionPublicDTO> getAuctionsByUserAndStatus(Integer userId, AuctionStatus status) {
        return auctionRepository.findByProperty_Owner_IdAndStatusAndIsDeletedFalse(userId, status)
                .stream()
                .map(AuctionMapper::toPublicDTO)
                .toList();
    }

    // ---------------- INTERNAL CLEAN HELPERS ----------------

    private Auction getActiveAuctionEntity(Integer id) {
        Auction auction = auctionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Auction record not found with ID: " + id));

        if (Boolean.TRUE.equals(auction.getDeleted())) {
            throw new EntityNotFoundException("Auction record has been deleted.");
        }
        return auction;
    }

    private void validateStatus(Auction auction, List<AuctionStatus> allowed) {
        if (!allowed.contains(auction.getStatus())) {
            throw new IllegalStateException("Invalid status workflow modification request from status: " + auction.getStatus());
        }
    }
}