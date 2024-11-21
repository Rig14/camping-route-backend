package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.exception.ExceptionResponse;
import ee.taltech.iti03022024backend.service.CampingRouteGpxService;
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
@Tag(name = "Camping route GPX", description = "Camping route GPX file management APIs")
public class CampingRouteGpxController {
    private final CampingRouteGpxService gpxService;

    @Operation(
            summary = "Add GPX file to a camping route",
            description = "Add GPX file to a camping route that is being created"
    )
    @ApiResponse(responseCode = "200", description = "GPX file successfully added to camping route")
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
                                    name = "wrongFile",
                                    summary = "Wrong file error",
                                    value = "{\"message\": \"Only GPX files are allowed.\"}"
                            ),
                            @ExampleObject(
                                    name = "saveError",
                                    summary = "File save error",
                                    value = "{\"message\": \"GPX file could not be saved.\"}"
                            )
                    }
            )
    )
    @PostMapping("/camping_routes/gpx/{id}")
    public ResponseEntity<Void> addGpxFileToCampingRoute(
            Principal principal,
            @RequestParam("file") MultipartFile file,
            @PathVariable long id
    ) {
        return gpxService.storeGpx(principal.getName(), file, id);
    }

    @Operation(
            summary = "Get GPX file by camping route ID",
            description = "Get GPX file from the system by provided camping route ID"
    )
    @ApiResponse(responseCode = "200", description = "GPX file found successfully from the system")
    @ApiResponse(responseCode = "404", description = "No GPX file found from the system with provided camping route ID",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"GPX file not found for camping route with id 0\"}")
            )
    )
    @ApiResponse(responseCode = "500", description = "Something went wrong with getting the file",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Invalid file path\"}")
            )
    )
    @GetMapping("/public/camping_routes/gpx/{campingRouteId}")
    public ResponseEntity<Resource> getGpx(@PathVariable long campingRouteId) {
        return gpxService.getGpx(campingRouteId);
    }

    @Operation(
            summary = "Delete GPX file from camping route",
            description = "Delete GPX file from system with provided camping route ID"
    )
    @ApiResponse(responseCode = "204", description = "GPX file successfully deleted from the system")
    @ApiResponse(responseCode = "404", description = "Information not found from the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "fileNotExisting",
                                    summary = "File does not exist",
                                    value = "{\"message\": \"GPX file does not exist for camping route with id 0\"}"
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
                    examples = @ExampleObject(value = "{\"message\": \"GPX file could not be deleted.\"}")
            )
    )
    @DeleteMapping("/camping_routes/gpx/{id}")
    public ResponseEntity<Void> deleteGpx(
            Principal principal,
            @PathVariable long id) {
        return gpxService.deleteGpx(principal.getName(), id);
    }
}
