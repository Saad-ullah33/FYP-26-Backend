package com.propsightai.Controller.UserController;

import com.propsightai.Dto.AuctionAnalytics;
import com.propsightai.Dto.PropertyAnalytics;
import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Repository.BidRepository;
import com.propsightai.Repository.AuctionRepository;
import com.propsightai.Role.AuctionStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user/analytics")
@RequiredArgsConstructor
public class UserUserAnalyticsController {

    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    /**
     * Fetches personalized transactional insights and performance metrics 
     * for the logged-in user's private dashboard.
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getPersonalAnalyticsOverview(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = resolveCurrentUser(userDetails);
        log.info("Compiling personal analytics workspace for user ID: {}", user.getId());

        Map<String, Object> metrics = new HashMap<>();
        
        // Bidding Activity Metrics
        metrics.put("totalBidsPlaced", bidRepository.countByBidderId(user.getId()));
        metrics.put("successfulAuctionsWon", bidRepository.countSuccessfulBidsByBidder(user.getId()));
        metrics.put("totalCapitalExpended", bidRepository.sumTotalAmountSpentByBidder(user.getId()));

        // Listing Activity Metrics (Sourced from our optimized soft-delete query methods)
        metrics.put("myTotalAuctionsCreated", auctionRepository.countByProperty_Owner_IdAndIsDeletedFalse(user.getId()));
        metrics.put("myActiveAuctions", auctionRepository.countByProperty_Owner_IdAndStatusAndIsDeletedFalse(user.getId(), AuctionStatus.ACTIVE));
        metrics.put("mySoldAuctions", auctionRepository.countByProperty_Owner_IdAndStatusAndIsDeletedFalse(user.getId(), AuctionStatus.SOLD));

        return ResponseEntity.ok(metrics);
    }

    private User resolveCurrentUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new SecurityException("Unauthorized context: Principal identity assignment unresolved.");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Principal entity account matching identity data could not be verified."));
    }
}