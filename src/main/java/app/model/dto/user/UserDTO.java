package app.model.dto.user;

import app.model.entity.user.Role;
import app.model.entity.user.Server;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String profilePicture;
    private Role role;
    private Server server;
    private boolean isActive;
}
