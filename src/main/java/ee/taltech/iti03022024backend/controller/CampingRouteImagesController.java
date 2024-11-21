package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.CampingRouteImageNamesDto;
import ee.taltech.iti03022024backend.exception.ExceptionResponse;
import ee.taltech.iti03022024backend.service.CampingRouteImagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Tag(name = "Camping route images", description = "Camping route images management APIs")
public class CampingRouteImagesController {
    private final CampingRouteImagesService campingRouteImagesService;


    @Operation(
            summary = "Add images to a camping route",
            description = "Add images to a camping route that is being created"
    )
    @ApiResponse(responseCode = "200", description = "Images successfully added to camping route")
    @ApiResponse(responseCode = "404", description = "Camping route with provided ID does not exist",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Camping route with id of 0 does not exist\"}")
            )
    )
    @ApiResponse(responseCode = "401", description = "User is not permitted to add images to this camping route",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"You are not permitted to do this action.\"}")
            )
    )
    @ApiResponse(responseCode = "500", description = "Something with file storing went wrong",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "emptyFile",
                                    summary = "Empty file error",
                                    value = "{\"message\": \"Encountered empty file\"}"
                            ),
                            @ExampleObject(
                                    name = "saveError",
                                    summary = "File save error",
                                    value = "{\"message\": \"File could not be saved\"}"
                            )
                    }
            )
    )
    @PostMapping("/camping_routes/images/{id}")
    public ResponseEntity<Void> addImagesToCampingRoute(
            Principal principal,
            @RequestParam("files") MultipartFile[] files,
            @PathVariable long id
    ) {
        return campingRouteImagesService.storeImages(principal.getName(), files, id);
    }

    @Operation(
            summary = "Get image names by camping route ID",
            description = "Get image names from system by provided camping route ID"
    )
    @ApiResponse(responseCode = "200", description = "Image names successfully found by camping route ID")
    @ApiResponse(responseCode = "404", description = "No images found from the system with provided camping route ID",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Could not find images for camping route\"}")
            )
    )
    @GetMapping("/public/camping_routes/images/{id}")
    public ResponseEntity<CampingRouteImageNamesDto> getImageNames(@PathVariable long id) {
        return campingRouteImagesService.getImageNames(id);
    }

    @Operation(
            summary = "Get image by camping route ID and image name",
            description = "Get image from the system by provided camping route ID and image name"
    )
    @ApiResponse(responseCode = "200", description = "Image found successfully from the system")
    @ApiResponse(responseCode = "404", description = "No image found from the system with provided camping route ID and image name",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Could not find and read file: <filename>\"}")
            )
    )
    @ApiResponse(responseCode = "500", description = "Something went wrong with getting the file",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Invalid file path\"}")
            )
    )
    @GetMapping("/public/camping_routes/images/{id}/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable long id, @PathVariable String imageName) {
        return campingRouteImagesService.getImage(id, imageName);
    }

    @Operation(
            summary = "Delete image from camping route",
            description = "Delete image from system with provided camping route ID and image name"
    )
    @ApiResponse(responseCode = "204", description = "Image successfully deleted from the system")
    @ApiResponse(responseCode = "404", description = "Information not found from the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "imageNotExisting",
                                    summary = "Image does not exist",
                                    value = "{\"message\": \"File does not exist\"}"
                            ),
                            @ExampleObject(
                                    name = "campingRouteNotExisting",
                                    summary = "Camping route does not exist",
                                    value = "{\"message\": \"Camping route with id of 0 does not exist\"}"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "401", description = "User is not permitted to delete image from this camping route",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"You are not permitted to do this action.\"}")
            )
    )
    @ApiResponse(responseCode = "500", description = "Image could not be deleted",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"File with name <imagename> could not be deleted\"}")
            )
    )
    @DeleteMapping("/camping_routes/images/{id}/{imageName}")
    public ResponseEntity<Void> deleteImage(
            Principal principal,
            @PathVariable long id,
            @PathVariable String imageName) {
        return campingRouteImagesService.deleteImage(principal.getName(), id, imageName);
    }

    @Operation(
            summary = "Delete all images from camping route",
            description = "Delete all images from system with provided camping route ID"
    )
    @ApiResponse(responseCode = "204", description = "Images successfully deleted from the system")
    @ApiResponse(responseCode = "404", description = "Information not found from the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "imagesNotExisting",
                                    summary = "Images does not exist",
                                    value = "{\"message\": \"Images could not be found for camping route with id: 0\"}"
                            ),
                            @ExampleObject(
                                    name = "campingRouteNotExisting",
                                    summary = "Camping route does not exist",
                                    value = "{\"message\": \"Camping route with id of 0 does not exist\"}"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "401", description = "User is not permitted to delete images from this camping route",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"You are not permitted to do this action.\"}")
            )
    )
    @ApiResponse(responseCode = "500", description = "Images could not be deleted",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Could not delete directory containing images for camping route with id: 0\"}")
            )
    )
    @DeleteMapping("/camping_routes/images/{id}")
    public ResponseEntity<Void> deleteAllImages(Principal principal, @PathVariable long id) {
        return campingRouteImagesService.deleteAllImage(principal.getName(), id);
    }
}
