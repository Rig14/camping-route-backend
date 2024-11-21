package ee.taltech.iti03022024backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class PageResponse<CampingRouteDto> {
    private List<CampingRouteDto> content;
    private long totalElements;
    private int totalPages;

    public PageResponse(List<CampingRouteDto> content, long totalElements, int totalPages) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
