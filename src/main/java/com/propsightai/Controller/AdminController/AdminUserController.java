package com.propsightai.Controller.AdminController;

import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired private JwtService jwtService;

    @Autowired
    private com.propsightai.AuditService auditService;

    // ================= GET ALL USERS =================
    @GetMapping
    public List<com.propsightai.Dto.UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(u -> {
            com.propsightai.Dto.UserDto dto = new com.propsightai.Dto.UserDto();
            dto.setId(u.getId());
            dto.setName(u.getName());
            dto.setEmail(u.getEmail());
            dto.setPhone(u.getPhone());
            dto.setProfile(u.getProfile());
            dto.setAddress(u.getAddress());
            return dto;
        }).toList();
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{id}")
    public com.propsightai.Dto.UserDto getUserById(@PathVariable Integer id) {
        com.propsightai.Model.User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        com.propsightai.Dto.UserDto dto = new com.propsightai.Dto.UserDto();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setProfile(u.getProfile());
        dto.setAddress(u.getAddress());
        return dto;
    }

    // ================= ADMIN ACTIONS =================
    @PostMapping("/{id}/approve")
    public com.propsightai.Dto.UserDto approveUser(@PathVariable Integer id) {
        com.propsightai.Model.User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.ACTIVE);
        user.setEmailVerified(true);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.ADMIN_ACTION, id, "Approved user");
        return new com.propsightai.Dto.UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getProfile(), user.getAddress());
    }

    @PostMapping("/{id}/reject")
    public com.propsightai.Dto.UserDto rejectUser(@PathVariable Integer id) {
        com.propsightai.Model.User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.REJECTED);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.ADMIN_ACTION, id, "Rejected user");
        return new com.propsightai.Dto.UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getProfile(), user.getAddress());
    }

    @PostMapping("/{id}/block")
    public com.propsightai.Dto.UserDto blockUser(@PathVariable Integer id) {
        com.propsightai.Model.User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.BLOCKED);
        user.setActive(false);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.USER_BLOCKED, id, "Blocked by admin");
        return new com.propsightai.Dto.UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getProfile(), user.getAddress());
    }

    @PostMapping("/{id}/unblock")
    public com.propsightai.Dto.UserDto unblockUser(@PathVariable Integer id) {
        com.propsightai.Model.User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(com.propsightai.Role.UserStatus.ACTIVE);
        user.setActive(true);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.ADMIN_ACTION, id, "Unblocked by admin");
        return new com.propsightai.Dto.UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getProfile(), user.getAddress());
    }

    @PostMapping("/{id}/verify")
    public com.propsightai.Dto.UserDto verifyUser(@PathVariable Integer id) {
        com.propsightai.Model.User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmailVerified(true);
        user.setStatus(com.propsightai.Role.UserStatus.ACTIVE);
        userRepository.save(user);
        auditService.record(com.propsightai.AuditEventType.USER_VERIFIED, id, "Manually verified by admin");
        return new com.propsightai.Dto.UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getProfile(), user.getAddress());
    }

    // ================= DELETE USER =================
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {

        com.propsightai.Model.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);

        return "User deleted successfully";
    }

    // ================= TOGGLE ACTIVE STATUS =================
    @PatchMapping("/{id}/toggle-status")
    public com.propsightai.Dto.UserDto toggleStatus(@PathVariable Integer id) {

        com.propsightai.Model.User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(!user.getActive());

        userRepository.save(user);

        return new com.propsightai.Dto.UserDto(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getProfile(), user.getAddress());
    }
}