package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.exception.CampingRouteImageStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampingRouteImagesService {
    private final Path rootDir = Path.of("files").resolve("camping_route_images");

    public ResponseEntity<Void> storeImages(MultipartFile[] files, long postId) {
        log.info("Storing images for camping route with id {}. image count: {}",
                postId,
                files.length
        );

        for (MultipartFile file : files) {
            storeImage(file, postId);
        }

        return ResponseEntity.ok().build();
    }

    private void storeImage(MultipartFile file, long postId) {
        log.info("Storing file with name {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new CampingRouteImageStorageException("Encountered empty file. Skipping...");
        }

        try {
            var fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

            // create directories if they don't exist
            var campingRouteDir = rootDir.resolve(String.valueOf(postId));
            if (Files.notExists(campingRouteDir)) {
                log.info("Creating directory for camping route with id {}", postId);
                Files.createDirectories(campingRouteDir);
            }

            // save the file to the directory
            try (var stream = Files.list(campingRouteDir)) {
                var currentFileCount = stream.count();

                Files.copy(
                        file.getInputStream(),
                        campingRouteDir
                                .resolve(currentFileCount + "." + fileExtension)
                                .normalize()
                                .toAbsolutePath() // EX: files/{id}/0.png
                );
            }
        } catch (IOException e) {
            throw new CampingRouteImageStorageException("File could not be saved.", e);
        }
    }
}
