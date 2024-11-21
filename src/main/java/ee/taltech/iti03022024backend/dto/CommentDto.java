package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for camping route comment")
public class CommentDto {
    @Schema(description = "ID for the camping route comment", example = "0")
    private long id;
    @Schema(description = "Content for the camping route comment", example = "Väga äge rada")
    private String content;
}
