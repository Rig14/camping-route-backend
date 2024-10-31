package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.mapping.CampingRouteMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CampingRouteService {
    private final CampingRouteRepository repository;
    private final CampingRouteMapper mapper;

    public ResponseEntity<CampingRouteDto> createCampingRoute(CampingRouteDto dto) {
        log.info("Creating new camping route with name {}", dto.getName());

        return ResponseEntity.ok(mapper.toDto(repository.save(mapper.toEntity(dto))));
    }

    public ResponseEntity<List<CampingRouteDto>> getCampingRoutes(Optional<String> name, Optional<String> location, Optional<String> username) {
        log.info("Fetching all camping routes with filters for {} {} {}",
                name.orElse(""),
                location.orElse(""),
                username.orElse("")
        );
        return ResponseEntity.ok(mapper.toDtoList(repository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrUser_UsernameContainingIgnoreCase(
                name.orElse(""),
                location.orElse(""),
                username.orElse("")
        )));
    }

    public ResponseEntity<CampingRouteDto> getCampingRoute(long id) {
        log.info("Fetching camping route with id {}", id);

        return repository.findById(id).map(value -> ResponseEntity.ok(mapper.toDto(value)))
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + id + " does not exist"));
    }

    public ResponseEntity<Void> deleteCampingRoute(long id) {
        log.info("Deleting camping route with id {}", id);

        return repository.findById(id).map(route -> {
            repository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + id + " does not exist"));
    }
}
