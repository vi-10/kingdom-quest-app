package app.service.item;

import app.exception.ItemNotFoundException;
import app.exception.QuestNotFoundException;
import app.exception.UserNotFoundException;
import app.mapper.item.ItemMapper;
import app.mapper.quest.QuestMapper;
import app.model.dto.item.ForgeResultDTO;
import app.model.dto.item.ItemDTO;
import app.model.dto.quest.QuestDTO;
import app.model.entity.hero.Hero;
import app.model.entity.heroitem.HeroItem;
import app.model.entity.item.Item;
import app.model.entity.quest.Quest;
import app.repository.hero.HeroRepository;
import app.repository.heroitem.HeroItemRepository;
import app.repository.item.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ItemService {
    private ItemRepository itemRepository;
    private HeroRepository heroRepository;
    private HeroItemRepository heroItemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, HeroRepository heroRepository, HeroItemRepository heroItemRepository) {
        this.itemRepository = itemRepository;
        this.heroRepository = heroRepository;
        this.heroItemRepository = heroItemRepository;
    }

    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream().map(ItemMapper::toItemDTO).toList();
    }

    public ForgeResultDTO forgeItem(UUID itemId, UUID userId) {

        Hero hero = heroRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item not found"));

        if (hero.getHeroClass() != item.getHeroClass()) {
            return ForgeResultDTO.builder().message("Incorrect hero class.").build();
        }

        if (hero.getGold() < item.getRequiredGold()) {
            return ForgeResultDTO.builder().message("Not enough gold.").build();
        }

        hero.setGold(hero.getGold() - item.getRequiredGold());

        HeroItem heroItem = HeroItem.builder().hero(hero).item(item).build();

        heroItemRepository.save(heroItem);
        heroRepository.save(hero);

        return null;
    }
}
