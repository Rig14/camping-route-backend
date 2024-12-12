package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.ViewDto;
import ee.taltech.iti03022024backend.exception.ExceptionResponse;
import ee.taltech.iti03022024backend.service.CampingRouteViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Tag(name = "Views", description = "Camping route views API")
public class ViewsController {
    private final CampingRouteViewService service;

    @Operation(
            summary = "Add a view to the camping route",
            description = "Add a view to camping route provided camping route ID is given"
    )
    @ApiResponse(responseCode = "200", description = "view was added to the camping route")
    @ApiResponse(responseCode = "404", description = "Some information was not found",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "routeNotFound",
                                    summary = "Camping route not found",
                                    value = "{\"message\": \"Camping route with id of 0 does not exist\"}"
                            )
                    }
            )
    )
    @PostMapping("/public/camping_routes/views/{campingRouteId}")
    public ResponseEntity<ViewDto> addView(@PathVariable long campingRouteId) {
        return service.addViewForCampingRoute(campingRouteId);
    }


    @Operation(
            summary = "Get view count for a camping route.",
            description = "Get view count for camping route by provided camping route ID from the system"
    )
    @ApiResponse(responseCode = "200", description = "Camping route view count successfully fetched")
    @ApiResponse(responseCode = "404", description = "Camping route with provided ID was not found",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Camping route with id of 0 does not exist\"}")
            )
    )
    @GetMapping("/public/camping_routes/views/{campingRouteId}")
    public ResponseEntity<ViewDto> getViewCount(@PathVariable long campingRouteId) {
        return service.getViewCountForCampingRoute(campingRouteId);
    }

}
