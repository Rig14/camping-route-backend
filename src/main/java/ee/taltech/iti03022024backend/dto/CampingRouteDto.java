package ee.taltech.iti03022024backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for camping route information")
public class CampingRouteDto {
    @Schema(description = "ID of the camping route", example = "0")
    private long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Schema(description = "Name of the camping route", example = "Viru raba")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    @Schema(description = "Description of the camping route", example = "Kaunis teekond läbi looduse")
    private String description;

    @NotBlank(message = "Location is required")
    @Size(min = 3, max = 100, message = "Location must be between 3 and 100 characters")
    @Schema(description = "Location of the camping route", example = "Kuusalu vald, Harjumaa")
    private String location;

    @Schema(description = "Thumbnail URL of the camping route",
            example = "https://media.voog.com/0000/0030/9870/photos/viru_raba_loodusrada_1_medium.jpg")
    private String thumbnailUrl;

    @Schema(description = "ID of Camping Route author")
    private long userID;
}
