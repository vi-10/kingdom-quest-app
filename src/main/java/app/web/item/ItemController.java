package app.web.item;

import app.model.dto.hero.HeroDTO;
import app.model.dto.item.ForgeResultDTO;
import app.model.dto.item.ItemDTO;
import app.security.AuthenticationUserDetails;
import app.service.hero.HeroService;
import app.service.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/items")
public class ItemController {
    private HeroService heroService;
    private ItemService itemService;

    @Autowired
    public ItemController(HeroService heroService, ItemService itemService) {
        this.heroService = heroService;
        this.itemService = itemService;
    }

    @GetMapping("/forge")
    public ModelAndView getForgePage(@AuthenticationPrincipal AuthenticationUserDetails principal) {

        UUID userId = principal.getId();

        HeroDTO hero = heroService.getByUserId(userId);

        List<ItemDTO> items = itemService.getAllItems();

        ModelAndView modelAndView = new ModelAndView("forge-items");
        modelAndView.addObject("hero", hero);
        modelAndView.addObject("items", items);

        return modelAndView;
    }

    @PostMapping("/{id}/forge")
    public ModelAndView forgeItem(@PathVariable UUID id,
                                  @AuthenticationPrincipal AuthenticationUserDetails principal) {

        UUID userId = principal.getId();

        ForgeResultDTO forgeResult = itemService.forgeItem(id, userId);

        HeroDTO hero = heroService.getByUserId(userId);

        List<ItemDTO> items = itemService.getAllItems();

        ModelAndView modelAndView = new ModelAndView("forge-items");
        modelAndView.addObject("hero", hero);
        modelAndView.addObject("items", items);
        modelAndView.addObject("forgeResult", forgeResult);

        return modelAndView;
    }

    @GetMapping("/inventory")
    public ModelAndView getInventory(@AuthenticationPrincipal AuthenticationUserDetails principal) {

        UUID userId = principal.getId();

        List<ItemDTO> items = itemService.getInventory(userId);

        ModelAndView modelAndView = new ModelAndView("inventory");
        modelAndView.addObject("items", items);

        return modelAndView;
    }
}
