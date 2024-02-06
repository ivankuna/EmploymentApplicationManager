package hr.betaSoft.security.secService;

import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.userdto.UserDto;

import java.util.List;

public interface UserService {

    void saveUser(UserDto userDto);

    void deleteUser(long id);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByEmailToSend(String emailToSend);

    List<UserDto> findAllUsers();

    List<User> findAll();

    long countUsers();

    UserDto convertEntityToDto(User user);

    User findById(long id);
}
