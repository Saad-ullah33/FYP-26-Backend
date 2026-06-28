package com.propsightai.Controller.AdminController;

import com.propsightai.Model.User;
import com.propsightai.Repository.UserRepository;
import com.propsightai.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired private JwtService jwtService;

    // ================= GET ALL USERS =================
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ================= UPDATE USER =================
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id,
                           @RequestBody User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setUserType(updatedUser.getUserType());
        user.setActive(updatedUser.getActive());

        return userRepository.save(user);
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
    public User toggleStatus(@PathVariable Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(!user.getActive());

        return userRepository.save(user);
    }
}