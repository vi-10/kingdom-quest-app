package app.mapper.hero;

import app.model.dto.hero.HeroDTO;
import app.model.entity.hero.Hero;


public class HeroMapper {

    public static HeroDTO toHeroDTO(Hero hero){
        if (hero == null) {
            return null;
        }

        return HeroDTO.builder()
                .id(hero.getId())
                .roleplayName(hero.getRoleplayName())
                .heroClass(hero.getHeroClass())
                .level(hero.getLevel())
                .xp(hero.getXp())
                .gold(hero.getGold())
                .build();
    }
}
