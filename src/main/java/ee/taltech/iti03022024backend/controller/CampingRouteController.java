package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.exception.ExceptionResponse;
import ee.taltech.iti03022024backend.service.CampingRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @ApiResponse(responseCode = "404", description = "User that wants to add camping route is not found in the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"User not found with username: Kalamees24\"}")
            )
    )
    @PostMapping("/camping_routes")
    public ResponseEntity<CampingRouteDto> createCampingRoute(Principal principal, @RequestBody CampingRouteDto dto) {
        return service.createCampingRoute(principal.getName(), dto);
    }

    @Operation(
            summary = "Get camping routes",
            description = "Get camping routes by name, location, username or get all if criteria is not provided"
    )
    @ApiResponse(responseCode = "200", description = "Camping routes successfuly found by given criteria")
    @GetMapping("/public/camping_routes")
    public ResponseEntity<List<CampingRouteDto>> getCampingRoutes(
            @RequestParam("name") Optional<String> name,
            @RequestParam("location") Optional<String> location,
            @RequestParam("username") Optional<String> username) {
        return service.getCampingRoutes(name, location, username);
    }

    @Operation(
            summary = "Get camping routes by user ID",
            description = "Get camping routes from the system based on the given user ID"
    )
    @ApiResponse(responseCode = "200", description = "Camping routes successfully found by user ID")
    @GetMapping("/public/camping_routes/user/{userId}")
    public ResponseEntity<List<CampingRouteDto>> getCampingRoutesByUserId(@PathVariable long userId) {
        return service.getCampingRoutesByUserId(userId);
    }

    @Operation(
            summary = "Get a camping route by it's ID",
            description = "Get a camping route from the system by it's ID"
    )
    @ApiResponse(responseCode = "200", description = "Camping route successfully found by it's ID")
    @ApiResponse(responseCode = "404", description = "Camping route with provided ID is not found",
            content = @Content(
                 schema = @Schema(implementation = ExceptionResponse.class),
                 examples = @ExampleObject(value = "{\"message\": \"Camping route with id of 0 does not exist\"}")
             )
    )
    @GetMapping("/public/camping_routes/{id}")
    public ResponseEntity<CampingRouteDto> getCampingRoute(@PathVariable long id) {
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
    public ResponseEntity<Void> deleteCampingRoute(Principal principal, @PathVariable long id) {
        return service.deleteCampingRoute(principal.getName(), id);
    }
}
