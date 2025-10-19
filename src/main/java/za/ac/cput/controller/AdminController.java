package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Admin;
import za.ac.cput.factory.AdminFactory;
import za.ac.cput.repository.AdminRepository;
import za.ac.cput.service.AdminService;
import za.ac.cput.util.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AdminController(AdminService adminService, AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Admin adminInput) {
        try {
            // Check if admin already exists
            Admin existingAdmin = adminRepository.findByEmail(adminInput.getEmail());
            if (existingAdmin != null) {
                return ResponseEntity.badRequest().body("Admin with this email already exists");
            }

            // Build admin with encrypted password
            Admin newAdmin = AdminFactory.buildAdmin(
                    adminInput.getFirstName(),
                    adminInput.getLastName(),
                    adminInput.getEmail(),
                    adminInput.getPassword(),
                    passwordEncoder
            );

            if (newAdmin == null) {
                return ResponseEntity.badRequest().body("Invalid input fields");
            }

            Admin saved = adminService.create(newAdmin);

            // Remove password from response
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("firstName", saved.getFirstName());
            response.put("lastName", saved.getLastName());
            response.put("email", saved.getEmail());
            response.put("role", saved.getRole());
            response.put("message", "Admin registered successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody Admin loginInput) {
        try {
            Admin admin = adminRepository.findByEmail(loginInput.getEmail());

            if (admin == null || !passwordEncoder.matches(loginInput.getPassword(), admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole(), admin.getId());

            // Return user data with token
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", admin.getId());
            response.put("firstName", admin.getFirstName());
            response.put("lastName", admin.getLastName());
            response.put("email", admin.getEmail());
            response.put("role", admin.getRole());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Admin>> getAll() {
        List<Admin> admins = adminService.getAll();
        // Remove passwords from response for security
        admins.forEach(admin -> admin.setPassword(null));
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        Admin admin = adminService.read(id);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        // Remove password from response
        admin.setPassword(null);
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Admin admin) {
        try {
            // If password is being updated, encrypt it
            if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
                String encryptedPassword = passwordEncoder.encode(admin.getPassword());
                admin.setPassword(encryptedPassword);
            } else {
                // Keep existing password
                Admin existingAdmin = adminService.read(admin.getId());
                if (existingAdmin != null) {
                    admin.setPassword(existingAdmin.getPassword());
                }
            }

            Admin updated = adminService.update(admin);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            // Remove password from response
            updated.setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean deleted = adminService.delete(id);
            if (!deleted) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body("Admin deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Delete error: " + e.getMessage());
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "Admin backend is running!";
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            String email = jwtUtil.extractEmail(jwt);
            String role = jwtUtil.extractRole(jwt);
            Long userId = jwtUtil.extractUserId(jwt);

            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            Admin admin = adminService.read(userId);
            if (admin == null) {
                return ResponseEntity.notFound().build();
            }

            // Remove password from response
            admin.setPassword(null);
            return ResponseEntity.ok(admin);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}