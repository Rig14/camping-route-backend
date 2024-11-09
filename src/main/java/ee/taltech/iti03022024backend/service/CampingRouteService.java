package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.exception.NotPermittedException;
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

    public ResponseEntity<List<CampingRouteDto>> getCampingRoutesByUserId(long id) {
        log.info("Fetching all camping routes with user id: {}", id);
        return ResponseEntity.ok(mapper.toDtoList(repository.findByUser_Id(id)));
    }

    public ResponseEntity<CampingRouteDto> getCampingRoute(long id) {
        log.info("Fetching camping route with id {}", id);

        return repository.findById(id).map(value -> ResponseEntity.ok(mapper.toDto(value)))
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + id + " does not exist"));
    }

    public ResponseEntity<Void> deleteCampingRoute(String principal, long id) {
        CampingRouteEntity route = repository.findById(id)
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with ID " + id + " does not exist"));

        if (!route.getUser().getUsername().equals(principal)) {
            throw new NotPermittedException("You are not permitted to delete this camping route.");
        }

        log.info("Deleting camping route with id {}", id);

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
