package hr.betaSoft.security.secService;

import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.userdto.UserDto;

import java.util.List;

public interface UserService {

    void saveUser(UserDto userDto);

    User findByUsername(String username);

    User findByEmail(String email);

    User findByEmailToSend(String emailToSend);

    List<UserDto> findAllUsers();

    List<User> findAll();

    long countUsers();
}
