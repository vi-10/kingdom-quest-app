package app.model.dto.heroitem;

import app.model.dto.hero.HeroDTO;
import app.model.dto.item.ItemDTO;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class HeroItemDTO {
    private UUID id;
    private HeroDTO hero;
    private ItemDTO item;
}
