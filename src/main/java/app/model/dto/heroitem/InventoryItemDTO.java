package app.model.dto.heroitem;

import app.model.entity.item.ItemRarity;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class InventoryItemDTO {
    private UUID heroItemId;
    private String name;
    private ItemRarity rarity;
}
