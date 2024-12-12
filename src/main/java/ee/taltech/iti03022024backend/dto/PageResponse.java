package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@Schema(description = "Data Transfer Object for page response")
public class PageResponse<T> {
    @Schema(description = "Camping route DTO-s list", example = "[<CampingRouteDTO_1>, <CampingRouteDTO_2>]")
    private final List<T> content;
    @Schema(description = "Total elements form the query response", example = "65")
    private final long totalElements;
    @Schema(description = "Total pages form the query response", example = "4")
    private final int totalPages;
}
