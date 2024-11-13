package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.dto.VerificationDto;
import ee.taltech.iti03022024backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class UserController {
    private final UserService service;

    @PostMapping("/public/user")
    public ResponseEntity<VerificationDto> createUser(@RequestBody UserDto dto) {
        return service.createUser(dto);
    }

    @GetMapping("/public/user/verify")
    public ResponseEntity<VerificationDto> verifyUser(@RequestBody UserDto dto) {
        return service.verifyUser(dto);
    }

    @GetMapping("/public/user/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(Principal principal, @PathVariable long id) {
        return service.deleteUser(principal.getName(), id);
    }

}
