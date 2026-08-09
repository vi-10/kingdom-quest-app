package app.service.quest;

import app.exception.QuestNotFoundException;
import app.model.dto.quest.QuestDTO;
import app.model.dto.quest.QuestResultDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.quest.Quest;
import app.model.entity.quest.QuestType;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.quest.QuestRepository;
import app.repository.user.UserRepository;
import app.service.event.EventService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static app.util.quest.QuestFactory.getQuest;
import static app.util.user.UserFactory.getUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class QuestServiceItTest {
    @Autowired
    private QuestService questService;

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private EventService eventService;

    @Test
    void getAllQuests_shouldReturnAllQuests() {

        Quest quest1 = getQuest();

        Quest quest2 = Quest.builder()
                .title("Mercenary attack")
                .description("Defeat a dangerous mercenary.")
                .questType(QuestType.COMBAT)
                .requiredLevel(1)
                .rewardXp(100)
                .rewardGold(50)
                .build();

        questRepository.save(quest1);
        questRepository.save(quest2);

        List<QuestDTO> result = questService.getAllQuests();

        assertFalse(result.isEmpty());

        QuestDTO returnedQuest1 = result.stream()
                .filter(q -> q.getId().equals(quest1.getId()))
                .findFirst()
                .orElseThrow();

        QuestDTO returnedQuest2 = result.stream()
                .filter(q -> q.getId().equals(quest2.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals("Defeat the Goblin", returnedQuest1.getTitle());
        assertEquals("Defeat a dangerous goblin.", returnedQuest1.getDescription());
        assertEquals(QuestType.COMBAT, returnedQuest1.getQuestType());
        assertEquals(1, returnedQuest1.getRequiredLevel());
        assertEquals(80, returnedQuest1.getRewardXp());
        assertEquals(50, returnedQuest1.getRewardGold());

        assertEquals("Mercenary attack", returnedQuest2.getTitle());
        assertEquals("Defeat a dangerous mercenary.", returnedQuest2.getDescription());
        assertEquals(QuestType.COMBAT, returnedQuest2.getQuestType());
        assertEquals(1, returnedQuest2.getRequiredLevel());
        assertEquals(100, returnedQuest2.getRewardXp());
        assertEquals(50, returnedQuest2.getRewardGold());
    }

    @Test
    void completeQuest_shouldRewardHero() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        Quest quest = getQuest();

        questRepository.save(quest);

        when(eventService.getActiveEvent()).thenReturn(null);

        QuestResultDTO result =
                questService.completeQuest(quest.getId(), user.getId());

        assertTrue(result.isSuccess());
        assertEquals(
                "You earned 80 XP and 50 gold!\n",
                result.getMessage()
        );

        Hero updatedHero = heroRepository.findById(hero.getId()).orElseThrow();

        assertEquals(80, updatedHero.getXp());
        assertEquals(150, updatedHero.getGold());
        assertEquals(1, updatedHero.getLevel());
    }

    @Test
    void completeQuest_shouldFail_whenHeroClassCannotCompleteQuest() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Mage")
                .heroClass(HeroClass.MAGE)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        Quest quest = getQuest();

        questRepository.save(quest);

        QuestResultDTO result =
                questService.completeQuest(quest.getId(), user.getId());

        assertFalse(result.isSuccess());
        assertEquals(
                "Your hero class cannot complete this quest.",
                result.getMessage()
        );

        Hero unchangedHero = heroRepository.findById(hero.getId()).orElseThrow();

        assertEquals(0, unchangedHero.getXp());
        assertEquals(100, unchangedHero.getGold());
    }

    @Test
    void completeQuest_shouldFail_whenHeroLevelIsTooLow() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        Quest quest = Quest.builder()
                .title("High Level Quest")
                .description("A difficult quest.")
                .questType(QuestType.COMBAT)
                .requiredLevel(5)
                .rewardXp(100)
                .rewardGold(100)
                .build();

        questRepository.save(quest);

        QuestResultDTO result =
                questService.completeQuest(quest.getId(), user.getId());

        assertFalse(result.isSuccess());
        assertEquals(
                "Your level is too low for this quest.",
                result.getMessage()
        );
    }

    @Test
    void completeQuest_shouldThrow_whenQuestDoesNotExist() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        assertThrows(
                QuestNotFoundException.class,
                () -> questService.completeQuest(
                        UUID.randomUUID(),
                        user.getId()
                )
        );
    }

}
