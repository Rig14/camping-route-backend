package ee.taltech.iti03022024backend.service;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import ee.taltech.iti03022024backend.dto.UserDto;
import ee.taltech.iti03022024backend.dto.VerificationDto;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.*;
import ee.taltech.iti03022024backend.mapping.UserMapper;
import ee.taltech.iti03022024backend.mapping.UserMapperImpl;
import ee.taltech.iti03022024backend.repository.UserRepository;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Spy
    private UserMapper mapper = new UserMapperImpl();

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SecretKey jwtKey;

    private UserService userService;

    @BeforeEach
    void setUp() {
        byte[] keyBytes = "0123456789abcdef0123456789abcdef".getBytes(); // 32 bytes for HS256
        jwtKey = Keys.hmacShaKeyFor(keyBytes);

        userService = new UserService(mapper, repository, passwordEncoder, jwtKey);
    }

    @Test
    void createUser_shouldThrowUsernameAlreadyExistsException_whenUsernameIsTaken() {
        // Given
        UserDto dto = new UserDto();
        dto.setUsername("testUser");
        dto.setEmail("test@example.com");
        dto.setPassword("Valid@123");

        when(repository.existsByUsername(dto.getUsername())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    void createUser_shouldThrowEmailAlreadyExistsException_whenEmailIsRegistered() {
        // Given
        UserDto dto = new UserDto();
        dto.setUsername("testUser");
        dto.setEmail("test@example.com");
        dto.setPassword("Valid@123");

        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email is already registered");
    }

    @Test
    void createUser_shouldThrowInvalidPasswordException_whenPasswordIsInvalid() {
        // Given
        UserDto dto = new UserDto();
        dto.setUsername("testUser");
        dto.setEmail("test@example.com");
        dto.setPassword("short");

        when(repository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Password does not meet requirements.");
    }

    @Test
    void verifyUser_shouldThrowInvalidCredentialsException_whenCredentialsAreInvalid() {
        // Given
        UserDto dto = new UserDto();
        dto.setUsername("testUser");
        dto.setPassword("Invalid@123");

        when(repository.findByUsername(dto.getUsername())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.verifyUser(dto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void getUser_shouldReturnUserDto_whenUserExists() {
        // Given
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testUser");

        when(repository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mapper.toDto(userEntity)).thenReturn(new UserDto());

        // When
        ResponseEntity<UserDto> response = userService.getUser(userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void verifyUser_shouldReturnToken_whenCredentialsAreValid() {
        // Given
        UserDto dto = new UserDto();
        dto.setUsername("testUser");
        dto.setPassword("Valid@123");

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword("$2a$10$encodedPassword"); // Simulate encoded password

        when(repository.findByUsername(dto.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

        // When
        ResponseEntity<VerificationDto> response = userService.verifyUser(dto);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
    }

    @Test
    void verifyUser_shouldThrowInvalidCredentialsException_whenPasswordIsIncorrect() {
        // Given
        UserDto dto = new UserDto();
        dto.setUsername("testUser");
        dto.setPassword("Invalid@123");

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword("$2a$10$encodedPassword"); // Simulate encoded password

        when(repository.findByUsername(dto.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.verifyUser(dto))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void getUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        // Given
        long userId = 1L;
        when(repository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id: " + userId);
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserIsOwner() {
        // Given
        String principal = "testUser";
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername(principal);

        when(repository.findByUsername(principal)).thenReturn(Optional.of(userEntity));

        // When
        ResponseEntity<Void> response = userService.deleteUser(principal, userId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(repository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowNotPermittedException_whenUserIsNotOwner() {
        // Given
        String principal = "testUser";
        long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L); // Different ID
        userEntity.setUsername(principal);

        when(repository.findByUsername(principal)).thenReturn(Optional.of(userEntity));

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(principal, userId))
                .isInstanceOf(NotPermittedException.class)
                .hasMessageContaining("You are not permitted to delete this user.");
    }

    @Test
    void deleteUser_shouldThrowUserNotFoundException_whenPrincipalDoesNotExist() {
        // Given
        String principal = "nonExistentUser";
        long userId = 1L;

        when(repository.findByUsername(principal)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(principal, userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with username: " + principal);
    }
}
