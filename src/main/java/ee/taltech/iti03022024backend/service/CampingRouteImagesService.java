package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteImageNamesDto;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampingRouteImagesService {
    private final Path rootDir = Path.of("files").resolve("camping_route_images");

    public ResponseEntity<Void> storeImages(MultipartFile[] files, long campingRouteId) {
        log.info("Storing images for camping route with id {}. image count: {}",
                campingRouteId,
                files.length
        );

        for (MultipartFile file : files) {
            storeImage(file, campingRouteId);
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<CampingRouteImageNamesDto> getImageNames(long campingRouteId) {
        log.info("Fetching image names for camping route with id {}", campingRouteId);

        var dirName = rootDir.resolve(String.valueOf(campingRouteId))
                .normalize()
                .toAbsolutePath();

        if (Files.notExists(dirName)) {
            log.info("No images where found for camping route with id {}", campingRouteId);
            return ResponseEntity.notFound().build();
        }

        // find all files in the dir. get their names and construct dto with list of these names
        try (var stream = Files.list(dirName)) {
            var fileNames = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
            var response = new CampingRouteImageNamesDto();
            response.setImageNames(fileNames);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new CampingRouteImageStorageException(
                    "Could not find files for camping route with id " + campingRouteId,
                    e
            );
        }
    }

    private void storeImage(MultipartFile file, long campingRouteId) {
        log.info("Storing file with name {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new CampingRouteImageStorageException("Encountered empty file. Skipping...");
        }

        try {
            var fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

            // create directories if they don't exist
            var campingRouteDir = rootDir.resolve(String.valueOf(campingRouteId));
            if (Files.notExists(campingRouteDir)) {
                log.info("Creating directory for camping route with id {}", campingRouteId);
                Files.createDirectories(campingRouteDir);
            }

            // save the file to the directory
            Files.copy(
                    file.getInputStream(),
                    campingRouteDir
                            .resolve(UUID.randomUUID() + "." + fileExtension)
                            .normalize()
                            .toAbsolutePath() // EX: files/{id}/as43sl5bf24.png
            );

        } catch (IOException e) {
            throw new CampingRouteImageStorageException("File could not be saved.", e);
        }
    }
}
