package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.exception.NotPermittedException;
import ee.taltech.iti03022024backend.exception.UserNotFoundException;
import ee.taltech.iti03022024backend.mapping.CampingRouteMapper;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.UserRepository;
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
    private final CampingRouteRepository routeRepository;
    private final UserRepository userRepository;
    private final CampingRouteMapper mapper;

    public ResponseEntity<CampingRouteDto> createCampingRoute(String principal, CampingRouteDto dto) {
        UserEntity user = userRepository.findByUsername(principal)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + principal));

        log.info("Creating new camping route with name {}", dto.getName());

        CampingRouteEntity route = mapper.toEntity(dto);
        route.setUser(user);

        return ResponseEntity.ok(mapper.toDto(routeRepository.save(route)));
    }

    public ResponseEntity<List<CampingRouteDto>> getCampingRoutes(Optional<String> name, Optional<String> location, Optional<String> username) {
        log.info("Fetching all camping routes with filters for {} {} {}",
                name.orElse(""),
                location.orElse(""),
                username.orElse("")
        );
        return ResponseEntity.ok(mapper.toDtoList(routeRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrUser_UsernameContainingIgnoreCase(
                name.orElse(""),
                location.orElse(""),
                username.orElse("")
        )));
    }

    public ResponseEntity<List<CampingRouteDto>> getCampingRoutesByUserId(long id) {
        log.info("Fetching all camping routes with user id: {}", id);
        return ResponseEntity.ok(mapper.toDtoList(routeRepository.findByUser_Id(id)));
    }

    public ResponseEntity<CampingRouteDto> getCampingRoute(long id) {
        log.info("Fetching camping route with id {}", id);

        return routeRepository.findById(id).map(value -> ResponseEntity.ok(mapper.toDto(value)))
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of " + id + " does not exist"));
    }

    public ResponseEntity<Void> deleteCampingRoute(String principal, long id) {
        CampingRouteEntity route = routeRepository.findById(id)
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with ID " + id + " does not exist"));

        if (!route.getUser().getUsername().equals(principal)) {
            throw new NotPermittedException("You are not permitted to delete this camping route.");
        }

        log.info("Deleting camping route with id {}", id);

        routeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
