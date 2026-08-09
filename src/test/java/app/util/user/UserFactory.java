package app.util.user;

import app.model.dto.user.RegisterDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Server;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserFactory {

    public static RegisterDTO getRegisterDTO(){
        return RegisterDTO.builder()
                .username("testUser")
                .password("password123")
                .email("test@example.com")
                .server(Server.EUROPE)
                .heroClass(HeroClass.WARRIOR)
                .roleplayName("Aragorn")
                .build();
    }
}
