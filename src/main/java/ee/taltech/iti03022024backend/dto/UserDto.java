package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for user information")
public class UserDto {
    @Schema(description = "ID of the user", example = "0")
    private long id;

    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the user", example = "Kalamees24")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    @Schema(description = "Email of the user", example = "kala.mees@gmail.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one uppercase letter, " +
                    "one lowercase letter, one number, and one special character")
    @Schema(description = "Password of the user", example = "<some hash>")
    private String password;
}
