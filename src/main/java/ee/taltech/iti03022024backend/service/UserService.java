package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.VerificationDto;
import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.*;
import ee.taltech.iti03022024backend.mapping.UserMapper;
import ee.taltech.iti03022024backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private boolean isPasswordValid(String password) {
        // Password must be at least 8 characters long and contains:
        // at least one digit
        // at least one lowercase letter
        // at least one uppercase letter
        // at least one special character
        String pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password != null && password.matches(pattern);
    }

    public ResponseEntity<UserDto> createUser(UserDto dto) {
        if (repository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        if (!isPasswordValid(dto.getPassword())) {
            throw new InvalidPasswordException("Password does not meet requirements.");
        }
        log.info("Creating new user with name {}", dto.getUsername());

        UserEntity entity = mapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return ResponseEntity.ok(mapper.toDto(repository.save(entity)));
    }

    public ResponseEntity<UserDto> verifyUser(VerificationDto loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getUsername());

        UserEntity user = repository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        log.info("Successful login for user: {}", loginRequest.getUsername());
        return ResponseEntity.ok(mapper.toDto(user));
    }

    public ResponseEntity<UserDto> getUser(long id) {
        log.info("Fetching user with id {}", id);
        return repository.findById(id)
                .map(entity -> ResponseEntity.ok(mapper.toDto(entity)))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public ResponseEntity<Void> deleteUser(long id) {
        log.info("Deleting user with id {}", id);
        return repository.findById(id).map(route -> {
            repository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElseThrow(() -> new UserNotFoundException("User with id of " + id + " does not exist"));
    }
}
