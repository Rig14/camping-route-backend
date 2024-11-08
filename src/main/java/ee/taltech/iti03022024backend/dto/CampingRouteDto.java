package ee.taltech.iti03022024backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CampingRouteDto {
    private long id;
    private String name;
    private String description;
    private String location;
    private String thumbnailUrl;
}
