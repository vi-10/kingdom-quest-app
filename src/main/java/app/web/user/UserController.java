package app.web.user;

import app.model.dto.hero.HeroDTO;
import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.security.AuthenticationUserDetails;
import app.service.hero.HeroService;
import app.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

        ModelAndView modelAndView = new ModelAndView("login");

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
    public ModelAndView getDashboard(@AuthenticationPrincipal AuthenticationUserDetails principal) {

        UUID userId = principal.getId();

        UserDTO user = userService.getById(userId);
        HeroDTO hero = heroService.getByUserId(userId);

        ModelAndView modelAndView = new ModelAndView("dashboard");
        modelAndView.addObject("user", user);
        modelAndView.addObject("hero", hero);

        return modelAndView;
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

    @GetMapping("/admin/quests")
    public ModelAndView getQuestAdminPage(){
        return new ModelAndView("quests-administration");
    }

    @GetMapping("/admin/events")
    public ModelAndView getEventAdminPage(){
        return new ModelAndView("events-administration");
    }

    @GetMapping("/edit")
    public ModelAndView getEditPage(){
        EditProfileRequest request = EditProfileRequest.builder().build();
        ModelAndView modelAndView = new ModelAndView("edit");
        modelAndView.addObject("editData", request);
        return modelAndView;
    }

    @PutMapping("/edit")
    public ModelAndView editProfile(@Valid @ModelAttribute("editData") EditProfileRequest request,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal AuthenticationUserDetails principal
                                     ) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("edit");
        }

        userService.editProfile(principal.getId(), request);

        return new ModelAndView("redirect:/dashboard");
    }

}
