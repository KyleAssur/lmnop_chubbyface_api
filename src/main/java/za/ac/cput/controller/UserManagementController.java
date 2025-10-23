package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.DTO.UserRequest;
import za.ac.cput.domain.Admin;
import za.ac.cput.domain.User;
import za.ac.cput.factory.AdminFactory;
import za.ac.cput.factory.UserFactory;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.AdminService;
import za.ac.cput.service.UserService;
import za.ac.cput.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserService userService;
    private final AdminService adminService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserManagementController(UserService userService, AdminService adminService,
                                    UserRepository userRepository, AdminRepository adminRepository,
                                    PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Get all users (regular users only)
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        try {
            List<User> users = userService.getAll();
            List<Map<String, Object>> userDTOs = users.stream()
                    .map(this::convertToUserDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all admins
    @GetMapping("/admins")
    public ResponseEntity<List<Map<String, Object>>> getAllAdmins() {
        try {
            List<Admin> admins = adminService.getAll();
            List<Map<String, Object>> adminDTOs = admins.stream()
                    .map(this::convertToAdminDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(adminDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create regular user
    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        try {
            // Check if user already exists
            User existingUser = userRepository.findByEmail(userRequest.getEmail());
            if (existingUser != null) {
                return ResponseEntity.badRequest().body("User with this email already exists");
            }

            User newUser = UserFactory.buildUser(
                    userRequest.getFirstName(),
                    userRequest.getLastName(),
                    userRequest.getEmail(),
                    userRequest.getPassword(),
                    passwordEncoder
            );

            if (newUser == null) {
                return ResponseEntity.badRequest().body("Invalid input fields");
            }

            User saved = userService.create(newUser);
            Map<String, Object> response = convertToUserDTO(saved);
            response.put("message", "User created successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    // Create admin
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody UserRequest adminRequest) {
        try {
            // Check if admin already exists
            Admin existingAdmin = adminRepository.findByEmail(adminRequest.getEmail());
            if (existingAdmin != null) {
                return ResponseEntity.badRequest().body("Admin with this email already exists");
            }

            Admin newAdmin = AdminFactory.buildAdmin(
                    adminRequest.getFirstName(),
                    adminRequest.getLastName(),
                    adminRequest.getEmail(),
                    adminRequest.getPassword(),
                    passwordEncoder
            );

            if (newAdmin == null) {
                return ResponseEntity.badRequest().body("Invalid input fields");
            }

            Admin saved = adminService.create(newAdmin);
            Map<String, Object> response = convertToAdminDTO(saved);
            response.put("message", "Admin created successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating admin: " + e.getMessage());
        }
    }

    // Update user or admin
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        try {
            // First try to find as User
            User existingUser = userService.read(id);
            if (existingUser != null) {
                return updateExistingUser(existingUser, userRequest);
            }

            // If not found as User, try as Admin
            Admin existingAdmin = adminService.read(id);
            if (existingAdmin != null) {
                return updateExistingAdmin(existingAdmin, userRequest);
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    private ResponseEntity<?> updateExistingUser(User existingUser, UserRequest userRequest) {
        // Check if email is being changed and if it's already taken
        if (!existingUser.getEmail().equals(userRequest.getEmail())) {
            User userWithEmail = userRepository.findByEmail(userRequest.getEmail());
            if (userWithEmail != null && !userWithEmail.getId().equals(existingUser.getId())) {
                return ResponseEntity.badRequest().body("Email is already taken by another user");
            }
        }

        // Update user fields
        User updatedUser = new User.Builder()
                .copy(existingUser)
                .setFirstName(userRequest.getFirstName())
                .setLastName(userRequest.getLastName())
                .setEmail(userRequest.getEmail())
                .build();

        // Update password only if provided
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
            updatedUser.setPassword(encryptedPassword);
        } else {
            updatedUser.setPassword(existingUser.getPassword());
        }

        User saved = userService.update(updatedUser);
        Map<String, Object> response = convertToUserDTO(saved);
        response.put("message", "User updated successfully");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> updateExistingAdmin(Admin existingAdmin, UserRequest adminRequest) {
        // Check if email is being changed and if it's already taken
        if (!existingAdmin.getEmail().equals(adminRequest.getEmail())) {
            Admin adminWithEmail = adminRepository.findByEmail(adminRequest.getEmail());
            if (adminWithEmail != null && !adminWithEmail.getId().equals(existingAdmin.getId())) {
                return ResponseEntity.badRequest().body("Email is already taken by another admin");
            }
        }

        // Update admin fields
        Admin updatedAdmin = new Admin.Builder()
                .copy(existingAdmin)
                .setFirstName(adminRequest.getFirstName())
                .setLastName(adminRequest.getLastName())
                .setEmail(adminRequest.getEmail())
                .build();

        // Update password only if provided
        if (adminRequest.getPassword() != null && !adminRequest.getPassword().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(adminRequest.getPassword());
            updatedAdmin.setPassword(encryptedPassword);
        } else {
            updatedAdmin.setPassword(existingAdmin.getPassword());
        }

        Admin saved = adminService.update(updatedAdmin);
        Map<String, Object> response = convertToAdminDTO(saved);
        response.put("message", "Admin updated successfully");
        return ResponseEntity.ok(response);
    }

    // Delete user or admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            // First try to delete as User
            User user = userService.read(id);
            if (user != null) {
                userService.delete(id);
                return ResponseEntity.ok().body("User deleted successfully");
            }

            // If not found as User, try as Admin
            Admin admin = adminService.read(id);
            if (admin != null) {
                adminService.delete(id);
                return ResponseEntity.ok().body("Admin deleted successfully");
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            // First try to find as User
            User user = userService.read(id);
            if (user != null) {
                return ResponseEntity.ok(convertToUserDTO(user));
            }

            // If not found as User, try as Admin
            Admin admin = adminService.read(id);
            if (admin != null) {
                return ResponseEntity.ok(convertToAdminDTO(admin));
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Check if email exists
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            User user = userRepository.findByEmail(email);
            Admin admin = adminRepository.findByEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("exists", user != null || admin != null);
            response.put("userType", user != null ? "USER" : admin != null ? "ADMIN" : "NONE");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods to convert entities to DTO
    private Map<String, Object> convertToUserDTO(User user) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", user.getId());
        dto.put("firstName", user.getFirstName());
        dto.put("lastName", user.getLastName());
        dto.put("email", user.getEmail());
        dto.put("role", user.getRole());
        dto.put("createdAt", LocalDateTime.now()); // You might want to add createdAt field to your User entity
        return dto;
    }

    private Map<String, Object> convertToAdminDTO(Admin admin) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", admin.getId());
        dto.put("firstName", admin.getFirstName());
        dto.put("lastName", admin.getLastName());
        dto.put("email", admin.getEmail());
        dto.put("role", admin.getRole());
        dto.put("createdAt", LocalDateTime.now()); // You might want to add createdAt field to your Admin entity
        return dto;
    }

    @GetMapping("/ping")
    public String ping() {
        return "User Management API is running!";
    }
}