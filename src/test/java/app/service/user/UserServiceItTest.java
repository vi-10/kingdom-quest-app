package app.service.user;

import app.exception.UserAlreadyExistsException;
import app.exception.UserNotFoundException;
import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.Server;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.user.UserRepository;
import app.security.AuthenticationUserDetails;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static app.util.user.UserFactory.*;
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

    @Test
    void loadUserByUsername_shouldReturnAuthenticationDetails() {

        User user = getUser();

        userRepository.save(user);

        UserDetails result = userService.loadUserByUsername("testUser");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.isEnabled());

        AuthenticationUserDetails authenticationDetails =
                (AuthenticationUserDetails) result;

        assertEquals(user.getId(), authenticationDetails.getId());
        assertEquals(Role.USER, authenticationDetails.getRole());
        assertTrue(authenticationDetails.isActive());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {

        assertThrows(
                UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknownUser")
        );
    }

    @Test
    void editProfile_shouldUpdateUserAndHero() {

        User user = User.builder()
                .username("oldUsername")
                .password("password")
                .email("old@example.com")
                .profilePicture("/images/warrior.jpeg")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        Hero hero = Hero.builder()
                .roleplayName("OldName")
                .heroClass(HeroClass.WARRIOR)
                .level(5)
                .xp(400)
                .gold(200)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        EditProfileRequest request = getEditProfileRequest();

        userService.editProfile(user.getId(), request);

        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        Hero updatedHero = heroRepository.findByUserId(updatedUser.getId())
                .orElseThrow();

        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals("/images/new-picture.jpg", updatedUser.getProfilePicture());

        assertEquals("NewName", updatedHero.getRoleplayName());

        assertEquals(HeroClass.WARRIOR, updatedHero.getHeroClass());
        assertEquals(5, updatedHero.getLevel());
        assertEquals(400, updatedHero.getXp());
        assertEquals(200, updatedHero.getGold());
    }

    @Test
    void editProfile_shouldUseDefaultPicture_whenProfilePictureIsBlank() {

        User user = User.builder()
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .profilePicture("/images/old.jpg")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        Hero hero = Hero.builder()
                .roleplayName("Hero")
                .heroClass(HeroClass.MAGE)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        EditProfileRequest request = EditProfileRequest.builder()
                .username("testUser")
                .email("test@example.com")
                .profilePicture("   ")
                .roleplayName("Updated Hero")
                .build();

        userService.editProfile(user.getId(), request);

        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        assertEquals("/images/mage.png", updatedUser.getProfilePicture());
    }

    @Test
    void editProfile_shouldThrowException_whenUserDoesNotExist() {

        EditProfileRequest request = getEditProfileRequest();

        assertThrows(
                UserNotFoundException.class,
                () -> userService.editProfile(UUID.randomUUID(), request)
        );
    }

    @Test
    void editProfile_shouldThrowException_whenUsernameBelongsToAnotherUser() {

        User firstUser = getUser();

        User secondUser = User.builder()
                .username("secondUser")
                .password("password")
                .email("second@example.com")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        userRepository.save(firstUser);
        userRepository.save(secondUser);

        EditProfileRequest request = EditProfileRequest.builder()
                .username("secondUser")
                .email("new@example.com")
                .profilePicture("/images/test.jpg")
                .roleplayName("Hero")
                .build();

        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.editProfile(firstUser.getId(), request)
        );
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {

        long usersBefore = userRepository.count();

        User firstUser = User.builder()
                .username("user1")
                .password("password")
                .email("user1@example.com")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        User secondUser = User.builder()
                .username("user2")
                .password("password")
                .email("user2@example.com")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        userRepository.save(firstUser);
        userRepository.save(secondUser);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(usersBefore + 2, result.size());

        assertTrue(result.stream()
                .anyMatch(user -> user.getUsername().equals("user1")));

        assertTrue(result.stream()
                .anyMatch(user -> user.getUsername().equals("user2")));
    }

    @Test
    void switchRole_shouldChangeUserRoleFromUserToAdmin() {

        User user = getUser();

        userRepository.save(user);

        userService.switchRole(user.getId());

        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        assertEquals(Role.ADMIN, updatedUser.getRole());
    }

    @Test
    void switchRole_shouldChangeUserRoleFromAdminToUser() {

        User user = User.builder()
                .username("testAdmin")
                .password("password")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        userRepository.save(user);

        userService.switchRole(user.getId());

        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        assertEquals(Role.USER, updatedUser.getRole());
    }

    @Test
    void switchRole_shouldThrowException_whenUserDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(
                UserNotFoundException.class,
                () -> userService.switchRole(id)
        );
    }

    @Test
    void switchStatus_shouldDeactivateActiveUser() {

        User user = getUser();

        userRepository.save(user);

        userService.switchStatus(user.getId());

        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        assertFalse(updatedUser.isActive());
    }

    @Test
    void switchStatus_shouldActivateInactiveUser() {

        User user = User.builder()
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(false)
                .build();

        userRepository.save(user);

        userService.switchStatus(user.getId());

        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow();

        assertTrue(updatedUser.isActive());
    }

    @Test
    void switchStatus_shouldThrowException_whenUserDoesNotExist() {

        UUID id = UUID.randomUUID();

        assertThrows(
                UserNotFoundException.class,
                () -> userService.switchStatus(id)
        );
    }

}
