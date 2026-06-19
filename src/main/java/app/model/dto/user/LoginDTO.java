package app.model.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginDTO {
    @Size(min = 5, message = "Username must be at least 5 characters")
    private String username;
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;
}
