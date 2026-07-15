package com.propsightai.Controller.AdminController;

import com.propsightai.Dto.UserDto;
import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.Role.UserStatus;
import com.propsightai.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired private JwtService jwtService;

    @Autowired
    private com.propsightai.AuditService auditService;

    // ================= GET ALL USERS =================
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(u -> {
            UserDto dto = new UserDto();
            dto.setId(u.getId());
            dto.setName(u.getName());
            dto.setEmail(u.getEmail());
            dto.setPhone(u.getPhone());
            dto.setProfile(u.getImage());
            dto.setAddress(u.getAddress());
            return dto;
        }).toList();
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setProfile(u.getImage());
        dto.setAddress(u.getAddress());
        return dto;
    }

    // ================= ADMIN ACTIONS =================
    @PostMapping("/{id}/approve")
    public UserDto approveUser(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.ACTIVE);
        user.setEmailVerified(true);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.ADMIN_ACTION, id, "Approved user");
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getImage(), user.getAddress());
    }

    @PostMapping("/{id}/reject")
    public UserDto rejectUser(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.REJECTED);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.ADMIN_ACTION, id, "Rejected user");
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getImage(), user.getAddress());
    }

    @PostMapping("/{id}/block")
    public UserDto blockUser(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.BLOCKED);
        user.setActive(false);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.USER_BLOCKED, id, "Blocked by admin");
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getImage(), user.getAddress());
    }

    @PostMapping("/{id}/unblock")
    public UserDto unblockUser(@PathVariable Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.ACTIVE);
        user.setActive(true);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.ADMIN_ACTION, id, "Unblocked by admin");
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getImage(), user.getAddress());
    }
    @Transactional
    @PostMapping("/{id}/verify")
    public UserDto verifyUser(@PathVariable Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Before Active = {}", user.getActive());

        user.setEmailVerified(true);
        user.setActive(true);
        user.setStatus(UserStatus.ACTIVE);

        log.info("After Set Active = {}", user.getActive());

        userRepository.saveAndFlush(user);

        User dbUser = userRepository.findById(id).orElseThrow();

        log.info("After Save DB Active = {}", dbUser.getActive());
        log.info("After Save Email Verified = {}", dbUser.getEmailVerified());

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getImage(),
                user.getAddress()
        );
    }

    // ================= DELETE USER =================
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);

        return "User deleted successfully";
    }

    // ================= TOGGLE ACTIVE STATUS =================
    @PatchMapping("/{id}/toggle-status")
    public UserDto toggleStatus(@PathVariable Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(!user.getActive());

        userRepository.save(user);

        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getImage(), user.getAddress());
    }
}