package app.web.quest;

import app.model.dto.hero.HeroDTO;
import app.model.dto.quest.CreateQuestDTO;
import app.model.dto.quest.EditQuestDTO;
import app.model.dto.quest.QuestDTO;
import app.model.dto.quest.QuestResultDTO;
import app.service.hero.HeroService;
import app.service.quest.QuestService;
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
public class QuestController {
    private HeroService heroService;
    private QuestService questService;

    @Autowired
    public QuestController(HeroService heroService, QuestService questService) {
        this.heroService = heroService;
        this.questService = questService;
    }

    @GetMapping("/quests")
    public ModelAndView getQuests(HttpSession session){

        UUID userId = (UUID) session.getAttribute("userId");

        HeroDTO hero = heroService.getByUserId(userId);

        List<QuestDTO> quests = questService.getAllQuests();

        ModelAndView modelAndView = new ModelAndView("available-quests");
        modelAndView.addObject("hero", hero);
        modelAndView.addObject("quests", quests);

        return modelAndView;
    }

    @PostMapping("/quests/{id}/complete")
    public ModelAndView completeQuest(@PathVariable UUID id,
                                      HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");

        QuestResultDTO result = questService.completeQuest(id, userId);

        ModelAndView modelAndView = new ModelAndView("quest-result");
        modelAndView.addObject("result", result);

        return modelAndView;
    }

    @GetMapping("/admin/quests/create")
    public ModelAndView getCreateQuestPage() {

        ModelAndView modelAndView = new ModelAndView("create-quest");
        CreateQuestDTO questData = CreateQuestDTO.builder().build();
        modelAndView.addObject("questData", questData);

        return modelAndView;
    }

    @PostMapping("/admin/quests/create")
    public ModelAndView createQuest(
            @Valid @ModelAttribute("questData") CreateQuestDTO questData,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("create-quest");
        }

        questService.createQuest(questData);

        return new ModelAndView("redirect:/admin/quests");
    }

    @GetMapping("/admin/quests/edit")
    public ModelAndView getEditQuestPage() {

        ModelAndView modelAndView = new ModelAndView("edit-quest");
        EditQuestDTO questData = EditQuestDTO.builder().build();
        modelAndView.addObject("questData", questData);
        modelAndView.addObject("quests", questService.getAllQuests());

        return modelAndView;
    }

    @PutMapping("/admin/quests/edit")
    public ModelAndView editQuest(@Valid @ModelAttribute("questData") EditQuestDTO questData,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            ModelAndView modelAndView = new ModelAndView("edit-quest");
            modelAndView.addObject("quests", questService.getAllQuests());

            return modelAndView;
        }

        questService.editQuest(questData);

        return new ModelAndView("redirect:/admin/quests");
    }

    @GetMapping("/admin/quests/delete")
    public ModelAndView getDeleteQuestPage() {

        ModelAndView modelAndView = new ModelAndView("delete-quest");
        List<QuestDTO> quests = questService.getAllQuests();
        modelAndView.addObject("quests", quests);
        modelAndView.addObject("noQuests", quests.isEmpty());

        return modelAndView;
    }

    @DeleteMapping("/admin/quests/delete")
    public ModelAndView deleteQuest(@RequestParam UUID questId) {

        questService.deleteQuest(questId);

        return new ModelAndView("redirect:/admin/quests");
    }
}
