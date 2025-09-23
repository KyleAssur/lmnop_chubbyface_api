package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.User;
import za.ac.cput.repository.UserRepository;

import java.util.List;
@Service
public class UserService implements IUserService {

    private final UserRepository repository;
    @Autowired

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User create(User customer) {
        User newCustomer = new User.Builder()
                .setId(customer.getId())
                .SetfirstName(customer.getFirstName())
                .SetlastName(customer.getLastName())
                .SetEmail(customer.getEmail())
                .SetPassword(customer.getPassword())
                .SetRole("USER")
                .build();

        return repository.save(newCustomer);
    }

    @Override
    public User read(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public User update(User customer) {
        if (repository.existsById(customer.getId())) {
            User updatedCustomer = new User.Builder()
                    .setId(customer.getId())
                    .SetfirstName(customer.getFirstName())
                    .SetlastName(customer.getLastName())
                    .SetEmail(customer.getEmail())
                    .SetPassword(customer.getPassword())
                    .SetRole("USER")
                    .build();

            return repository.save(updatedCustomer);
        } else {
            System.out.println("Customer with ID " + customer.getId() + " does not exist.");
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

    public String verify(User customer) {
        User foundCustomer = repository.findById(customer.getId()).orElse(null);

        if (foundCustomer != null && foundCustomer.getPassword().equals(customer.getPassword())) {
            return "success";
        } else {
            return "fail";
        }
    }

    public User findByEmail(String email) {
        User foundCustomer = repository.findByEmail(email);
        return null;
    }
}
