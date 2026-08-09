package app.util.item;

import app.model.entity.hero.HeroClass;
import app.model.entity.item.Item;
import app.model.entity.item.ItemRarity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ItemFactory {

    public static Item getItem()
    { return Item.builder()
            .name("Iron Sword")
            .heroClass(HeroClass.WARRIOR)
            .requiredGold(50)
            .rarity(ItemRarity.COMMON)
            .build();
    }
}
