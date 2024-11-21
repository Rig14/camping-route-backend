package ee.taltech.iti03022024backend.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class VerificationDto {
    private final String token;
}
