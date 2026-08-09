package app.service.quest;

import app.repository.hero.HeroRepository;
import app.repository.quest.QuestRepository;
import app.repository.user.UserRepository;
import app.service.event.EventService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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

}
