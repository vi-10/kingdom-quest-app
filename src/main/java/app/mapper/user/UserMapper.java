package app.mapper.user;

import app.model.dto.user.UserDTO;
import app.model.entity.user.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class UserMapper {

    public static UserDTO toUserDTO(User user){
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .server(user.getServer())
                .isActive(user.isActive())
                .build();

    }


}
