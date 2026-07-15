package com.propsightai.Controller.AuctionController;

import com.propsightai.Dto.AuctionPublicDTO;
import com.propsightai.Dto.AuctionRequestDTO;
import com.propsightai.Model.Property;
import com.propsightai.Role.AuctionStatus;
import com.propsightai.Role.AuctionViewType;
import com.propsightai.Service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    // CREATE AUCTION
    @PostMapping
    public AuctionPublicDTO createAuction(@RequestBody AuctionRequestDTO dto) {
        return auctionService.createAuction(dto);
    }

    // DELETE AUCTION (SOFT DELETE)
    @DeleteMapping("/delete/{id}")
    public String deleteAuction(@PathVariable Integer id) {
        auctionService.deleteAuction(id);
        return "Auction deleted successfully";
    }

    // GET BY ID
    @GetMapping("/{id}")
    public AuctionPublicDTO getById(@PathVariable Integer id) {
        return auctionService.getAuctionById(id);
    }

    // GET BY FILTER (PUBLIC / USER / ADMIN)
//    @GetMapping
//    public List<AuctionPublicDTO> getByFilter(@RequestParam AuctionViewType type) {
//        return auctionService.getAuctionsByFilter(type);
//    }

    @PutMapping("/{id}/approve")
    public AuctionPublicDTO approve(@PathVariable Integer id) {
        return auctionService.approveAuction(id);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<AuctionPublicDTO> publishAuction(@PathVariable Integer id) {

        AuctionPublicDTO response = auctionService.publishAuction(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<AuctionPublicDTO> getAuctions(
            @RequestParam AuctionViewType view,
            @RequestParam(required = false) AuctionStatus status
    ) {
        return auctionService.getFilteredAuctions(view, status);
    }

    @GetMapping("/getAllAuction")
    public List<AuctionPublicDTO> getAllAuctions()
    {
        return this. auctionService.getAllAuctionListing();
    }
}