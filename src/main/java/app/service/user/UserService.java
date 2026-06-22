package app.service.user;

import app.exception.InvalidCredentialsException;
import app.exception.UserAlreadyExistsException;
import app.exception.UserInactiveException;
import app.exception.UserNotFoundException;
import app.mapper.user.UserMapper;
import app.model.dto.user.LoginDTO;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    private UserRepository userRepository;
    private HeroRepository heroRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, HeroRepository heroRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.heroRepository = heroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO login(LoginDTO loginData){
        User user = userRepository.findByUsername(loginData.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!user.isActive()) {
            throw new UserInactiveException("User account is inactive");
        }

        if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return UserMapper.toUserDTO(user);
    }

    private String getDefaultProfilePicture(HeroClass heroClass) {

        return switch (heroClass) {
            case WARRIOR -> "/images/warrior.jpeg";
            case MAGE -> "/images/mage.png";
            case ROGUE -> "/images/rogue.jpg";
            case HEALER -> "/images/healer.jpg";
        };
    }

    public UserDTO register(RegisterDTO registerData) {

        if (userRepository.existsByUsername(registerData.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        User user = User.builder()
                .username(registerData.getUsername())
                .password(passwordEncoder.encode(registerData.getPassword()))
                .email(registerData.getEmail())
                .profilePicture(getDefaultProfilePicture(registerData.getHeroClass()))
                .role(Role.PLAYER)
                .server(registerData.getServer())
                .isActive(true)
                .build();

        Hero hero = Hero.builder()
                .roleplayName(registerData.getRoleplayName())
                .heroClass(registerData.getHeroClass())
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        return UserMapper.toUserDTO(user);
    }


    public UserDTO getById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
        return UserMapper.toUserDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDTO).toList();
    }

    public void switchRole(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
        if (user.getRole() == Role.PLAYER) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.PLAYER);
        }
        userRepository.save(user);
    }

    public void switchStatus(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}
