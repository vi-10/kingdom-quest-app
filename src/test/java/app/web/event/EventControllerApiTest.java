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

    @Test
    void createEvent_withValidData_asAdmin_shouldRedirectToAdminEvents() throws Exception {

        AuthenticationUserDetails adminPrincipal = UserFactory.getAdminUser();

        CreateEventRequest request = CreateEventRequest.builder()
                .title("Double Combat Rewards")
                .description("Combat quests give increased rewards.")
                .affectedQuestType(QuestType.COMBAT)
                .bonusXp(50)
                .bonusGold(25)
                .start(LocalDateTime.of(2026, 8, 1, 10, 0))
                .end(LocalDateTime.of(2026, 8, 7, 22, 0))
                .build();

        MockHttpServletRequestBuilder httpRequest = post("/admin/events/create")
                .with(user(adminPrincipal))
                .with(csrf())
                .param("title", request.getTitle())
                .param("description", request.getDescription())
                .param("affectedQuestType", request.getAffectedQuestType().name())
                .param("bonusXp", request.getBonusXp().toString())
                .param("bonusGold", request.getBonusGold().toString())
                .param("start", "2026-08-01T10:00")
                .param("end", "2026-08-07T22:00");

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/events"));

        verify(eventService).createEvent(request);
    }

    @Test
    void createEvent_withInvalidData_asAdmin_shouldReturnCreateEventView() throws Exception {

        AuthenticationUserDetails adminPrincipal = UserFactory.getAdminUser();

        MockHttpServletRequestBuilder request = post("/admin/events/create")
                .with(user(adminPrincipal))
                .with(csrf())
                .param("title", "")
                .param("description", "")
                .param("bonusXp", "-10")
                .param("bonusGold", "-5");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("create-event"))
                .andExpect(model().attributeHasFieldErrors(
                        "eventData",
                        "title",
                        "description",
                        "affectedQuestType",
                        "bonusXp",
                        "bonusGold",
                        "start",
                        "end"
                ));

        verify(eventService, never()).createEvent(any(CreateEventRequest.class));
    }

    @Test
    void createEvent_asUser_shouldReturnForbidden_andStatus403() throws Exception {

        AuthenticationUserDetails user = UserFactory.getUserPrincipal();
        user.setRole(Role.USER);

        MockHttpServletRequestBuilder httpRequest = post("/admin/events/create")
                .with(user(user))
                .with(csrf())
                .param("title", "Double Combat Rewards")
                .param("description", "Combat quests give increased rewards.")
                .param("affectedQuestType", QuestType.COMBAT.name())
                .param("bonusXp", Integer.toString(50))
                .param("bonusGold", Integer.toString(25))
                .param("start", "2026-08-01T10:00")
                .param("end", "2026-08-07T22:00");

        mockMvc.perform(httpRequest)
                .andExpect(status().isForbidden());

        verifyNoInteractions(eventService);
    }






}
