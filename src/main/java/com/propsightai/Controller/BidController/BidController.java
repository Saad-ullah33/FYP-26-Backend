package com.propsightai.Controller.BidController;


import com.propsightai.Dto.BidCreateDto;
import com.propsightai.Dto.BidResponseDto;
import com.propsightai.Service.BidService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/bid")
@CrossOrigin(origins = "*")
public class BidController {


    @Autowired
    private BidService bidService;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    @PostMapping("/placebid")
    public BidResponseDto placeBid(
            @Valid @RequestBody BidCreateDto bidDto
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        if(authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException("Unauthenticated user" );
        }
        Principal principal =
                authentication::getName;
        BidResponseDto response =
                bidService.placeBid(
                        bidDto.getAuctionId(),
                        bidDto.getAmount(),
                        principal
                );
        // realtime update
        messagingTemplate.convertAndSend(
                "/topic/auction/"
                        + bidDto.getAuctionId(),
                response
        );
        return response;
    }
}