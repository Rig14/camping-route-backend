package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for camping route comment")
public class CommentDto {
    @Schema(description = "ID for the camping route comment", example = "0")
    private long id;

    @NotBlank(message = "Comment content cannot be empty")
    @Size(min = 1, max = 1000, message = "Comment must be between 1 and 1000 characters")
    @Schema(description = "Content for the camping route comment", example = "Väga äge rada")
    private String content;

    @Schema(description = "ID of the comment author")
    private long userID;
}
