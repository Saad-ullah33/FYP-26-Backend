package com.propsightai.Controller.AuctionWebSocketController;


import com.propsightai.Dto.BidCreateDto;
import com.propsightai.Dto.BidResponseDto;
import com.propsightai.Service.BidService;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
public class AuctionWebSocketController {


    private final BidService bidService;


    public AuctionWebSocketController(
            BidService bidService
    ){
        this.bidService = bidService;
    }



    @MessageMapping("/auction/{auctionId}/bid")
    @SendTo("/topic/auction/{auctionId}")
    public BidResponseDto placeBid(
            @DestinationVariable int auctionId,
            BidCreateDto request,
            Principal principal
    ) {

        return bidService.placeBid(
                auctionId,
                request.getAmount(),
                principal
        );
    }
    }