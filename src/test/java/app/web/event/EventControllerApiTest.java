package app.web.event;

import app.security.CustomAuthenticationFailureHandler;
import app.service.event.EventService;
import app.service.item.ItemService;
import app.web.item.ItemController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(EventController.class)
public class EventControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
}
