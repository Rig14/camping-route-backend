package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.service.CampingRouteGpxService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class CampingRouteGpxController {
    private final CampingRouteGpxService gpxService;

    @PostMapping("/camping_routes/gpx/{id}")
    public ResponseEntity<Void> addGpxFileToCampingRoute(
            Principal principal,
            @RequestParam("file") MultipartFile file,
            @PathVariable long id
    ) {
        return gpxService.storeGpx(principal.getName(), file, id);
    }

    @GetMapping("/public/camping_routes/gpx/{campingRouteId}")
    public ResponseEntity<Resource> getGpx(@PathVariable long campingRouteId) {
        return gpxService.getGpx(campingRouteId);
    }

    @DeleteMapping("/camping_routes/gpx/{id}")
    public ResponseEntity<Void> deleteGpx(
            Principal principal,
            @PathVariable long id) {
        return gpxService.deleteGpx(principal.getName(), id);
    }
}
