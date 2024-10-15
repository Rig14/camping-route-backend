package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.mapping.CampingRouteMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampingRouteService {
    private final CampingRouteRepository repository;
    private final CampingRouteMapper mapper;

    public ResponseEntity<CampingRouteDto> createCampingRoute(CampingRouteDto dto) {
        return ResponseEntity.ok(mapper.toDto(repository.save(mapper.toEntity(dto))));
    }

    public ResponseEntity<List<CampingRouteDto>> getCampingRoutes(Optional<String> name, Optional<String> location) {
        return ResponseEntity.ok(mapper.toDtoList(repository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            name.orElse(""),
            location.orElse("")
        )));
    }

    public ResponseEntity<CampingRouteDto> getCampingRoute(long id) {
        return repository.findById(id).map(value -> ResponseEntity.ok(mapper.toDto(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> deleteCampingRoute(long id) {
        return repository.findById(id).map(value -> {
            repository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
