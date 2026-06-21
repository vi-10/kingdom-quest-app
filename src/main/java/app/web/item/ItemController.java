package app.web.item;

import app.model.dto.hero.HeroDTO;
import app.model.dto.item.ForgeResultDTO;
import app.model.dto.item.ItemDTO;
import app.service.hero.HeroService;
import app.service.item.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class ItemController {
    private HeroService heroService;
    private ItemService itemService;

    @Autowired
    public ItemController(HeroService heroService, ItemService itemService) {
        this.heroService = heroService;
        this.itemService = itemService;
    }

    @GetMapping("/items/forge")
    public ModelAndView getForgePage(HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");

        HeroDTO hero = heroService.getByUserId(userId);

        List<ItemDTO> items = itemService.getAllItems();

        ModelAndView modelAndView = new ModelAndView("forge-items");
        modelAndView.addObject("hero", hero);
        modelAndView.addObject("items", items);

        return modelAndView;
    }

    @PostMapping("/items/{id}/forge")
    public ModelAndView forgeItem(@PathVariable UUID id, HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");

        ForgeResultDTO forgeResult = itemService.forgeItem(id, userId);

        HeroDTO hero = heroService.getByUserId(userId);

        List<ItemDTO> items = itemService.getAllItems();

        ModelAndView modelAndView = new ModelAndView("forge-items");
        modelAndView.addObject("hero", hero);
        modelAndView.addObject("items", items);
        modelAndView.addObject("forgeResult", forgeResult);

        return modelAndView;
    }
}
