package app.init;

import app.model.entity.hero.HeroClass;
import app.model.entity.item.Item;
import app.model.entity.item.ItemRarity;
import app.repository.item.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ItemInit implements CommandLineRunner {
    private ItemRepository itemRepository;

    @Autowired
    public ItemInit(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(itemRepository.count() > 0){
            return;
        }

        Item i1 = Item.builder()
                .name("Rusty Iron Sword")
                .heroClass(HeroClass.WARRIOR)
                .requiredGold(20)
                .rarity(ItemRarity.COMMON)
                .build();

        Item i2 = Item.builder()
                .name("Apprentice Wand")
                .heroClass(HeroClass.MAGE)
                .requiredGold(25)
                .rarity(ItemRarity.COMMON)
                .build();

        Item i3 = Item.builder()
                .name("Leather Dagger")
                .heroClass(HeroClass.ROGUE)
                .requiredGold(25)
                .rarity(ItemRarity.COMMON)
                .build();

        Item i4 = Item.builder()
                .name("Bandages")
                .heroClass(HeroClass.HEALER)
                .requiredGold(20)
                .rarity(ItemRarity.COMMON)
                .build();

        Item i5 = Item.builder()
                .name("Steel Blade")
                .heroClass(HeroClass.WARRIOR)
                .requiredGold(80)
                .rarity(ItemRarity.RARE)
                .build();

        Item i6 = Item.builder()
                .name("Arcane spellbook")
                .heroClass(HeroClass.MAGE)
                .requiredGold(90)
                .rarity(ItemRarity.RARE)
                .build();

        Item i7 = Item.builder()
                .name("Smoke bomb")
                .heroClass(HeroClass.ROGUE)
                .requiredGold(85)
                .rarity(ItemRarity.RARE)
                .build();

        Item i8 = Item.builder()
                .name("Panacea")
                .heroClass(HeroClass.HEALER)
                .requiredGold(80)
                .rarity(ItemRarity.RARE)
                .build();

        itemRepository.saveAll(List.of(i1, i2, i3, i4, i5, i6, i7, i8));
        log.info("Default items created");
    }
}
