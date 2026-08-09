package app.web.user;

import app.model.dto.hero.HeroDTO;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.Server;
import app.security.AuthenticationUserDetails;
import app.security.CustomAuthenticationFailureHandler;
import app.service.hero.HeroService;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.UUID;

import static app.util.user.UserFactory.getUserPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
public class UserControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private HeroService heroService;

    @MockitoBean
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Test
    void index_shouldReturnIndexPage() throws Exception {

        MockHttpServletRequestBuilder request = get("/");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getLogin_shouldReturnLoginPage() throws Exception {

        MockHttpServletRequestBuilder request = get("/login");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void getRegister_shouldReturnRegisterPageWithModel() throws Exception {

        MockHttpServletRequestBuilder request = get("/register");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerData"));
    }

    @Test
    void registerUser_shouldRegisterAndRedirect_whenRequestIsValid() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .with(csrf())
                .param("username", "TestUser")
                .param("password", "Password123")
                .param("email", "test@example.com")
                .param("roleplayName", "Arthas")
                .param("heroClass", "WARRIOR")
                .param("server", "EUROPE");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).register(any(RegisterDTO.class));
    }

    @Test
    void registerUser_shouldReturnRegisterPage_whenRequestIsInvalid() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .with(csrf())
                .param("username", "")
                .param("password", "")
                .param("email", "invalid-email")
                .param("roleplayName", "")
                .param("heroClass", "WARRIOR")
                .param("server", "EUROPE");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors(
                        "registerData",
                        "username",
                        "password",
                        "email",
                        "roleplayName"
                ));

        verify(userService, never()).register(any(RegisterDTO.class));
    }

    @Test
    void getDashboard_shouldReturnDashboardView_andStatus200() throws Exception {

        UserDTO user = UserDTO.builder()
                .id(UUID.randomUUID())
                .username("TestUser")
                .email("test@test.com")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(true)
                .build();

        HeroDTO hero = HeroDTO.builder()
                .id(UUID.randomUUID())
                .roleplayName("Arthas")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .build();

        when(userService.getById(any())).thenReturn(user);
        when(heroService.getByUserId(any())).thenReturn(hero);

        MockHttpServletRequestBuilder request = get("/dashboard")
                .with(user(getUserPrincipal()));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("hero", hero));
    }


}
