package com.propsightai.Controller.AdminController;

import com.propsightai.Repository.AuctionRepository;
import com.propsightai.Repository.PropertyRepository;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminDashboardController {

    @Autowired private UserRepository userRepository;
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private BidRepository bidRepository;

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(com.propsightai.Role.UserStatus.ACTIVE);
        long blockedUsers = userRepository.countByStatus(com.propsightai.Role.UserStatus.BLOCKED);
        long pendingUsers = userRepository.countByStatus(com.propsightai.Role.UserStatus.PENDING_VERIFICATION);

        long totalProperties = propertyRepository.count();
        long activeAuctions = auctionRepository.countByStatus(com.propsightai.Role.AuctionStatus.ACTIVE);
        long totalBids = bidRepository.count();

        return Map.of(
                "totalUsers", totalUsers,
                "activeUsers", activeUsers,
                "blockedUsers", blockedUsers,
                "pendingUsers", pendingUsers,
                "totalProperties", totalProperties,
                "activeAuctions", activeAuctions,
                "totalBids", totalBids
        );
    }
}