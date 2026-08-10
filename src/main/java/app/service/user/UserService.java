package app.service.user;

import app.exception.UserAlreadyExistsException;
import app.exception.UserNotFoundException;
import app.mapper.user.UserMapper;
import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.user.UserRepository;
import app.security.AuthenticationUserDetails;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private HeroRepository heroRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, HeroRepository heroRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.heroRepository = heroRepository;
        this.passwordEncoder = passwordEncoder;
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
        log.info("Registering new user with username '{}'", registerData.getUsername());

        if (userRepository.existsByUsername(registerData.getUsername())) {
            throw new UserAlreadyExistsException(registerData.getUsername());
        }

        User user = User.builder()
                .username(registerData.getUsername())
                .password(passwordEncoder.encode(registerData.getPassword()))
                .email(registerData.getEmail())
                .profilePicture(getDefaultProfilePicture(registerData.getHeroClass()))
                .role(Role.USER)
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

        log.info("User '{}' registered successfully with ID {}", user.getUsername(), user.getId());

        return UserMapper.toUserDTO(user);
    }


    public UserDTO getById(UUID id) {
        log.debug("Fetching user with ID {}", id);

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        return UserMapper.toUserDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        log.debug("Fetching all users");

        List<UserDTO> users =  userRepository.findAll().stream().map(UserMapper::toUserDTO).toList();

        log.debug("Fetched {} users", users.size());

        return users;
    }

    public void switchRole(UUID id) {
        log.info("Switching role for user with ID {}", id);

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        Role oldRole = user.getRole();

        if (user.getRole() == Role.USER) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        userRepository.save(user);

        log.info("User with ID {} role changed from {} to {}", id, oldRole, user.getRole());
    }

    public void switchStatus(UUID id) {
        log.info("Switching active status for user with ID {}", id);

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        boolean oldStatus = user.isActive();

        user.setActive(!user.isActive());
        userRepository.save(user);

        if(oldStatus){
            log.info("User with ID {} status changed to inactive.", id);
        } else {
            log.info("User with ID {} status changed to active.", id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading authentication details for username '{}'", username);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return AuthenticationUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }

    public void editProfile(UUID userId, EditProfileRequest request) {
        log.info("Editing profile for user with ID {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());

        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            throw new UserAlreadyExistsException(request.getUsername());
        }

        Hero hero = user.getHero();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if(request.getProfilePicture() == null || request.getProfilePicture().isBlank()){
            user.setProfilePicture(getDefaultProfilePicture(hero.getHeroClass()));
        } else {
            user.setProfilePicture(request.getProfilePicture());
        }

        hero.setRoleplayName(request.getRoleplayName());

        userRepository.save(user);

        log.info("Profile for user with ID {} updated successfully", userId);
    }
}
