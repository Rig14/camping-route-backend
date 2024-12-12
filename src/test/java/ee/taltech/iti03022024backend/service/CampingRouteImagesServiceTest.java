package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.CampingRouteImageNamesDto;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteImageNotFound;
import ee.taltech.iti03022024backend.exception.CampingRouteImageStorageException;
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

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampingRouteImagesServiceTest {

    @Mock
    private CampingRouteRepository campingRouteRepository;
    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<CampingRouteEntity> routeCaptor;

    // We will use a temporary directory to test file operations
    @TempDir
    Path tempDir;

    private CampingRouteImagesService service;

    @BeforeEach
    void setUp() throws Exception {
        // Set up a temporary directory as our rootDir
        Path rootDir = tempDir.resolve("files").resolve("camping_route_images");
        Files.createDirectories(rootDir);

        // We need to inject this rootDir into the service since the code given uses a final field.
        // We'll do this via reflection since the code snippet is final and not modifiable.
        service = new CampingRouteImagesService(campingRouteRepository);
        var rootDirField = CampingRouteImagesService.class.getDeclaredField("rootDir");
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
    void givenEmptyFile_whenStoreImages_thenThrowCampingRouteImageStorageException() throws IOException {
        // given
        long routeId = 1L;
        String principal = "validUser";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        when(multipartFile.isEmpty()).thenReturn(true);
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");

        MultipartFile[] files = new MultipartFile[]{multipartFile};

        // when / then
        assertThrows(CampingRouteImageStorageException.class, () -> {
            service.storeImages(principal, files, routeId);
        });
    }

    @Test
    void givenNonExistingRoute_whenStoreImages_thenThrowCampingRouteNotFoundException() {
        // given
        long routeId = 999L;
        String principal = "validUser";
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        MultipartFile[] files = new MultipartFile[]{multipartFile};

        // when / then
        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.storeImages(principal, files, routeId);
        });
    }

    @Test
    void givenUnauthorizedUser_whenStoreImages_thenThrowNotPermittedException() {
        // given
        long routeId = 1L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        MultipartFile[] files = new MultipartFile[]{multipartFile};

        // when / then
        assertThrows(NotPermittedException.class, () -> {
            service.storeImages("otherUser", files, routeId);
        });
    }


    @Test
    void givenExistingDirectoryWithFiles_whenGetImageNames_thenReturnListOfFiles() throws IOException {
        // given
        long routeId = 2L;
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve("image1.png"));
        Files.createFile(routeDir.resolve("image2.jpg"));

        // when
        ResponseEntity<CampingRouteImageNamesDto> response = service.getImageNames(routeId);

        // then
        assertEquals(200, response.getStatusCodeValue());
        CampingRouteImageNamesDto dto = response.getBody();
        assertNotNull(dto);
        List<String> imageNames = dto.getImageNames();
        assertEquals(2, imageNames.size());
        assertTrue(imageNames.contains("image1.png"));
        assertTrue(imageNames.contains("image2.jpg"));
    }

    @Test
    void givenNonExistingDirectory_whenGetImageNames_thenThrowCampingRouteImageNotFound() {
        // given
        long routeId = 3L;
        // no directory created

        // when / then
        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.getImageNames(routeId);
        });
    }


    @Test
    void givenExistingFile_whenGetImage_thenReturnResource() throws IOException {
        // given
        long routeId = 4L;
        String imageName = "image.png";
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.write(routeDir.resolve(imageName), "test".getBytes());

        // when
        ResponseEntity<Resource> response = service.getImage(routeId, imageName);

        // then
        assertEquals(200, response.getStatusCodeValue());
        Resource resource = response.getBody();
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void givenNonExistingFile_whenGetImage_thenThrowCampingRouteImageNotFound() {
        // given
        long routeId = 5L;
        String imageName = "no_such_image.png";
        // no file created

        // when / then
        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.getImage(routeId, imageName);
        });
    }


    @Test
    void givenAuthorizedUserAndExistingFile_whenDeleteImage_thenFileDeleted() throws IOException {
        // given
        long routeId = 6L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        String imageName = "delete_me.png";
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve(imageName));

        // when
        ResponseEntity<Void> response = service.deleteImage(principal, routeId, imageName);

        // then
        assertEquals(204, response.getStatusCodeValue());
        assertFalse(Files.exists(routeDir.resolve(imageName)));
    }

    @Test
    void givenAuthorizedUserAndNonExistingFile_whenDeleteImage_thenThrowCampingRouteImageNotFound() {
        // given
        long routeId = 7L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        String imageName = "nonexistent.png";
        // no file created

        // when / then
        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.deleteImage(principal, routeId, imageName);
        });
    }

    @Test
    void givenUnauthorizedUser_whenDeleteImage_thenThrowNotPermittedException() throws IOException {
        // given
        long routeId = 8L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        String imageName = "some.png";
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve(imageName));

        // when / then
        assertThrows(NotPermittedException.class, () -> {
            service.deleteImage("otherUser", routeId, imageName);
        });
    }

    @Test
    void givenNonExistingRoute_whenDeleteImage_thenThrowCampingRouteNotFoundException() {
        // given
        long routeId = 999L;
        String principal = "someUser";
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.deleteImage(principal, routeId, "whatever.png");
        });
    }


    @Test
    void givenAuthorizedUserAndExistingDirectory_whenDeleteAllImages_thenAllDeleted() throws IOException {
        // given
        long routeId = 10L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve("img1.png"));
        Files.createFile(routeDir.resolve("img2.png"));

        // when
        ResponseEntity<Void> response = service.deleteAllImage(principal, routeId);

        // then
        assertEquals(204, response.getStatusCodeValue());
        assertFalse(Files.exists(routeDir));
    }

    @Test
    void givenAuthorizedUserAndNonExistingDirectory_whenDeleteAllImages_thenThrowCampingRouteImageNotFound() {
        // given
        long routeId = 11L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        // no directory created

        // when / then
        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.deleteAllImage(principal, routeId);
        });
    }

    @Test
    void givenUnauthorizedUser_whenDeleteAllImages_thenThrowNotPermittedException() throws IOException {
        // given
        long routeId = 12L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve("img.png"));

        // when / then
        assertThrows(NotPermittedException.class, () -> {
            service.deleteAllImage("otherUser", routeId);
        });
    }

    @Test
    void givenNonExistingRoute_whenDeleteAllImages_thenThrowCampingRouteNotFoundException() {
        // given
        long routeId = 999L;
        String principal = "someone";
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.deleteAllImage(principal, routeId);
        });
    }
}
