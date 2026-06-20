package app.web;

import app.model.dto.user.LoginDTO;
import app.model.dto.user.RegisterDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

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


}
