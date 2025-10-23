package za.ac.cput.service;

import za.ac.cput.DTO.UserDTO;
import za.ac.cput.domain.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User create(User customer);
    User read(Long id);
    User update(User customer);
    void delete(long id);
    List<User> getAll();
    String verify(User customer);
}
