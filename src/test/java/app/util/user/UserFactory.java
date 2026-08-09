package app.util.user;

import app.model.dto.user.RegisterDTO;
import app.model.entity.hero.HeroClass;
import app.model.entity.user.Role;
import app.model.entity.user.Server;
import app.model.entity.user.User;
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

    public static User getUser(){
        return User.builder()
                .username("testUser")
                .password("password")
                .email("test@example.com")
                .role(Role.USER)
                .server(Server.EUROPE)
                .isActive(true)
                .profilePicture("/images/warrior.jpeg")
                .build();
    }


}
