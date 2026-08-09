package app.service.quest;

import app.model.dto.quest.QuestDTO;
import app.model.entity.quest.Quest;
import app.model.entity.quest.QuestType;
import app.repository.hero.HeroRepository;
import app.repository.quest.QuestRepository;
import app.repository.user.UserRepository;
import app.service.event.EventService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

        Quest quest1 = Quest.builder()
                .title("Defeat the Goblin")
                .description("Defeat a dangerous goblin.")
                .questType(QuestType.COMBAT)
                .requiredLevel(1)
                .rewardXp(100)
                .rewardGold(50)
                .build();

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
        assertEquals(100, returnedQuest1.getRewardXp());
        assertEquals(50, returnedQuest1.getRewardGold());

        assertEquals("Mercenary attack", returnedQuest2.getTitle());
        assertEquals("Defeat a dangerous mercenary.", returnedQuest2.getDescription());
        assertEquals(QuestType.COMBAT, returnedQuest2.getQuestType());
        assertEquals(1, returnedQuest2.getRequiredLevel());
        assertEquals(100, returnedQuest2.getRewardXp());
        assertEquals(50, returnedQuest2.getRewardGold());
    }

}
