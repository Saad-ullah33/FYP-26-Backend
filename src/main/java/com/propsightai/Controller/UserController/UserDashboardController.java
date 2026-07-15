package com.propsightai.Controller.UserController;

import com.propsightai.Dto.AuctionPublicDTO;
import com.propsightai.Dto.AuctionRequestDTO;
import com.propsightai.Dto.PropertyDto;
import com.propsightai.Dto.UserDashboardStatsDTO;
import com.propsightai.Dto.UserProfileDTO;
import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Service.AuctionService;
import com.propsightai.Service.PropertyService;
import com.propsightai.Role.AuctionStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserDashboardController {

    private final UserRepository userRepository;
    private final PropertyService propertyService;
    private final AuctionService auctionService;

    // ---------------- DASHBOARD STATS ----------------
    @GetMapping("/stats")
    public ResponseEntity<UserDashboardStatsDTO> getDashboardStats(@AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveCurrentUser(userDetails);

        UserDashboardStatsDTO dto = new UserDashboardStatsDTO();
        dto.setTotalProperties(propertyService.countByUser(user.getId()));
        dto.setTotalAuctions(auctionService.countByUser(user.getId()));
        dto.setActiveAuctions(auctionService.countByUserAndStatus(user.getId(), AuctionStatus.ACTIVE));
        dto.setPendingAuctions(auctionService.countByUserAndStatus(user.getId(), AuctionStatus.PENDING_APPROVAL));
        dto.setRejectedAuctions(auctionService.countByUserAndStatus(user.getId(), AuctionStatus.REJECTED));
        dto.setSoldAuctions(auctionService.countByUserAndStatus(user.getId(), AuctionStatus.SOLD));

        return ResponseEntity.ok(dto);
    }

    // ---------------- USER PROPERTIES ----------------
    @GetMapping("/properties")
    public ResponseEntity<List<PropertyDto>> getMyProperties(@AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveCurrentUser(userDetails);
        return ResponseEntity.ok(propertyService.getPropertiesByUser(user.getId()));
    }

    // ---------------- USER AUCTIONS ----------------
    @GetMapping("/auctions")
    public ResponseEntity<List<AuctionPublicDTO>> getMyAuctions(
            @RequestParam(value = "status", required = false) AuctionStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = resolveCurrentUser(userDetails);
        List<AuctionPublicDTO> auctions;

        if (status != null) {
            auctions = auctionService.getAuctionsByUserAndStatus(user.getId(), status);
        } else {
            auctions = auctionService.getAuctionsByUser(user.getId());
        }
        return ResponseEntity.ok(auctions);
    }

    // ---------------- DELETE PROPERTY LISTING ----------------
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveCurrentUser(userDetails);
        propertyService.deletePropertyByIdAndUser(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ---------------- GET PROFILE DETAILS ----------------
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveCurrentUser(userDetails);

        UserProfileDTO dto = new UserProfileDTO();
        dto.setFullName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setCity(extractCityFromAddress(user.getAddress()));
        dto.setNotificationsEnabled(true); // Default fallback placeholder flag

        return ResponseEntity.ok(dto);
    }

    // ---------------- UPDATE PROFILE DETAILS ----------------
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @Validated @RequestBody UserProfileDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = resolveCurrentUser(userDetails);

        user.setName(dto.getFullName());
        user.setPhone(dto.getPhone());

        String formattedAddress = dto.getAddress();
        if (dto.getCity() != null && !dto.getCity().trim().isEmpty() && !formattedAddress.contains(dto.getCity())) {
            formattedAddress = formattedAddress + ", " + dto.getCity().trim();
        }
        user.setAddress(formattedAddress);
        userRepository.save(user);

        return ResponseEntity.ok(dto);
    }

    // ---------------- UPGRADE/PLACE PROPERTY ON AUCTION ----------------
    /**
     * Contextual Endpoint enabling a landlord/owner to officially map an existing property asset
     * into a live public/pending auction pipeline window request.
     */
    @PostMapping("/properties/{id}/enable-auction")
    public ResponseEntity<AuctionPublicDTO> enablePropertyAuction(
            @PathVariable Integer id,
            @Validated @RequestBody AuctionRequestDTO auctionRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = resolveCurrentUser(userDetails);

        // 1. Ownership Sanity Validation Check through the service boundary layout
        propertyService.verifyPropertyOwnership(id, user.getId());

        // 2. Map structural entity constraints onto the transaction execution layer
        auctionRequest.setPropertyId(id);
        AuctionPublicDTO activeAuctionDto = auctionService.createAuction(auctionRequest);

        // 3. Mark flags inside your core properties tables safely
        propertyService.enableAuctionForUserProperty(Long.valueOf(id), user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(activeAuctionDto);
    }

    // ---------------- INTERNAL UTILITIES ----------------

    private User resolveCurrentUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new SecurityException("Unauthorized context: Principal identity assignment unresolved.");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Principal entity account matching identity data could not be verified."));
    }

    private String extractCityFromAddress(String address) {
        if (address == null) return "Faisalabad";
        return address.toLowerCase().contains("faisalabad") ? "Faisalabad" : "Faisalabad";
    }
}