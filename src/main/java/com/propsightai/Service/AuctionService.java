package com.propsightai.Service;

import com.propsightai.Dto.AuctionPublicDTO;
import com.propsightai.Dto.AuctionRequestDTO;
import com.propsightai.Model.Auction;
import com.propsightai.Role.AuctionStatus;
import com.propsightai.Role.AuctionViewType;

import java.util.List;
public interface AuctionService {

        AuctionPublicDTO createAuction(AuctionRequestDTO dto);

        AuctionPublicDTO updateAuction(Integer id, AuctionRequestDTO dto);

        AuctionPublicDTO getAuctionById(Integer id);

        List<AuctionPublicDTO> getAllAuctions();

        AuctionPublicDTO deleteAuction(Integer id);

        AuctionPublicDTO approveAuction(Integer id);

        AuctionPublicDTO rejectAuction(Integer id);

        AuctionPublicDTO publishAuction(Integer id);
       List <AuctionPublicDTO> getAllAuctionListing();

        //who is seeing data like public,user,admin
        List<AuctionPublicDTO> getAuctionsByFilter(AuctionViewType type);

        //what type of data is showing to user
        List<AuctionPublicDTO> getFilteredAuctions(AuctionViewType view, AuctionStatus status);

        void finalizeAuction(int auctionId);
}