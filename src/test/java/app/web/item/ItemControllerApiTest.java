package app.web.item;

import app.model.dto.hero.HeroDTO;
import app.model.dto.item.ItemDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.item.ItemRarity;
import app.security.AuthenticationUserDetails;
import app.security.CustomAuthenticationFailureHandler;
import app.service.hero.HeroService;
import app.service.item.ItemService;
import app.service.quest.QuestService;
import app.util.user.UserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ItemController.class)
public class ItemControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HeroService heroService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Test
    void getForgePage_shouldReturnForgeItemsView_andStatus200() throws Exception {

        AuthenticationUserDetails principal = UserFactory.getUserPrincipal();
        UUID userId = principal.getId();

        HeroDTO hero = HeroDTO.builder()
                .id(UUID.randomUUID())
                .roleplayName("Test Hero")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .build();

        List<ItemDTO> items = List.of(
                ItemDTO.builder()
                        .id(UUID.randomUUID())
                        .name("Iron Sword")
                        .heroClass(HeroClass.WARRIOR)
                        .requiredGold(50)
                        .rarity(ItemRarity.COMMON)
                        .build()
        );

        when(heroService.getByUserId(userId)).thenReturn(hero);
        when(itemService.getAllItems()).thenReturn(items);

        MockHttpServletRequestBuilder request = get("/items/forge")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("forge-items"))
                .andExpect(model().attribute("hero", hero))
                .andExpect(model().attribute("items", items));

        verify(heroService).getByUserId(userId);
        verify(itemService).getAllItems();
    }


}
