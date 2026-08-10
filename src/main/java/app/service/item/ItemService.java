package app.service.item;

import app.exception.HeroNotFoundException;
import app.exception.ItemNotFoundException;
import app.exception.UnauthorizedException;
import app.mapper.item.ItemMapper;
import app.model.dto.heroitem.InventoryItemDTO;
import app.model.dto.item.ForgeResultDTO;
import app.model.dto.item.ItemDTO;
import app.model.entity.hero.Hero;
import app.model.entity.heroitem.HeroItem;
import app.model.entity.item.Item;
import app.repository.hero.HeroRepository;
import app.repository.heroitem.HeroItemRepository;
import app.repository.item.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.debug("Fetching all available items");

        List<ItemDTO> items = itemRepository.findAll().stream().map(ItemMapper::toItemDTO).toList();

        log.debug("Fetched {} available items", items.size());

        return items;
    }

    public ForgeResultDTO forgeItem(UUID itemId, UUID userId) {
        log.info("User {} attempting to forge item with ID {}", userId, itemId);

        Hero hero = heroRepository.findByUserId(userId)
                .orElseThrow(HeroNotFoundException::new);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        if (hero.getHeroClass() != item.getHeroClass()) {
            log.debug("User {} cannot forge item '{}' because hero class {} does not match required class {}",
                    userId, item.getName(), hero.getHeroClass(), item.getHeroClass());

            return ForgeResultDTO.builder()
                    .message(String.format("A %s can't forge this item.", hero.getHeroClass().name().toLowerCase()))
                    .build();
        }

        if (hero.getGold() < item.getRequiredGold()) {
            log.debug("User {} cannot forge item '{}' due to insufficient gold. Required: {}, available: {}",
                    userId, item.getName(), item.getRequiredGold(), hero.getGold());

            return ForgeResultDTO.builder().message("Not enough gold.").build();
        }

        hero.setGold(hero.getGold() - item.getRequiredGold());

        HeroItem heroItem = HeroItem.builder().hero(hero).item(item).build();

        hero.getItems().add(heroItem);

        heroItemRepository.save(heroItem);
        heroRepository.save(hero);

        log.info("User {} successfully forged item '{}'. Gold spent: {}",
                userId, item.getName(), item.getRequiredGold());

        return null;
    }

    public List<InventoryItemDTO> getInventory(UUID userId) {
        log.debug("Fetching inventory for user {}", userId);

        Hero hero = heroRepository.findByUserId(userId)
                .orElseThrow(HeroNotFoundException::new);

        List<InventoryItemDTO> inventory =  heroItemRepository.findByHeroId(hero.getId())
                .stream()
                .map(heroItem -> InventoryItemDTO.builder()
                        .heroItemId(heroItem.getId())
                        .name(heroItem.getItem().getName())
                        .rarity(heroItem.getItem().getRarity())
                        .build())
                .toList();

        log.debug("Fetched {} inventory item(s) for user {}", inventory.size(), userId);

        return inventory;
    }

    public void dropItem(UUID heroItemId, UUID userId) {
        log.info("User {} attempting to drop item with ID {}", userId, heroItemId);

        Hero hero = heroRepository.findByUserId(userId)
                .orElseThrow(HeroNotFoundException::new);

        HeroItem heroItem = heroItemRepository.findById(heroItemId)
                .orElseThrow(ItemNotFoundException::new);

        if (!heroItem.getHero().getId().equals(hero.getId())) {
            log.warn("User {} attempted to drop item {} that they do not own", userId, heroItemId);

            throw new UnauthorizedException("You cannot drop an item that you do not own.");
        }

        heroItemRepository.delete(heroItem);

        log.info("User {} successfully dropped item '{}'", userId, heroItem.getItem().getName());
    }
}
