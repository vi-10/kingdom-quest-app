package app.web.user;

import app.model.dto.user.RegisterDTO;
import app.security.CustomAuthenticationFailureHandler;
import app.service.hero.HeroService;
import app.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getLogin_shouldReturnLoginPage() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void getRegister_shouldReturnRegisterPageWithModel() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerData"));
    }

    @Test
    void registerUser_shouldRegisterAndRedirect_whenRequestIsValid() throws Exception {

        mockMvc.perform(
                        post("/register")
                                .with(csrf())
                                .param("username", "TestUser")
                                .param("password", "Password123")
                                .param("email", "test@example.com")
                                .param("roleplayName", "Arthas")
                                .param("heroClass", "WARRIOR")
                                .param("server", "EUROPE")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).register(any(RegisterDTO.class));
    }

    @Test
    void registerUser_shouldReturnRegisterPage_whenRequestIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/register")
                                .with(csrf())
                                .param("username", "")
                                .param("password", "")
                                .param("email", "invalid-email")
                                .param("roleplayName", "")
                                .param("heroClass", "WARRIOR")
                                .param("server", "EUROPE")
                )
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

}
