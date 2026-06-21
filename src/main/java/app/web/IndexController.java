package app.web;

import app.model.dto.user.LoginDTO;
import app.model.dto.user.RegisterDTO;
import app.model.dto.user.UserDTO;
import app.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
    private UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
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
}
