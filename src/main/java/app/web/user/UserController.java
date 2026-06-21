package app.web.user;

import app.model.dto.hero.HeroDTO;
import app.model.dto.user.LoginDTO;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.user.Role;
import app.service.hero.HeroService;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class UserController {
    private UserService userService;
    private HeroService heroService;

    @Autowired
    public UserController(UserService userService, HeroService heroService) {
        this.userService = userService;
        this.heroService = heroService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLogin(){
        LoginDTO login = LoginDTO.builder().build();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginData", login);
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegister(){
        RegisterDTO register = RegisterDTO.builder().build();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerData", register);
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("loginData") LoginDTO loginData,
                              BindingResult bindingResult,
                              HttpSession session) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("login");
        }

        UserDTO user = userService.login(loginData);

        session.setAttribute("userId", user.getId());

        return new ModelAndView("redirect:/dashboard");
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid @ModelAttribute("registerData") RegisterDTO registerData,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerData);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/dashboard")
    public ModelAndView getDashboard(HttpSession httpSession) {

        UUID userId = (UUID) httpSession.getAttribute("userId");

        UserDTO user = userService.getById(userId);
        HeroDTO hero = heroService.getByUserId(userId);

        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", user);
        modelAndView.addObject("hero", hero);

        return modelAndView;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/admin/users")
    public ModelAndView getAllUsers() {

        List<UserDTO> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView("users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    @PutMapping("/admin/users/{id}/role")
    public ModelAndView switchRole(@PathVariable UUID id) {
        userService.switchRole(id);
        return new ModelAndView("redirect:/admin/users");
    }

    @PutMapping("/admin/users/{id}/status")
    public ModelAndView switchStatus(@PathVariable UUID id) {
        userService.switchStatus(id);
        return new ModelAndView("redirect:/admin/users");
    }

}
