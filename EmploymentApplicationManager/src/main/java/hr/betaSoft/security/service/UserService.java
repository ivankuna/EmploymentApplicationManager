package hr.betaSoft.security.service;

import hr.betaSoft.security.model.User;
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

    boolean checkIfEmployeeUnderUserExist(long userId);

    User findByOib(String oib);
}