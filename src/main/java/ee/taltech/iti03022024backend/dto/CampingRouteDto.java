package ee.taltech.iti03022024backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class CampingRouteDto {
    private long id;
    private String name;
    private String description;
    private String location;
    private String thumbnailUrl;
    private List<CommentDto> comments;
}
