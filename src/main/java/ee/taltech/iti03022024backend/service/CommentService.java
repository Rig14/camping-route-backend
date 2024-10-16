package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.CommentEntity;
import ee.taltech.iti03022024backend.mapping.CommentMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CampingRouteRepository campingRouteRepository;

    public ResponseEntity<CommentDto> createComment(CommentDto dto, long campingRouteId) {
        CampingRouteEntity campingRoute = campingRouteRepository.findById(campingRouteId)
                .orElseThrow(EntityNotFoundException::new);

        CommentEntity commentEntity = commentMapper.toEntity(dto);
        commentEntity.setCampingRoute(campingRoute);

        List<CommentEntity> commentList = campingRoute.getComment();
        if (commentList == null) {
            commentList = new ArrayList<>(); // Initialize if null
        }
        commentList.add(commentEntity);

        CommentEntity savedComment = commentRepository.save(commentEntity);
        campingRouteRepository.save(campingRoute);
        return ResponseEntity.ok(commentMapper.toDto(savedComment));
    }

    public ResponseEntity<List<CommentDto>> getCommentsByCampingRoute(Long id) {
        return ResponseEntity.ok(commentMapper.toDtoList(commentRepository.findAllByCampingRoute(getCampingRouteEntity(id))));
    }

    private CampingRouteEntity getCampingRouteEntity(long id) {
        return campingRouteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CampingRoute not found"));
    }
}
