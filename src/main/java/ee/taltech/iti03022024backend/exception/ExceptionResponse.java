package ee.taltech.iti03022024backend.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Exception Response for exception information")
public class ExceptionResponse {
    @Schema(description = "Exception message")
    private final String message;
}
