package app.web.quest;

import app.model.dto.hero.HeroDTO;
import app.model.dto.quest.CreateQuestDTO;
import app.model.dto.quest.QuestDTO;
import app.model.dto.quest.QuestResultDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.quest.QuestType;
import app.model.entity.user.Role;
import app.security.AuthenticationUserDetails;
import app.security.CustomAuthenticationFailureHandler;
import app.service.hero.HeroService;
import app.service.quest.QuestService;
import app.util.user.UserFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static app.util.user.UserFactory.getUserPrincipal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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

    @Test
    void getQuests_shouldReturnAvailableQuestsView_andStatus200() throws Exception {

        AuthenticationUserDetails principal = getUserPrincipal();
        UUID userId = principal.getId();

        HeroDTO hero = HeroDTO.builder()
                .id(UUID.randomUUID())
                .roleplayName("Test Hero")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .build();

        List<QuestDTO> quests = List.of(
                QuestDTO.builder()
                        .id(UUID.randomUUID())
                        .title("Defeat the Goblins")
                        .description("Clear the nearby forest.")
                        .questType(QuestType.COMBAT)
                        .requiredLevel(1)
                        .rewardXp(50)
                        .rewardGold(25)
                        .build()
        );

        when(heroService.getByUserId(userId)).thenReturn(hero);
        when(questService.getAllQuests()).thenReturn(quests);

        MockHttpServletRequestBuilder request = get("/quests")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("available-quests"))
                .andExpect(model().attribute("hero", hero))
                .andExpect(model().attribute("quests", quests));

        verify(heroService).getByUserId(userId);
        verify(questService).getAllQuests();
    }

    @Test
    void completeQuest_shouldReturnQuestResultView_andStatus200() throws Exception {

        AuthenticationUserDetails principal = getUserPrincipal();
        UUID userId = principal.getId();
        UUID questId = UUID.randomUUID();

        QuestResultDTO result = QuestResultDTO.builder()
                .success(true)
                .message("You earned 50 XP and 25 gold!")
                .build();

        when(questService.completeQuest(questId, userId))
                .thenReturn(result);

        MockHttpServletRequestBuilder request = post("/quests/{id}/complete", questId)
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("quest-result"))
                .andExpect(model().attribute("result", result));

        verify(questService).completeQuest(questId, userId);
    }

    @Test
    void completeQuest_whenQuestCannotBeCompleted_shouldReturnQuestResultView() throws Exception {

        AuthenticationUserDetails principal = getUserPrincipal();
        UUID userId = principal.getId();
        UUID questId = UUID.randomUUID();

        QuestResultDTO result = QuestResultDTO.builder()
                .success(false)
                .message("Your hero class cannot complete this quest.")
                .build();

        when(questService.completeQuest(questId, userId))
                .thenReturn(result);

        MockHttpServletRequestBuilder request = post("/quests/{id}/complete", questId)
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("quest-result"))
                .andExpect(model().attribute("result", result));

        verify(questService).completeQuest(questId, userId);
    }

    @Test
    void getCreateQuestPage_asAdmin_shouldReturnCreateQuestView_andStatus200() throws Exception {

        AuthenticationUserDetails principal = UserFactory.getAdminUser();

        MockHttpServletRequestBuilder request = get("/admin/quests/create")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("create-quest"))
                .andExpect(model().attributeExists("questData"));
    }

    @Test
    void getCreateQuestPage_asUser_shouldReturnForbidden() throws Exception {

        AuthenticationUserDetails principal = UserFactory.getUserPrincipal();

        principal.setRole(Role.USER);

        MockHttpServletRequestBuilder request = get("/admin/quests/create")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        verifyNoInteractions(questService);
    }

    @Test
    void createQuest_withValidData_asAdmin_shouldRedirectToAdminQuests_andStatus302() throws Exception {

        AuthenticationUserDetails admin = UserFactory.getAdminUser();

        MockHttpServletRequestBuilder request = post("/admin/quests/create")
                .with(user(admin))
                .with(csrf())
                .param("title", "Defeat the Goblins")
                .param("description", "Clear the nearby forest.")
                .param("requiredLevel", "1")
                .param("rewardXp", "50")
                .param("rewardGold", "25")
                .param("questType", "COMBAT");

        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/admin/quests"));

        ArgumentCaptor<CreateQuestDTO> captor =
                ArgumentCaptor.forClass(CreateQuestDTO.class);

        verify(questService).createQuest(captor.capture());

        CreateQuestDTO questData = captor.getValue();

        assertEquals("Defeat the Goblins", questData.getTitle());
        assertEquals("Clear the nearby forest.", questData.getDescription());
        assertEquals(1, questData.getRequiredLevel());
        assertEquals(50, questData.getRewardXp());
        assertEquals(25, questData.getRewardGold());
        assertEquals(QuestType.COMBAT, questData.getQuestType());
    }

    @Test
    void createQuest_withInvalidData_asAdmin_shouldReturnCreateQuestView_andStatus200() throws Exception {

        AuthenticationUserDetails admin = UserFactory.getAdminUser();

        MockHttpServletRequestBuilder request = post("/admin/quests/create")
                .with(user(admin))
                .with(csrf())
                .param("title", "")
                .param("description", "")
                .param("requiredLevel", "0")
                .param("rewardXp", "-10")
                .param("rewardGold", "-5")
                .param("questType", "");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("create-quest"))
                .andExpect(model().attributeExists("questData"))
                .andExpect(model().attributeHasFieldErrors("questData", "title"))
                .andExpect(model().attributeHasFieldErrors("questData", "description"))
                .andExpect(model().attributeHasFieldErrors("questData", "requiredLevel"))
                .andExpect(model().attributeHasFieldErrors("questData", "rewardXp"))
                .andExpect(model().attributeHasFieldErrors("questData", "rewardGold"))
                .andExpect(model().attributeHasFieldErrors("questData", "questType"));

        verifyNoInteractions(questService);
    }

    @Test
    void createQuest_asUser_shouldReturnForbidden_andStatus403() throws Exception {

        AuthenticationUserDetails user = UserFactory.getUserPrincipal();
        user.setRole(Role.USER);

        MockHttpServletRequestBuilder request = post("/admin/quests/create")
                .with(user(user))
                .with(csrf())
                .param("title", "Defeat the Goblins")
                .param("description", "Clear the nearby forest.")
                .param("requiredLevel", "1")
                .param("rewardXp", "50")
                .param("rewardGold", "25")
                .param("questType", "COMBAT");

        mockMvc.perform(request)
                .andExpect(status().isForbidden());

        verifyNoInteractions(questService);
    }
}
