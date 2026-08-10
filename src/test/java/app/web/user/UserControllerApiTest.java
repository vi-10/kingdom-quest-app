package app.web.user;

import app.model.dto.hero.HeroDTO;
import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.Server;
import app.security.AuthenticationUserDetails;
import app.security.CustomAuthenticationFailureHandler;
import app.service.hero.HeroService;
import app.service.user.UserService;
import app.util.user.UserFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static app.util.user.UserFactory.getAdminUser;
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

    @Test
    void getAllUsers_whenUserIsAdmin_shouldReturnUsersView_andStatus200() throws Exception {

        List<UserDTO> users = List.of(
                UserDTO.builder()
                        .id(UUID.randomUUID())
                        .username("User1")
                        .email("user1@test.com")
                        .role(Role.USER)
                        .build()
        );

        when(userService.getAllUsers()).thenReturn(users);

        MockHttpServletRequestBuilder request = get("/admin/users")
                .with(user(getAdminUser()));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attribute("users", users));
    }

    @Test
    void switchRole_shouldRedirectToUsers_andStatus302() throws Exception {

        UUID userId = UUID.randomUUID();

        MockHttpServletRequestBuilder request = put("/admin/users/{id}/role", userId)
                .with(user(getAdminUser()))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/admin/users"));

        verify(userService).switchRole(userId);
    }

    @Test
    void switchStatus_shouldRedirectToUsers_andStatus302() throws Exception {

        UUID userId = UUID.randomUUID();

        MockHttpServletRequestBuilder request = put("/admin/users/{id}/status", userId)
                .with(user(getAdminUser()))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/admin/users"));

        verify(userService).switchStatus(userId);
    }

    @Test
    void getEditPage_shouldReturnEditView_andStatus200() throws Exception {

        MockHttpServletRequestBuilder request = get("/edit")
                .with(user(UserFactory.getUserPrincipal()));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("editData"));
    }

    @Test
    void editProfile_shouldRedirectToDashboard_andStatus302() throws Exception {

        AuthenticationUserDetails principal = UserFactory.getUserPrincipal();

        MockHttpServletRequestBuilder request = put("/edit")
                .with(user(principal))
                .with(csrf())
                .param("username", "NewUsername")
                .param("email", "new@email.com")
                .param("profilePicture", "")
                .param("roleplayName", "NewHeroName");

        mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/dashboard"));

        verify(userService).editProfile(eq(principal.getId()), any(EditProfileRequest.class));
    }

    @Test
    void editProfile_withInvalidData_shouldReturnEditView_andStatus200() throws Exception {

        MockHttpServletRequestBuilder request = put("/edit")
                .with(user(UserFactory.getUserPrincipal()))
                .with(csrf())
                .param("username", "")
                .param("email", "")
                .param("roleplayName", "");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeHasFieldErrors(
                        "editData",
                        "username",
                        "email",
                        "roleplayName"
                ));

        verify(userService, never()).editProfile(any(), any());
    }


}
