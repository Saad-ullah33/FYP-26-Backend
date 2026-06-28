package com.propsightai.Controller.BidController;

import com.propsightai.Dto.BidMessage;
import com.propsightai.Model.Bid;
import com.propsightai.Service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bid")
@CrossOrigin(origins = "*")
public class BidController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private BidService bidService;

    @PostMapping("/placebid")
    public Bid placeBid(@RequestBody BidMessage bid) {

        Bid savedBid = bidService.placeBid(
                bid.getAuctionId(),
                bid.getUserId(),
                bid.getAmount()
        );

        messagingTemplate.convertAndSend(
                "/topic/auction/" + bid.getAuctionId(),
                savedBid
        );

        return savedBid;
    }
}