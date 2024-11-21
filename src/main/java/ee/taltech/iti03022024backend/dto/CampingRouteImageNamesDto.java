package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Data Transfer Object for camping route image names")
public class CampingRouteImageNamesDto {
    @Schema(description = "List of the camping route image names", example = "[a532adgehpf.png, noe819amoi9.png]")
    private List<String> imageNames;
}
