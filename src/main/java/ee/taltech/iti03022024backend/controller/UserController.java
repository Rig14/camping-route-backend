package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.dto.VerificationDto;
import ee.taltech.iti03022024backend.exception.ExceptionResponse;
import ee.taltech.iti03022024backend.service.UserService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
@Tag(name = "Users", description = "User management APIs")
public class UserController {
    private final UserService service;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user to the system based on the provided information"
    )
    @ApiResponse(responseCode = "200", description = "User successfully created")
    @ApiResponse(responseCode = "409", description = "Some information already exists on the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "usernameTaken",
                                    summary = "Username already in use",
                                    value = "{\"message\": \"Username is already taken\"}"
                            ),
                            @ExampleObject(
                                    name = "emailRegistered",
                                    summary = "Email already registered",
                                    value = "{\"message\": \"Email is already registered\"}"
                            )
                    }
            )
    )
    @ApiResponse(responseCode = "400", description = "Password is not appropriate",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Password does not meet requirements.\"}")
            )
    )
    @PostMapping("/public/user")
    public ResponseEntity<VerificationDto> createUser(@RequestBody UserDto dto) {
        return service.createUser(dto);
    }

    @Operation(
            summary = "Verify user",
            description = "Verify that the user with provided information exists in the system"
    )
    @ApiResponse(responseCode = "200", description = "User successfully verified")
    @ApiResponse(responseCode = "400", description = "Some information is invalid",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"Invalid username or password\"}")
            )
    )
    @PostMapping("/public/user/verify")
    public ResponseEntity<VerificationDto> verifyUser(@RequestBody UserDto dto) {
        return service.verifyUser(dto);
    }

    @Operation(
            summary = "Get user",
            description = "Get user with provided ID from the system"
    )
    @ApiResponse(responseCode = "200", description = "User successfully found from the system")
    @ApiResponse(responseCode = "404", description = "User not found from the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"User not found with id: 0\"}")
            )
    )
    @GetMapping("/public/user/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    @Operation(
            summary = "Delete user",
            description = "Delete user with provided ID from the system"
    )
    @ApiResponse(responseCode = "204", description = "User successfully deleted from the system")
    @ApiResponse(responseCode = "404", description = "User not found from the system",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"User not found with username: Kalamees24\"}")
            )
    )
    @ApiResponse(responseCode = "401", description = "User that tries to delete user is not permitted to do that",
            content = @Content(
                    schema = @Schema(implementation = ExceptionResponse.class),
                    examples = @ExampleObject(value = "{\"message\": \"You are not permitted to delete this user.\"}")
            )
    )
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(Principal principal, @PathVariable long id) {
        return service.deleteUser(principal.getName(), id);
    }
}
