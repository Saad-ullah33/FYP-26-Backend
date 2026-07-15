package com.propsightai.Controller.AdminController;

import com.propsightai.Dto.AuctionPublicDTO;
import com.propsightai.Role.AuctionStatus;
import com.propsightai.Role.AuctionViewType;
import com.propsightai.Service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/auctions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ROLE_ADMIN')")// Restricts this workspace entirely to Admin accounts
public class AdminAuctionController {

    private final AuctionService auctionService;

    /**
     * Fetch all system auctions (excluding soft-deleted ones).
     * GET /api/admin/auctions
     */
    @GetMapping
    public ResponseEntity<List<AuctionPublicDTO>> getAllAuctions() {
        log.info("Admin Request: Fetching all global auction records.");
        return ResponseEntity.ok(auctionService.getAllAuctions());
    }

    /**
     * Fetch an individual auction record details by its unique identifier.
     * GET /api/admin/auctions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuctionPublicDTO> getAuctionById(@PathVariable Integer id) {
        log.info("Admin Request: Retrieving complete profile data for auction ID: {}", id);
        return ResponseEntity.ok(auctionService.getAuctionById(id));
    }



    /**
     * Approve a pending auction request, making it valid for scheduling or immediate launch.
     * PUT /api/admin/auctions/{id}/approve
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<AuctionPublicDTO> approveAuction(@PathVariable Integer id) {
        log.info("Admin Request: Review and APPROVE workflow signature execution for auction ID: {}", id);
        AuctionPublicDTO updatedAuction = auctionService.approveAuction(id);
        return ResponseEntity.ok(updatedAuction);
    }

    /**
     * Reject a submitted or active auction request.
     * PUT /api/admin/auctions/{id}/reject
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<AuctionPublicDTO> rejectAuction(@PathVariable Integer id) {
        log.info("Admin Request: Executing REJECT workflow mutation state against auction ID: {}", id);
        AuctionPublicDTO updatedAuction = auctionService.rejectAuction(id);
        return ResponseEntity.ok(updatedAuction);
    }

    /**
     * Force-publish or manually activate an approved auction listing to go live instantly.
     * PUT /api/admin/auctions/{id}/publish
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<AuctionPublicDTO> publishAuction(@PathVariable Integer id) {
        log.info("Admin Request: Launching APPROVED auction live instantly for ID: {}", id);
        AuctionPublicDTO updatedAuction = auctionService.publishAuction(id);
        return ResponseEntity.ok(updatedAuction);
    }

    /**
     * Manually close or force-finalize a live active auction (evaluating top bidders or reserves).
     * POST /api/admin/auctions/{id}/finalize
     */
    @PostMapping("/{id}/finalize")
    public ResponseEntity<String> forceFinalizeAuction(@PathVariable Integer id) {
        log.info("Admin Request: Explicit command override received to close/finalize settlement mechanics on auction ID: {}", id);
        auctionService.finalizeAuction(id);
        return ResponseEntity.ok("Auction settlement processing executed successfully.");
    }

    /**
     * Hard administrative cancel/soft-delete override constraint mapping.
     * DELETE /api/admin/auctions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<AuctionPublicDTO> forceCancelAndDeleteAuction(@PathVariable Integer id) {
        log.info("Admin Request: Hard emergency cancellation routing signature for auction ID: {}", id);
        AuctionPublicDTO cancelledAuction = auctionService.deleteAuction(id);
        return ResponseEntity.ok(cancelledAuction);
    }
    /**
     * Filter auctions systematically by state criteria patterns.
     * GET /api/admin/auctions/filter?status=PENDING_APPROVAL
     */
    @GetMapping("/filter")
    public ResponseEntity<List<AuctionPublicDTO>> getAuctionsByStatus(
            @RequestParam(value = "status") AuctionStatus status) {

        // 1. Admin triggers a filter query for a specific status.
        // 2. The code grabs the corresponding view matrix (e.g., INACTIVE, ACTIVE, CONCLUDED).
        // 3. The database returns only secure, un-deleted matches instantly!
        return ResponseEntity.ok(auctionService.getFilteredAuctions(AuctionViewType.ADMIN, status));
    }
}