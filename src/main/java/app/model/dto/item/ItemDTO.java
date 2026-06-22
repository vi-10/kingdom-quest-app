package app.model.dto.item;

import app.model.entity.hero.HeroClass;
import app.model.entity.item.ItemRarity;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ItemDTO {

    private UUID id;
    private String name;
    private HeroClass heroClass;
    private int requiredGold;
    private ItemRarity rarity;

}
