package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.dto.CampingRouteSearchRequest;
import ee.taltech.iti03022024backend.dto.PageResponse;
import ee.taltech.iti03022024backend.exception.ExceptionResponse;
import ee.taltech.iti03022024backend.service.CampingRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Tag(name = "Camping routes", description = "Camping route management APIs")
public class CampingRouteController {
    private final CampingRouteService service;

    @Operation(
            summary = "Create a new camping route",
            description = "Creates a new camping route to the system based on the provided information"
    )
    @ApiResponse(responseCode = "200", description = "Camping route successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Validation failed: name must not be blank\"}")
            ))
    @ApiResponse(responseCode = "404", description = "User that wants to add camping route is not found in the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"User not found with username: Kalamees24\"}")
            ))
    @PostMapping("/camping_routes")
    public ResponseEntity<CampingRouteDto> createCampingRoute(
            Principal principal,
            @Valid @RequestBody CampingRouteDto dto) {
        return service.createCampingRoute(principal.getName(), dto);
    }

    @PostMapping("/public/camping_routes/search")
    public PageResponse<CampingRouteDto> searchCampingRoutes(@RequestBody CampingRouteSearchRequest searchRequest) {
        System.out.println("Request received");
        return service.findCampingRoute(searchRequest);
    }

    @Operation(
            summary = "Get camping routes",
            description = "Get camping routes by name, location, username or get all if criteria is not provided"
    )
    @ApiResponse(responseCode = "200", description = "Camping routes successfuly found by given criteria")
    @PostMapping("/public/camping_routes")
    public PageResponse<CampingRouteDto> getCampingRoutes(@RequestBody CampingRouteSearchRequest searchRequest) {
        return service.getCampingRoutesForHomepage(searchRequest);
    }

    @Operation(
            summary = "Get camping routes by user ID",
            description = "Get camping routes from the system based on the given user ID"
    )
    @ApiResponse(responseCode = "200", description = "Camping routes successfully found by user ID")
    @GetMapping("/public/camping_routes/user/{userId}")
    public ResponseEntity<List<CampingRouteDto>> getCampingRoutesByUserId(
            @PathVariable @Min(value = 1, message = "User ID must be positive") long userId) {
        return service.getCampingRoutesByUserId(userId);
    }

    @Operation(
            summary = "Get a camping route by its ID",
            description = "Get a camping route from the system by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Camping route successfully found by its ID")
    @ApiResponse(responseCode = "404", description = "Camping route with provided ID is not found",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Camping route with id of 0 does not exist\"}")
            ))
    @GetMapping("/public/camping_routes/{id}")
    public ResponseEntity<CampingRouteDto> getCampingRoute(
            @PathVariable @Min(value = 1, message = "ID must be positive") long id) {
        return service.getCampingRoute(id);
    }

    @Operation(
            summary = "Delete camping route",
            description = "Delete camping route with provided ID from the system"
    )
    @ApiResponse(responseCode = "204", description = "Camping route successfully deleted from the system")
    @ApiResponse(responseCode = "404", description = "Camping route with provided ID is not found",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Camping route with id of 0 does not exist\"}")
            )
    )
    @ApiResponse(responseCode = "401", description = "User that tries to delete camping rote is not permitted to do that",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"You are not permitted to delete this camping route.\"}")
            )
    )
    @DeleteMapping("/camping_routes/{id}")
    public ResponseEntity<Void> deleteCampingRoute(
            Principal principal,
            @PathVariable @Min(value = 1, message = "ID must be positive") long id) {
        return service.deleteCampingRoute(principal.getName(), id);
    }
}
