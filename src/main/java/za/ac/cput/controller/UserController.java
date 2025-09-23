package za.ac.cput.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.User;
import za.ac.cput.factory.UserFactory;
import za.ac.cput.repository.UserRepository;

import za.ac.cput.service.UserService;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@RestController
@RequestMapping("/customers")
public class UserController {

    private final UserService customerService;
    private final UserRepository customerRepository;

    @Autowired
    public UserController(UserService customerService, UserRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User customerInput) {
        try {
            // Simple check for existing email (remove if repository method doesn't exist)
            // Optional<Customer> existingCustomer = customerRepository.findByEmail(customerInput.getEmail());
            // if (existingCustomer != null && existingCustomer.isPresent()) {
            //     return ResponseEntity.badRequest().body("Email already registered");
            // }

            User newCustomer = UserFactory.buildCustomer(
                    customerInput.getFirstName(),
                    customerInput.getLastName(),
                    customerInput.getEmail(),
                    customerInput.getPassword()
            );

            if (newCustomer == null) {
                return ResponseEntity.badRequest().body("Invalid input fields");
            }

            User saved = customerService.create(newCustomer);

            // Simple response without password
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("userId", saved.getId());
            response.put("email", saved.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody User loginInput) {
        try {
            // Simple login - remove repository call if method doesn't exist
            // Customer customer = customerRepository.findByEmail(loginInput.getEmail());

            // For now, let's use a simple approach - get all customers and find by email
            List<User> allCustomers = customerService.getAll();
            User customer = null;

            for (User c : allCustomers) {
                if (c.getEmail().equals(loginInput.getEmail())) {
                    customer = c;
                    break;
                }
            }

            if (customer == null || !customer.getPassword().equals(loginInput.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }

            // Return user data without password
            Map<String, Object> response = new HashMap<>();
            response.put("id", customer.getId());
            response.put("firstName", customer.getFirstName());
            response.put("lastName", customer.getLastName());
            response.put("email", customer.getEmail());
            response.put("role", customer.getRole());
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<User> getAll() {
        return customerService.getAll();
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        User customer = customerService.read(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody User customer) {
        try {
            User updated = customerService.update(customer);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        try {
            customerService.delete(id);
            return ResponseEntity.ok().body("Customer deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Delete error: " + e.getMessage());
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "Customer backend is running!";
    }
}