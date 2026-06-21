package app.model.dto.hero;


import app.model.entity.hero.HeroClass;
import lombok.Builder;
import lombok.Data;
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
