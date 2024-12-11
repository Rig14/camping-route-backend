package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Data Transfer Object for camping route views")
public class ViewDto {
    @Schema(description = "ID of the camping route.", example = "14")
    private final long campingRouteId;
    @Schema(description = "View count of the camping route. now many times this camping route has been viewed", example = "142")
    private final long viewsCount;
}
