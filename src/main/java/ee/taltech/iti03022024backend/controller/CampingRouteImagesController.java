package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.dto.CampingRouteImageNamesDto;
import ee.taltech.iti03022024backend.service.CampingRouteImagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api")
public class CampingRouteImagesController {
    private final CampingRouteImagesService campingRouteImagesService;

    @PostMapping("/camping_routes/images/{id}")
    public ResponseEntity<Void> addImagesToCampingRoute(
            @RequestParam("files") MultipartFile[] files,
            @PathVariable long id
    ) {
        return campingRouteImagesService.storeImages(files, id);
    }

    @GetMapping("/public/camping_routes/images/{id}")
    public ResponseEntity<CampingRouteImageNamesDto> getImageNames(@PathVariable long id) {
        return campingRouteImagesService.getImageNames(id);
    }

    @GetMapping("/public/camping_routes/images/{id}/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable long id, @PathVariable String imageName) {
        return campingRouteImagesService.getImage(id, imageName);
    }

    @DeleteMapping("/camping_routes/images/{id}/{imageName}")
    public ResponseEntity<Void> deleteImage(@PathVariable long id, @PathVariable String imageName) {
        return campingRouteImagesService.deleteImage(id, imageName);
    }

    @DeleteMapping("/camping_routes/images/{id}")
    public ResponseEntity<Void> deleteAllImages(@PathVariable long id) {
        return campingRouteImagesService.deleteAllImage(id);
    }
}
