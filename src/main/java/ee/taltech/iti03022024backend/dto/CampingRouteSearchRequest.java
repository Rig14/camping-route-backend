package ee.taltech.iti03022024backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CampingRouteSearchRequest {
    private String keyword;
    private int pageNumber;
    private int pageSize;
}
