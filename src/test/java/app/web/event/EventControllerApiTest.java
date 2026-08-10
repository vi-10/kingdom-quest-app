package app.web.event;

import app.model.dto.event.ActiveEventResponse;
import app.model.dto.event.CreateEventRequest;
import app.model.entity.quest.QuestType;
import app.model.entity.user.Role;
import app.security.AuthenticationUserDetails;
import app.security.CustomAuthenticationFailureHandler;
import app.service.event.EventService;
import app.util.user.UserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(EventController.class)
public class EventControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Test
    void getActiveEvent_shouldReturnEventView_andStatus200() throws Exception {

        AuthenticationUserDetails principal = UserFactory.getUserPrincipal();

        ActiveEventResponse event = ActiveEventResponse.builder()
                .title("Double XP Weekend")
                .description("Heroes receive bonus XP from combat quests.")
                .affectedQuestType(QuestType.COMBAT)
                .bonusXp(50)
                .bonusGold(25)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(23))
                .build();

        when(eventService.getActiveEvent()).thenReturn(event);

        MockHttpServletRequestBuilder request = get("/event").with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("event"))
                .andExpect(model().attribute("event", event));

        verify(eventService).getActiveEvent();
    }

    @Test
    void getActiveEvent_shouldReturnEventView_andStatus200_whenEventIsNull() throws Exception {

        AuthenticationUserDetails principal = UserFactory.getUserPrincipal();

        ActiveEventResponse event = null;

        when(eventService.getActiveEvent()).thenReturn(event);

        MockHttpServletRequestBuilder request = get("/event").with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("event"))
                .andExpect(model().attribute("event", event));

        verify(eventService).getActiveEvent();
    }

    @Test
    void getCreateEventPage_shouldReturnCreateEventView_andStatus200() throws Exception {

        AuthenticationUserDetails adminPrincipal = UserFactory.getAdminUser();

        MockHttpServletRequestBuilder request = get("/admin/events/create")
                .with(user(adminPrincipal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"))
                .andExpect(model().attributeExists("eventData"))
                .andExpect(model().attribute("eventData",
                        CreateEventRequest.builder().build()));
    }

    @Test
    void getCreateEventPage_asUser_shouldReturnForbidden() throws Exception {

        AuthenticationUserDetails principal = UserFactory.getUserPrincipal();

        principal.setRole(Role.USER);

        MockHttpServletRequestBuilder request = get("/admin/events/create")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        verifyNoInteractions(eventService);
    }


}
