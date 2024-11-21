package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Data Transfer Object for authentication token")
public class VerificationDto {
    @Schema(description = "Authentication token", example = "<some hash>")
    private final String token;
}
