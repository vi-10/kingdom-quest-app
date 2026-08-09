package app.service.hero;

import app.exception.HeroNotFoundException;
import app.model.dto.hero.HeroDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static app.util.user.UserFactory.getUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class HeroServiceItTest {
    @Autowired
    private HeroService heroService;

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getByUserId_shouldReturnHero() {

        User user = getUser();

        userRepository.save(user);

        Hero hero = Hero.builder()
                .roleplayName("Aragorn")
                .heroClass(HeroClass.WARRIOR)
                .level(5)
                .xp(450)
                .gold(250)
                .user(user)
                .build();

        heroRepository.save(hero);

        HeroDTO result = heroService.getByUserId(user.getId());

        assertNotNull(result);
        assertEquals(hero.getId(), result.getId());
        assertEquals("Aragorn", result.getRoleplayName());
        assertEquals(HeroClass.WARRIOR, result.getHeroClass());
        assertEquals(5, result.getLevel());
        assertEquals(450, result.getXp());
        assertEquals(250, result.getGold());
    }

    @Test
    void getByUserId_shouldThrowException_whenHeroDoesNotExist() {

        UUID userId = UUID.randomUUID();

        assertThrows(
                HeroNotFoundException.class,
                () -> heroService.getByUserId(userId)
        );
    }
}
