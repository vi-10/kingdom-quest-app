package app.service.user;

import app.exception.InvalidCredentialsException;
import app.exception.UserInactiveException;
import app.mapper.user.UserMapper;
import app.model.dto.user.LoginDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.user.User;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO login(LoginDTO loginData){
        User user = userRepository.findByUsername(loginData.getUsername())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid username or password"));

        if (!user.isActive()) {
            throw new UserInactiveException("User account is inactive");
        }

        if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return UserMapper.toUserDTO(user);
    }




}
