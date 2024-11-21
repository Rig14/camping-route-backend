package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteGpxNotFoundException;
import ee.taltech.iti03022024backend.exception.CampingRouteGpxStorageException;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampingRouteGpxService {

    private final Path rootDir = Path.of("files").resolve("camping_route_gpx");
    private final CampingRouteRepository repository;

    private void validateUser(String principal, long campingRouteId) {
        CampingRouteEntity route = repository.findById(campingRouteId)
                .orElseThrow(() -> new CampingRouteGpxNotFoundException("Camping route with id of "
                        + campingRouteId + " does not exist"));

        if (!route.getUser().getUsername().equals(principal)) {
            throw new NotPermittedException("You are not permitted to perform this action.");
        }
    }

    public ResponseEntity<Void> storeGpx(String principal, MultipartFile file, long campingRouteId) {
        validateUser(principal, campingRouteId);

        log.info("Storing GPX file for camping route with id {}: {}", campingRouteId, file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new CampingRouteGpxStorageException("Encountered empty file.");
        }

        try {
            var fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

            if (!"gpx".equalsIgnoreCase(fileExtension)) {
                throw new CampingRouteGpxStorageException("Only GPX files are allowed.");
            }

            // Create directories if they don't exist
            var campingRouteDir = rootDir.resolve(String.valueOf(campingRouteId));
            if (Files.notExists(campingRouteDir)) {
                log.info("Creating directory for camping route with id {}", campingRouteId);
                Files.createDirectories(campingRouteDir);
            }

            // Save the file, overwriting any existing GPX file
            Files.copy(
                    file.getInputStream(),
                    campingRouteDir.resolve("route.gpx").normalize().toAbsolutePath(),
                    StandardCopyOption.REPLACE_EXISTING // Overwrite if file exists
            );

        } catch (IOException e) {
            throw new CampingRouteGpxNotFoundException("GPX file could not be saved.");
        }

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Resource> getGpx(long campingRouteId) {
        log.info("Getting GPX file for camping route with id {}", campingRouteId);

        var filePath = rootDir
                .resolve(String.valueOf(campingRouteId))
                .resolve("route.gpx")
                .normalize()
                .toAbsolutePath();

        try {
            var resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new CampingRouteGpxNotFoundException("GPX file not found for camping route with id " + campingRouteId);
            }

            return ResponseEntity.ok(resource);

        } catch (MalformedURLException e) {
            throw new CampingRouteGpxStorageException("Invalid file path.");
        }
    }

    public ResponseEntity<Void> deleteGpx(String principal, long campingRouteId) {
        validateUser(principal, campingRouteId);

        log.info("Deleting GPX file for camping route with id {}", campingRouteId);

        var filePath = rootDir
                .resolve(String.valueOf(campingRouteId))
                .resolve("route.gpx")
                .normalize()
                .toAbsolutePath();

        try {
            if (Files.notExists(filePath)) {
                throw new CampingRouteGpxNotFoundException("GPX file does not exist for camping route with id " + campingRouteId);
            }

            Files.delete(filePath);

            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new CampingRouteGpxStorageException("GPX file could not be deleted.");
        }
    }
}
