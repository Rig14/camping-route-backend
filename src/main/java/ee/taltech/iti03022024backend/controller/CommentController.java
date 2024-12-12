package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.exception.ExceptionResponse;
import ee.taltech.iti03022024backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Tag(name = "Camping routes comment", description = "Camping route comment management APIs")
public class CommentController {
    private final CommentService service;

    @Operation(
            summary = "Add comment to camping route",
            description = "Add comment to camping route with provided information and camping route ID"
    )
    @ApiResponse(responseCode = "200", description = "Comment successfully added to camping route")
    @ApiResponse(responseCode = "404", description = "Some information was not found",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "routeNotFound",
                                    summary = "Camping route not found",
                                    value = "{\"message\": \"Camping route with id of 0 does not exist\"}"
                            ),
                            @ExampleObject(
                                    name = "userNotFound",
                                    summary = "User not found",
                                    value = "{\"message\": \"User not found with username: Kalamees24\"}"
                            )
                    }
            )
    )
    @PostMapping("/camping_routes/comments/{campingRouteId}")
    public ResponseEntity<CommentDto> createComment(
            Principal principal,
            @Valid @RequestBody CommentDto dto,
            @PathVariable long campingRouteId) {
        return service.createComment(principal.getName(), dto, campingRouteId);
    }

    @Operation(
            summary = "Get comments by camping route ID",
            description = "Get camping route comments by provided camping route ID from the system"
    )
    @ApiResponse(responseCode = "200", description = "Camping route comments successfully found")
    @ApiResponse(responseCode = "404", description = "Camping route with provided ID was not found",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Camping route with id of 0 does not exist\"}")
            )
    )
    @GetMapping("/public/camping_routes/comments/{campingRouteId}")
    public ResponseEntity<List<CommentDto>> getCommentsByCampingRoute(@PathVariable long campingRouteId) {
        return service.getCommentsByCampingRoute(campingRouteId);
    }

    @Operation(
            summary = "Get comments by user ID",
            description = "Get camping route comments by provided user ID from the system"
    )
    @ApiResponse(responseCode = "200", description = "Camping route comments successfully found")
    @GetMapping("/public/camping_routes/comments/user/{userId}")
    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@PathVariable long userId) {
        return service.getCommentsByUserId(userId);
    }

    @Operation(
            summary = "Delete comments by user ID",
            description = "Delete camping route comments by provided user ID from the system"
    )
    @ApiResponse(responseCode = "204", description = "Camping route comment successfully deleted")
    @ApiResponse(responseCode = "404", description = "Camping route with provided ID was not found",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Camping route with id of 0 does not exist\"}")
            ))
    @ApiResponse(responseCode = "401", description = "User that tries to delete comments is not permitted to do that",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"You are not permitted to delete this users comments.\"}")
            ))
    @DeleteMapping("/camping_routes/comments/single/{commentId}")
    public ResponseEntity<Void> deleteCommentByCommentId(
            @PathVariable long commentId,
            Principal principal
    ) {
        return service.deleteCommentByCommentId(principal.getName(), commentId);
    }
}
