package app.model.dto.hero;

import app.model.dto.heroitem.HeroItemDTO;
import app.model.dto.item.ItemDTO;
import app.model.dto.user.UserDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.item.Item;
import app.model.entity.user.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class HeroDTO {

    private UUID id;
    private String roleplayName;
    private HeroClass heroClass;
    private int level;
    private int xp;
    private int gold;


}
