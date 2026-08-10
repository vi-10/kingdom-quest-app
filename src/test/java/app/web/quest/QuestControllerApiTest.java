package app.web.quest;

import app.security.CustomAuthenticationFailureHandler;
import app.service.hero.HeroService;
import app.service.quest.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(QuestController.class)
public class QuestControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HeroService heroService;

    @MockitoBean
    private QuestService questService;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
}
