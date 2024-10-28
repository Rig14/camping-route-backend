package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.service.CampingRouteImagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/camping_routes/images")
public class CampingRouteImagesController {
    private final CampingRouteImagesService campingRouteImagesService;

    @PostMapping()
    public ResponseEntity<Void> addImagesToCampingRoute(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam long id
    ) {
        return campingRouteImagesService.storeImages(files, id);
    }
}
