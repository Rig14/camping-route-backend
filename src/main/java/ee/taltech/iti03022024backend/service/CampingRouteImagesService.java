package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteImageNamesDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteImageNotFound;
import ee.taltech.iti03022024backend.exception.CampingRouteImageStorageException;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.exception.NotPermittedException;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampingRouteImagesService {
    private final Path rootDir = Path.of("files").resolve("camping_route_images");
    private final CampingRouteRepository repository;

    private void validateUser(String principal, long campingRouteId) {
        CampingRouteEntity route = repository.findById(campingRouteId)
                .orElseThrow(() -> new CampingRouteNotFoundException("Camping route with id of "
                        + campingRouteId + " does not exist"));

        if (!route.getUser().getUsername().equals(principal)) {
            throw new NotPermittedException("You are not permitted to do this action.");
        }
    }

    public ResponseEntity<Void> storeImages(String principal, MultipartFile[] files, long campingRouteId) {
        validateUser(principal, campingRouteId);

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
            throw new CampingRouteImageNotFound("Could not find images for camping route");
        }

        // find all files in the dir. get their names and construct dto with list of these names
        try (var stream = Files.list(dirName)) {
            var fileNames = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
            log.info("Found {} image files for camping route with id {}", fileNames.size(), campingRouteId);
            var response = new CampingRouteImageNamesDto();
            response.setImageNames(fileNames);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new CampingRouteImageNotFound(
                    "Could not find files for camping route with id " + campingRouteId,
                    e
            );
        }
    }

    private void storeImage(MultipartFile file, long campingRouteId) {
        log.info("Storing file with name {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new CampingRouteImageStorageException("Encountered empty file.");
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
        var filePath = rootDir
                .resolve(String.valueOf(id))
                .resolve(imageName)
                .normalize()
                .toAbsolutePath();

        try {
            // get the file as a "Resource" and send it back to client.
            var resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new CampingRouteImageNotFound("Could not find and read file " + resource.getFilename());
            }
            return ResponseEntity
                    .ok()
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new CampingRouteImageStorageException(
                    "Invalid file path", e);
        }
    }



    public ResponseEntity<Void> deleteImage(String principal, long campingRouteId, String imageName) {
        validateUser(principal, campingRouteId);

        log.info("Deleting image {} from camping route with id {}", imageName, campingRouteId);

        // file path in the system
        var filePath = rootDir
                .resolve(String.valueOf(campingRouteId))
                .resolve(imageName)
                .normalize()
                .toAbsolutePath();

        try {
            if (Files.notExists(filePath)) {
                throw new CampingRouteImageNotFound("File " + imageName + "does not exist.");
            }

            Files.delete(filePath);

            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new CampingRouteImageStorageException("File with name " + imageName + " could not be deleted", e);
        }
    }

    public ResponseEntity<Void> deleteAllImage(String principal, long campingRouteId) {
        validateUser(principal, campingRouteId);

        log.info("Deleting all images for camping route with id {}", campingRouteId);

        var dirPath = rootDir
                .resolve(String.valueOf(campingRouteId))
                .normalize()
                .toAbsolutePath();

        try {
            if (Files.notExists(dirPath)) {
                throw new CampingRouteImageNotFound("Images could not be found for camping route with id " + campingRouteId);
            }

            try (var stream = Files.walk(dirPath)) {
                stream.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }

            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new CampingRouteImageStorageException(
                    "Could not delete directory containing images for camping route with id " + campingRouteId);
        }
    }
}
