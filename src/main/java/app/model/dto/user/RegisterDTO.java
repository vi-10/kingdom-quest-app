package app.model.dto.user;

import app.model.entity.hero.HeroClass;
import app.model.entity.user.Server;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDTO {
    @NotBlank(message = "Username must not be blank.")
    @Size(min = 5, message = "Username must be at least 5 characters")
    private String username;

    @NotBlank(message = "Password must not be blank.")
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;

    @Email(message = "Please enter a valid email.")
    @NotBlank(message = "Email must not be blank.")
    private String email;

    @NotBlank(message = "Roleplay name must not be blank.")
    private String roleplayName;

    @NotNull(message = "Hero class must not be null.")
    private HeroClass heroClass;

    @NotNull(message = "Server must not be null.")
    private Server server;
}
