package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.service.CampingRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CampingRouteController {
    private final CampingRouteService service;

    @PostMapping("/camping_routes")
    public ResponseEntity<CampingRouteDto> createCampingRoute(Principal principal, @RequestBody CampingRouteDto dto) {
        return service.createCampingRoute(principal.getName(), dto);
    }

    @GetMapping("/public/camping_routes")
    public ResponseEntity<List<CampingRouteDto>> getCampingRoutes(
            @RequestParam("name") Optional<String> name,
            @RequestParam("location") Optional<String> location,
            @RequestParam("username") Optional<String> username) {
        return service.getCampingRoutes(name, location, username);
    }

    @GetMapping("/public/camping_routes/user/{userId}")
    public ResponseEntity<List<CampingRouteDto>> getCampingRoutesByUserId(@PathVariable long userId) {
        return service.getCampingRoutesByUserId(userId);
    }

    @GetMapping("/public/camping_routes/{id}")
    public ResponseEntity<CampingRouteDto> getCampingRoute(@PathVariable long id) {
        return service.getCampingRoute(id);
    }

    @DeleteMapping("/camping_routes/{id}")
    public ResponseEntity<Void> deleteCampingRoute(Principal principal, @PathVariable long id) {
        return service.deleteCampingRoute(principal.getName(), id);
    }
}
