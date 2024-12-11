package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.CommentEntity;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.exception.CommentNotExistsException;
import ee.taltech.iti03022024backend.exception.NotPermittedException;
import ee.taltech.iti03022024backend.exception.UserNotFoundException;
import ee.taltech.iti03022024backend.mapping.CommentMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.CommentRepository;
import ee.taltech.iti03022024backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CampingRouteRepository campingRouteRepository;

    public ResponseEntity<CommentDto> createComment(String principal, CommentDto dto, long campingRouteId) {
        CampingRouteEntity campingRoute = campingRouteRepository.findById(campingRouteId)
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + campingRouteId + " does not exist"));

        UserEntity user = userRepository.findByUsername(principal)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + principal));

        CommentEntity commentEntity = commentMapper.toEntity(dto);
        commentEntity.setCampingRoute(campingRoute);
        commentEntity.setUser(user);

        List<CommentEntity> commentList = campingRoute.getComment();
        if (commentList == null) {
            commentList = new ArrayList<>(); // Initialize if null
        }
        commentList.add(commentEntity);

        CommentEntity savedComment = commentRepository.save(commentEntity);
        campingRouteRepository.save(campingRoute);
        log.info("Creating a comment with contents of {}", dto.getContent());

        return ResponseEntity.ok(commentMapper.toDto(savedComment));
    }

    public ResponseEntity<List<CommentDto>> getCommentsByCampingRoute(Long id) {
        log.info("Fetching comments by camping route id {}", id);

        return ResponseEntity.ok(commentMapper.toDtoList(commentRepository.findByCampingRoute(getCampingRouteEntity(id))));
    }

    public ResponseEntity<List<CommentDto>> getCommentsByUserId(long id) {
        log.info("Fetching comments by user id {}", id);

        return ResponseEntity.ok(commentMapper.toDtoList(commentRepository.findByUser_Id(id)));
    }

    private CampingRouteEntity getCampingRouteEntity(long id) {
        log.info("Fetching camping route with id of {}", id);

        return campingRouteRepository.findById(id)
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + id + " does not exist"));
    }

    public ResponseEntity<Void> deleteCommentByCommentId(String name, long commentId) {
        log.info("Deleting comment with id {}", commentId);

        var comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotExistsException("Comment with id of " + commentId + " does not exist"));
        var user = userRepository.findByUsername(name).orElseThrow(() -> new UserNotFoundException("User not found with username: " + name));

        if (!comment.getUser().equals(user)) {
            throw new UserNotFoundException("User does not have permission to delete comment with id of " + commentId);
        }

        commentRepository.deleteById(commentId);
        log.info("Comment with id {} deleted", commentId);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> deleteCommentsFromCampingRoute(String name, long campingRouteId) {
        log.info("Deleting comments for camping route with id {}", campingRouteId);

        var campingRoute = campingRouteRepository.findById(campingRouteId).orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + campingRouteId + " does not exist"));
        var user = userRepository.findByUsername(name).orElseThrow(() -> new UserNotFoundException("User not found with username: " + name));

        if (!campingRoute.getUser().equals(user)) {
            throw new NotPermittedException("User does not have permission to delete comments for camping route with id of " + campingRouteId);
        }

        commentRepository.deleteByCampingRoute_Id(campingRouteId);
        log.info("Comments deleted for camping route with id {}", campingRouteId);

        return ResponseEntity.noContent().build();
    }
}
