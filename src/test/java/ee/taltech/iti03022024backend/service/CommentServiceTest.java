package ee.taltech.iti03022024backend.service;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.CommentEntity;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.exception.UserNotFoundException;
import ee.taltech.iti03022024backend.mapping.CommentMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.CommentRepository;
import ee.taltech.iti03022024backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

class CommentServiceTest {

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CampingRouteRepository campingRouteRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentService = new CommentService(commentMapper, userRepository, commentRepository, campingRouteRepository);
    }

    @Test
    void createComment_shouldReturnCreatedComment() {
        // Given
        String principal = "testUser";
        CommentDto dto = new CommentDto();
        dto.setContent("Great camping route!");
        long campingRouteId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(principal);

        CampingRouteEntity campingRouteEntity = new CampingRouteEntity();
        campingRouteEntity.setId(campingRouteId);

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent(dto.getContent());

        when(userRepository.findByUsername(principal)).thenReturn(Optional.of(userEntity));
        when(campingRouteRepository.findById(campingRouteId)).thenReturn(Optional.of(campingRouteEntity));
        when(commentMapper.toEntity(dto)).thenReturn(commentEntity);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(commentEntity);
        when(commentMapper.toDto(commentEntity)).thenReturn(dto);

        // When
        ResponseEntity<CommentDto> response = commentService.createComment(principal, dto, campingRouteId);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(campingRouteRepository, times(1)).save(campingRouteEntity);
        verify(commentRepository, times(1)).save(commentEntity);
    }

    @Test
    void createComment_shouldThrowCampingRouteNotFoundException_whenCampingRouteNotFound() {
        // Given
        String principal = "testUser";
        CommentDto dto = new CommentDto();
        dto.setContent("Great camping route!");
        long campingRouteId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(principal);
        when(userRepository.findByUsername(principal)).thenReturn(Optional.of(userEntity));

        // Mock campingRouteRepository to return an empty Optional (route not found)
        when(campingRouteRepository.findById(campingRouteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.createComment(principal, dto, campingRouteId))
                .isInstanceOf(CampingRouteNotFoundException.class)
                .hasMessageContaining("Camping route with id of " + campingRouteId + " does not exist");
    }

    @Test
    void getCommentsByCampingRoute_shouldReturnListOfComments() {
        // Given
        long campingRouteId = 1L;
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent("Great route!");
        List<CommentEntity> commentEntities = Collections.singletonList(commentEntity);

        when(campingRouteRepository.findById(campingRouteId)).thenReturn(Optional.of(new CampingRouteEntity()));
        when(commentRepository.findByCampingRoute(any(CampingRouteEntity.class))).thenReturn(commentEntities);
        when(commentMapper.toDtoList(commentEntities)).thenReturn(Collections.singletonList(new CommentDto()));

        // When
        ResponseEntity<List<CommentDto>> response = commentService.getCommentsByCampingRoute(campingRouteId);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(commentRepository, times(1)).findByCampingRoute(any(CampingRouteEntity.class));
    }

    @Test
    void getCommentsByCampingRoute_shouldThrowCampingRouteNotFoundException_whenRouteNotFound() {
        // Given
        long campingRouteId = 1L;
        when(campingRouteRepository.findById(campingRouteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.getCommentsByCampingRoute(campingRouteId))
                .isInstanceOf(CampingRouteNotFoundException.class)
                .hasMessageContaining("Camping route with id of " + campingRouteId + " does not exist");
    }

    @Test
    void getCommentsByUserId_shouldReturnListOfComments() {
        // Given
        long userId = 1L;
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setContent("Nice experience!");
        List<CommentEntity> commentEntities = Collections.singletonList(commentEntity);

        when(commentRepository.findByUser_Id(userId)).thenReturn(commentEntities);
        when(commentMapper.toDtoList(commentEntities)).thenReturn(Collections.singletonList(new CommentDto()));

        // When
        ResponseEntity<List<CommentDto>> response = commentService.getCommentsByUserId(userId);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(commentRepository, times(1)).findByUser_Id(userId);
    }

    @Test
    void getCommentsByUserId_shouldReturnEmptyList_whenNoCommentsFound() {
        // Given
        long userId = 1L;
        when(commentRepository.findByUser_Id(userId)).thenReturn(Collections.emptyList());

        // When
        ResponseEntity<List<CommentDto>> response = commentService.getCommentsByUserId(userId);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
    }
}
