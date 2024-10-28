package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteImageNamesDto;
import ee.taltech.iti03022024backend.exception.CampingRouteImageStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
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

    public ResponseEntity<Resource> getImage(long id, String imageName) {
        log.info("Getting file with name {} for camping route with id {}", imageName, id);

        // file path in the system
        var fileDir = rootDir
                .resolve(String.valueOf(id))
                .resolve(imageName)
                .normalize()
                .toAbsolutePath();

        try {
            // get the file as a "Resource" and send it back to client.
            var resource = new UrlResource(fileDir.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new CampingRouteImageStorageException("Could not read file: " + resource.getFilename());
            }
            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new CampingRouteImageStorageException(
                    "File with name " +
                            imageName +
                            " does not exist for post with id "
                            + id,
                    e
            );
        }
    }
}
