package app.service.item;

import app.model.dto.item.ItemDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.item.Item;
import app.model.entity.item.ItemRarity;
import app.repository.hero.HeroRepository;
import app.repository.heroitem.HeroItemRepository;
import app.repository.item.ItemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static app.util.item.ItemFactory.getItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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


}
