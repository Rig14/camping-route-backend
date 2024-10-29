package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.CommentEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.mapping.CommentMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CampingRouteRepository campingRouteRepository;

    public ResponseEntity<CommentDto> createComment(CommentDto dto, long campingRouteId) {
        CampingRouteEntity campingRoute = campingRouteRepository.findById(campingRouteId)
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + campingRouteId + " does not exist"));

        CommentEntity commentEntity = commentMapper.toEntity(dto);
        commentEntity.setCampingRoute(campingRoute);

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

        return ResponseEntity.ok(commentMapper.toDtoList(commentRepository.findAllByCampingRoute(getCampingRouteEntity(id))));
    }

    private CampingRouteEntity getCampingRouteEntity(long id) {
        log.info("Fetching camping route with id of {}", id);

        return campingRouteRepository.findById(id)
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + id + " does not exist"));
    }
}
