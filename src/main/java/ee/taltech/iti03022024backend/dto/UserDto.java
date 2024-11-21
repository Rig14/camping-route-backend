package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for user information")
public class UserDto {
    @Schema(description = "ID of the user", example = "0")
    private long id;
    @Schema(description = "Username of the user", example = "Kalamees24")
    private String username;
    @Schema(description = "Email of the user", example = "kala.mees@gmail.com")
    private String email;
    @Schema(description = "Password of the user", example = "<some hash>")
    private String password;
}
