package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.dto.VerificationDto;
import ee.taltech.iti03022024backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {
    private final UserService service;

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        return service.createUser(dto);
    }

    @GetMapping("/verify")
    public ResponseEntity<UserDto> verifyUser(@RequestBody VerificationDto dto) {
        return service.verifyUser(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable long id) {
        return service.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        return service.deleteUser(id);
    }

}
