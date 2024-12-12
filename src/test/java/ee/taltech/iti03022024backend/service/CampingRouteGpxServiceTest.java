package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteGpxNotFoundException;
import ee.taltech.iti03022024backend.exception.CampingRouteGpxStorageException;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.exception.NotPermittedException;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampingRouteGpxServiceTest {

    @Mock
    private CampingRouteRepository campingRouteRepository;

    @Mock
    private MultipartFile multipartFile;

    @TempDir
    Path tempDir;

    private CampingRouteGpxService service;

    @BeforeEach
    void setUp() throws Exception {
        Path rootDir = tempDir.resolve("files").resolve("camping_route_gpx");
        Files.createDirectories(rootDir);

        service = new CampingRouteGpxService(campingRouteRepository);
        var rootDirField = CampingRouteGpxService.class.getDeclaredField("rootDir");
        rootDirField.setAccessible(true);
        rootDirField.set(service, rootDir);
    }

    private CampingRouteEntity mockRouteEntity(String username, long id) {
        var userEntity = new UserEntity();
        userEntity.setUsername(username);
        var route = new CampingRouteEntity();
        route.setId(id);
        route.setUser(userEntity);
        return route;
    }


    @Test
    void givenValidUserAndGpxFile_whenStoreGpx_thenSuccess() throws IOException {
        long routeId = 1L;
        String principal = "validUser";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("route.gpx");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));

        ResponseEntity<Void> response = service.storeGpx(principal, multipartFile, routeId);

        assertEquals(200, response.getStatusCodeValue());
        Path filePath = tempDir.resolve("files").resolve("camping_route_gpx").resolve(routeId + ".gpx");
        assertTrue(Files.exists(filePath));
    }

    @Test
    void givenNonExistingRoute_whenStoreGpx_thenThrowCampingRouteNotFoundException() {
        long routeId = 999L;
        String principal = "user";
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.storeGpx(principal, multipartFile, routeId);
        });
    }

    @Test
    void givenUnauthorizedUser_whenStoreGpx_thenThrowNotPermittedException() {
        long routeId = 2L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        assertThrows(NotPermittedException.class, () -> {
            service.storeGpx("otherUser", multipartFile, routeId);
        });
    }

    @Test
    void givenEmptyFile_whenStoreGpx_thenThrowCampingRouteGpxStorageException() {
        long routeId = 3L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        when(multipartFile.isEmpty()).thenReturn(true);
        when(multipartFile.getOriginalFilename()).thenReturn("route.gpx");

        assertThrows(CampingRouteGpxStorageException.class, () -> {
            service.storeGpx(principal, multipartFile, routeId);
        });
    }

    @Test
    void givenNonGpxFile_whenStoreGpx_thenThrowCampingRouteGpxStorageException() throws IOException {
        long routeId = 4L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("route.txt");

        assertThrows(CampingRouteGpxStorageException.class, () -> {
            service.storeGpx(principal, multipartFile, routeId);
        });
    }

    @Test
    void givenExistingGpxFile_whenGetGpx_thenResourceReturned() throws IOException {
        long routeId = 5L;
        Path filePath = tempDir.resolve("files").resolve("camping_route_gpx").resolve(routeId + ".gpx");
        Files.write(filePath, "test".getBytes());

        ResponseEntity<Resource> response = service.getGpx(routeId);

        assertEquals(200, response.getStatusCodeValue());
        Resource resource = response.getBody();
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void givenNonExistingGpxFile_whenGetGpx_thenThrowCampingRouteGpxNotFoundException() {
        long routeId = 6L;
        assertThrows(CampingRouteGpxNotFoundException.class, () -> {
            service.getGpx(routeId);
        });
    }


    @Test
    void givenAuthorizedUserAndExistingGpxFile_whenDeleteGpx_thenSuccess() throws IOException {
        long routeId = 7L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        Path filePath = tempDir.resolve("files").resolve("camping_route_gpx").resolve(routeId + ".gpx");
        Files.write(filePath, "test".getBytes());

        ResponseEntity<Void> response = service.deleteGpx(principal, routeId);

        assertEquals(204, response.getStatusCodeValue());
        assertFalse(Files.exists(filePath));
    }

    @Test
    void givenAuthorizedUserButNonExistingGpxFile_whenDeleteGpx_thenThrowCampingRouteGpxNotFoundException() {
        long routeId = 8L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        assertThrows(CampingRouteGpxNotFoundException.class, () -> {
            service.deleteGpx(principal, routeId);
        });
    }

    @Test
    void givenUnauthorizedUser_whenDeleteGpx_thenThrowNotPermittedException() throws IOException {
        long routeId = 9L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        Path filePath = tempDir.resolve("files").resolve("camping_route_gpx").resolve(routeId + ".gpx");
        Files.write(filePath, "test".getBytes());

        assertThrows(NotPermittedException.class, () -> {
            service.deleteGpx("otherUser", routeId);
        });
    }

    @Test
    void givenNonExistingRoute_whenDeleteGpx_thenThrowCampingRouteNotFoundException() {
        long routeId = 999L;
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.deleteGpx("someone", routeId);
        });
    }
}
