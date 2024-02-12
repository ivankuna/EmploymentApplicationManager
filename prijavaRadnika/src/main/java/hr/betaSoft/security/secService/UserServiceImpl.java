package hr.betaSoft.security.secService;

import hr.betaSoft.security.secModel.Role;
import hr.betaSoft.security.secModel.User;
import hr.betaSoft.security.secRepo.RoleRepository;
import hr.betaSoft.security.secRepo.UserRepository;
import hr.betaSoft.security.userdto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {

        String role_name = "";
        Long tempId = 0L;

        User user = new User();
        user.setCompany(userDto.getCompany());
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setOib(userDto.getOib());
        user.setAddress(userDto.getAddress());
        user.setCity(userDto.getCity());
        user.setTelephone(userDto.getTelephone());
        user.setEmail(userDto.getEmail());
        user.setEmailToSend(userDto.getEmailToSend());
        user.setSmtpMail(userDto.getSmtpMail());
        user.setSmtpPass(userDto.getSmtpPass());
        user.setUsername(userDto.getUsername());

        if (userDto.getId() != null) {
            user.setId(userDto.getId());
            user.setPassword(userDto.getPassword());
            tempId = userDto.getId();
        } else {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (isUserTableEmpty() || tempId == 1L) {
            role_name = "ROLE_ADMIN";
        } else {
            role_name = "ROLE_USER";
        }

        Role role = roleRepository.findByName(role_name);

        if (role == null){
            role = checkRoleExist(role_name);
        }

        user.setRoles(Arrays.asList(role));

        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElse(null);

        if (user != null) {
            user.getRoles().clear();

            userRepository.save(user);

            userRepository.delete(user);
        }
    }

    @Override
    public List<User> findAll() {

        return userRepository.findAll();
    }

    @Override
    public User findById(long id) {

        return userRepository.findById(id);
    }

    @Override
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public User getAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return findByUsername(authentication.getName());
    }

    public long countUsers() {

        return userRepository.count();
    }

    public UserDto convertEntityToDto(User user){

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setCompany(user.getCompany());
        String[] name = user.getName().split(" ");
        userDto.setFirstName(name[0]);
        userDto.setLastName(name[1]);
        userDto.setOib(user.getOib());
        userDto.setAddress(user.getAddress());
        userDto.setCity(user.getCity());
        userDto.setTelephone(user.getTelephone());
        userDto.setEmail(user.getEmail());
        userDto.setEmailToSend(user.getEmailToSend());
        userDto.setSmtpMail(user.getSmtpMail());
        userDto.setSmtpPass(user.getSmtpPass());

        return userDto;
    }

    public boolean isUserTableEmpty() {

        long userCount = userRepository.count();
        return userCount == 0;
    }

    private Role checkRoleExist(String role_name) {

        Role role = new Role();
        role.setName(role_name);
        return roleRepository.save(role);
    }
}
