package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for search request")
public class CampingRouteSearchRequest {

    @Schema(description = "Search keyword for filtering camping routes", example = "Tallinn")
    @Size(max = 100, message = "Search keyword must not exceed 100 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s.,()-]*$", message = "Search keyword contains invalid characters")
    private String keyword;

    @Schema(description = "Page number (zero-based)", example = "0")
    @Min(value = 0, message = "Page number must not be negative")
    private int pageNumber;

    @Schema(description = "Number of items per page", example = "10")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 20, message = "Page size must not exceed 20")
    private int pageSize;
}
