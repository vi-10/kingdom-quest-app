package app.service.item;

import app.exception.HeroNotFoundException;
import app.exception.ItemNotFoundException;
import app.model.dto.heroitem.InventoryItemDTO;
import app.model.dto.item.ForgeResultDTO;
import app.model.dto.item.ItemDTO;
import app.model.entity.hero.Hero;
import app.model.entity.hero.HeroClass;
import app.model.entity.heroitem.HeroItem;
import app.model.entity.item.Item;
import app.model.entity.item.ItemRarity;
import app.model.entity.user.User;
import app.repository.hero.HeroRepository;
import app.repository.heroitem.HeroItemRepository;
import app.repository.item.ItemRepository;
import app.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static app.util.item.ItemFactory.getItem;
import static app.util.user.UserFactory.getUser;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceItTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HeroItemRepository heroItemRepository;

    @Test
    void getAllItems_shouldReturnAllItems() {

        Item item1 = getItem();

        Item item2 = Item.builder()
                .name("Apprentice wand")
                .heroClass(HeroClass.MAGE)
                .requiredGold(40)
                .rarity(ItemRarity.COMMON)
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<ItemDTO> result = itemService.getAllItems();

        assertFalse(result.isEmpty());

        ItemDTO returnedItem1 = result.stream()
                .filter(i -> i.getId().equals(item1.getId()))
                .findFirst()
                .orElseThrow();

        ItemDTO returnedItem2 = result.stream()
                .filter(i -> i.getId().equals(item2.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals("Iron Sword", returnedItem1.getName());
        assertEquals(HeroClass.WARRIOR, returnedItem1.getHeroClass());
        assertEquals(50, returnedItem1.getRequiredGold());
        assertEquals(ItemRarity.COMMON, returnedItem1.getRarity());

        assertEquals("Apprentice wand", returnedItem2.getName());
        assertEquals(HeroClass.MAGE, returnedItem2.getHeroClass());
        assertEquals(40, returnedItem2.getRequiredGold());
        assertEquals(ItemRarity.COMMON, returnedItem2.getRarity());
    }

    @Test
    void forgeItem_shouldForgeItemAndDeductGold() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .items(new ArrayList<>())
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        Item item = getItem();

        itemRepository.save(item);

        ForgeResultDTO result = itemService.forgeItem(item.getId(), user.getId());

        assertNull(result);

        Hero updatedHero = heroRepository.findById(hero.getId()).orElseThrow();

        assertEquals(50, updatedHero.getGold());

        assertEquals(
                1,
                heroItemRepository.findByHeroId(hero.getId()).size()
        );

        HeroItem heroItem =
                heroItemRepository.findByHeroId(hero.getId()).get(0);

        assertEquals(item.getId(), heroItem.getItem().getId());
        assertEquals(hero.getId(), heroItem.getHero().getId());
    }

    @Test
    void forgeItem_shouldReturnMessage_whenHeroClassDoesNotMatch() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .items(new ArrayList<>())
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        Item item = Item.builder()
                .name("Magic Staff")
                .heroClass(HeroClass.MAGE)
                .requiredGold(40)
                .rarity(ItemRarity.COMMON)
                .build();

        itemRepository.save(item);

        ForgeResultDTO result = itemService.forgeItem(item.getId(), user.getId());

        assertEquals(
                "A warrior can't forge this item.",
                result.getMessage()
        );

        Hero unchangedHero =
                heroRepository.findById(hero.getId()).orElseThrow();

        assertEquals(100, unchangedHero.getGold());

        assertTrue(
                heroItemRepository.findByHeroId(hero.getId()).isEmpty()
        );
    }

    @Test
    void forgeItem_shouldReturnMessage_whenGoldIsInsufficient() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(20)
                .user(user)
                .items(new ArrayList<>())
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        Item item = Item.builder()
                .name("Expensive Sword")
                .heroClass(HeroClass.WARRIOR)
                .requiredGold(50)
                .rarity(ItemRarity.RARE)
                .build();

        itemRepository.save(item);

        ForgeResultDTO result =
                itemService.forgeItem(item.getId(), user.getId());

        assertEquals(
                "Not enough gold.",
                result.getMessage()
        );

        Hero unchangedHero =
                heroRepository.findById(hero.getId()).orElseThrow();

        assertEquals(20, unchangedHero.getGold());

        assertTrue(
                heroItemRepository.findByHeroId(hero.getId()).isEmpty()
        );
    }

    @Test
    void forgeItem_shouldThrow_whenItemDoesNotExist() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        assertThrows(
                ItemNotFoundException.class,
                () -> itemService.forgeItem(
                        UUID.randomUUID(),
                        user.getId()
                )
        );
    }

    @Test
    void forgeItem_shouldThrow_whenHeroDoesNotExist() {

        Item item = Item.builder()
                .name("Iron Sword")
                .heroClass(HeroClass.WARRIOR)
                .requiredGold(40)
                .rarity(ItemRarity.COMMON)
                .build();

        itemRepository.save(item);

        assertThrows(
                HeroNotFoundException.class,
                () -> itemService.forgeItem(
                        item.getId(),
                        UUID.randomUUID()
                )
        );
    }

    @Test
    void getInventory_shouldReturnHeroItems() {

        User user = getUser();

        Hero hero = Hero.builder()
                .roleplayName("Warrior")
                .heroClass(HeroClass.WARRIOR)
                .level(1)
                .xp(0)
                .gold(100)
                .user(user)
                .build();

        user.setHero(hero);

        userRepository.save(user);
        heroRepository.save(hero);

        Item item = Item.builder()
                .name("Iron Sword")
                .heroClass(HeroClass.WARRIOR)
                .requiredGold(40)
                .rarity(ItemRarity.COMMON)
                .build();

        itemRepository.save(item);

        HeroItem heroItem = HeroItem.builder()
                .hero(hero)
                .item(item)
                .build();

        heroItemRepository.save(heroItem);

        List<InventoryItemDTO> inventory = itemService.getInventory(user.getId());

        assertEquals(1, inventory.size());

        InventoryItemDTO inventoryItem = inventory.get(0);

        assertEquals(heroItem.getId(), inventoryItem.getHeroItemId());
        assertEquals("Iron Sword", inventoryItem.getName());
        assertEquals(ItemRarity.COMMON, inventoryItem.getRarity());
    }

    @Test
    void getInventory_shouldThrow_whenHeroDoesNotExist() {

        assertThrows(
                HeroNotFoundException.class,
                () -> itemService.getInventory(UUID.randomUUID())
        );
    }


}
