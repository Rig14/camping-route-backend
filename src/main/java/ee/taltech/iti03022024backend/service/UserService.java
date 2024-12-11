package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.VerificationDto;
import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.*;
import ee.taltech.iti03022024backend.mapping.UserMapper;
import ee.taltech.iti03022024backend.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey jwtKey;

    private boolean isPasswordValid(String password) {
        // Password must be at least 8 characters long and contains:
        // at least one digit
        // at least one lowercase letter
        // at least one uppercase letter
        // at least one special character
        String pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password != null && password.matches(pattern);
    }

    private String generateToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of(
                        // claims can be added if needed
                        "userId", user.getId()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(jwtKey)
                .compact();
    }

    public ResponseEntity<VerificationDto> createUser(UserDto dto) {
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

        UserEntity user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        String token = generateToken(user);
        repository.save(user);
        return ResponseEntity.ok(new VerificationDto(token, user.getId()));
    }

    public ResponseEntity<VerificationDto> verifyUser(UserDto dto) {
        log.info("Attempting login for user: {}", dto.getUsername());

        UserEntity user = repository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        log.info("Successful login for user: {}", dto.getUsername());
        String token = generateToken(user);
        return ResponseEntity.ok(new VerificationDto(token, user.getId()));
    }

    public ResponseEntity<UserDto> getUser(long id) {
        log.info("Fetching user with id {}", id);
        return repository.findById(id)
                .map(entity -> ResponseEntity.ok(mapper.toDto(entity)))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public ResponseEntity<Void> deleteUser(String principal, long id) {
        UserEntity user = repository.findByUsername(principal)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + principal));

        if (user.getId() != id) {
            throw new NotPermittedException("You are not permitted to delete this user.");
        }

        log.info("Deleting user with ID {}", id);

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
