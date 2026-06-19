package app.model.dto.user;

import app.model.entity.hero.HeroClass;
import app.model.entity.user.Server;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDTO {
    @Size(min = 5, message = "Username must be at least 5 characters")
    private String username;
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;
    @Size(min = 5, message = "Roleplay name must be at least 5 characters")
    private String roleplayName;
    @NotNull(message = "Hero class must not be null.")
    private HeroClass heroClass;
    @NotNull(message = "Server must not be null.")
    private Server server;

}
