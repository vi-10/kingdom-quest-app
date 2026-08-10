package app.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditProfileRequest {

    @NotBlank(message = "Username must not be blank.")
    private String username;

    @NotBlank(message = "Email must not be blank.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    private String profilePicture;

    @NotBlank(message = "Roleplay name must not be blank.")
    private String roleplayName;
}
