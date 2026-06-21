package app.web.quest;

import app.model.dto.hero.HeroDTO;
import app.model.dto.quest.QuestDTO;
import app.model.dto.user.UserDTO;
import app.service.hero.HeroService;
import app.service.quest.QuestService;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class QuestController {
    private UserService userService;
    private HeroService heroService;
    private QuestService questService;

    @Autowired
    public QuestController(UserService userService, HeroService heroService, QuestService questService) {
        this.userService = userService;
        this.heroService = heroService;
        this.questService = questService;
    }

    @GetMapping("/quests")
    public ModelAndView getQuests(HttpSession session){

        UUID userId = (UUID) session.getAttribute("userId");

        if (userId == null) {
            return new ModelAndView("redirect:/login");
        }

        UserDTO user = userService.getById(userId);
        HeroDTO hero = heroService.getByUserId(userId);

        List<QuestDTO> quests = questService.getAllQuests();

        ModelAndView modelAndView = new ModelAndView("available-quests");
        modelAndView.addObject("user", user);
        modelAndView.addObject("hero", hero);
        modelAndView.addObject("quests", quests);

        return modelAndView;
    }
}
