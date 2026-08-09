package app.service.user;

import app.exception.UserAlreadyExistsException;
import app.exception.UserNotFoundException;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.Server;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static app.util.user.UserFactory.getRegisterDTO;
import static app.util.user.UserFactory.getUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceItTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_shouldCreateUserAndHero() {

        RegisterDTO request = getRegisterDTO();

        UserDTO result = userService.register(request);

        assertNotNull(result);
        assertNotNull(result.getId());

        User user = userRepository.findById(result.getId())
                .orElseThrow();

        assertEquals("testUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
        assertEquals(Server.EUROPE, user.getServer());
        assertTrue(user.isActive());

        assertNotEquals("password123", user.getPassword());
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));

        assertEquals("/images/warrior.jpeg", user.getProfilePicture());

        Hero hero = heroRepository.findByUserId(user.getId())
                .orElseThrow();

        assertEquals("Aragorn", hero.getRoleplayName());
        assertEquals(HeroClass.WARRIOR, hero.getHeroClass());
        assertEquals(1, hero.getLevel());
        assertEquals(0, hero.getXp());
        assertEquals(100, hero.getGold());
        assertEquals(user.getId(), hero.getUser().getId());
    }

    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {

        RegisterDTO request = getRegisterDTO();

        userService.register(request);

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.register(request)
        );
    }

    @Test
    void getById_shouldReturnUserDTO() {

        User user = getUser();

        userRepository.save(user);

        UserDTO result = userService.getById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());
        assertEquals(Server.EUROPE, result.getServer());
        assertTrue(result.isActive());
    }

    @Test
    void getById_shouldThrowException_whenUserDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getById(id)
        );
    }
}
