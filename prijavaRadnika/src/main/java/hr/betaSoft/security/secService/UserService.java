package hr.betaSoft.security.secService;

import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.userdto.UserDto;

import java.util.List;

public interface UserService {

    void saveUser(UserDto userDto);

    void deleteUser(Long id);

    List<User> findAll();

    User findById(long id);

    User findByUsername(String username);

    User getAuthenticatedUser();

    long countUsers();

    UserDto convertEntityToDto(User user);
}
