package ee.taltech.iti03022024backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String password;
    private List<CampingRouteDto> campingRoutes;
    private List<CommentDto> comments;
}
