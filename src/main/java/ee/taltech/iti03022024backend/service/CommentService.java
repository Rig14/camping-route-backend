package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CommentDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.mapping.CommentMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CampingRouteRepository campingRouteRepository;

    public ResponseEntity<CommentDto> createComment(CommentDto dto) {
        return ResponseEntity.ok(commentMapper.toDto(commentRepository.save(commentMapper.toEntity(dto))));
    }

    public ResponseEntity<List<CommentDto>> getCommentsByCampingRoute(Long id) {
        return ResponseEntity.ok(commentMapper.toDtoList(commentRepository.findAllByCampingRoute(getCampingRouteEntity(id))));
    }

    public CampingRouteEntity getCampingRouteEntity(long id) {
        return campingRouteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CampingRoute not found"));
    }
}
