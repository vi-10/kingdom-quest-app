package app.mapper.item;

import app.model.dto.item.ItemDTO;
import app.model.entity.item.Item;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class ItemMapper {
    public static ItemDTO toItemDTO(Item item){
        if (item == null) {
            return null;
        }

        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .heroClass(item.getHeroClass())
                .requiredGold(item.getRequiredGold())
                .rarity(item.getRarity())
                .build();
    }
}
