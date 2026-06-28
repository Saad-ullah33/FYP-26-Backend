package com.propsightai.ModelMapper;

import com.propsightai.Dto.AuctionPublicDTO;
import com.propsightai.Model.Auction;

public class AuctionMapper {

    public static AuctionPublicDTO toPublicDTO(Auction auction) {

        AuctionPublicDTO dto = new AuctionPublicDTO();

        dto.setId(auction.getId());
        dto.setStatus(auction.getStatus().name());

        dto.setStartingPrice(auction.getStartingPrice());
        dto.setCurrentHighestBid(auction.getCurrentHighestBid());

        dto.setStartTime(auction.getStartTime());
        dto.setEndTime(auction.getEndTime());

        if (auction.getProperty() != null) {
            dto.setPropertyId(auction.getProperty().getId());
            dto.setPropertyTitle(auction.getProperty().getTitle());
        }

        return dto;
    }
}