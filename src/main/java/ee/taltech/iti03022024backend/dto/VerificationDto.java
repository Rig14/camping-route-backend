package ee.taltech.iti03022024backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerificationDto {
    private String username;
    private String password;
}
