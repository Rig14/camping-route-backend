package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.service.CampingRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/camping_routes")
public class CampingRouteController {
    private final CampingRouteService service;

    @PostMapping()
    public ResponseEntity<CampingRouteDto> createCampingRoute(@RequestBody CampingRouteDto dto) {
        return service.createCampingRoute(dto);
    }

    @GetMapping()
    public ResponseEntity<List<CampingRouteDto>> getCampingRoutes(
            @RequestParam("name") Optional<String> name,
            @RequestParam("location") Optional<String> location,
            @RequestParam("username") Optional<String> username) {
        return service.getCampingRoutes(name, location, username);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CampingRouteDto>> getCampingRoutesByUserId(@PathVariable long userId) {
        return service.getCampingRoutesByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampingRouteDto> getCampingRoute(@PathVariable long id) {
        return service.getCampingRoute(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampingRoute(@PathVariable long id) {
        return service.deleteCampingRoute(id);
    }
}
