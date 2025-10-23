package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.ac.cput.DTO.UserDTO;
import za.ac.cput.domain.User;
import za.ac.cput.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User create(User user) {
        User newUser = new User.Builder()
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setRole("USER")
                .build();

        return repository.save(newUser);
    }

    @Override
    public User read(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public User update(User user) {
        if (repository.existsById(user.getId())) {
            User updatedUser = new User.Builder()
                    .setId(user.getId())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword())
                    .setRole("USER")
                    .build();

            return repository.save(updatedUser);
        } else {
            System.out.println("User with ID " + user.getId() + " does not exist.");
            return null;
        }
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public String verify(User user) {
        User foundUser = repository.findById(user.getId()).orElse(null);

        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            return "success";
        } else {
            return "fail";
        }
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }
}