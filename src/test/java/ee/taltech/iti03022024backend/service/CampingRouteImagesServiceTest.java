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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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

    @TempDir
    Path tempDir;

    private CampingRouteImagesService service;

    @BeforeEach
    void setUp() throws Exception {
        // Set up rootDir for file operations only
        Path rootDir = tempDir.resolve("files").resolve("camping_route_images");
        Files.createDirectories(rootDir);

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
    void givenNonExistingImage_whenGetImage_thenThrowCampingRouteImageNotFound() {
        long campingRouteId = 1L;
        String imageName = "nonexistent.jpg";

        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.getImage(campingRouteId, imageName);
        });
    }

    @Test
    void givenEmptyFile_whenStoreImages_thenThrowCampingRouteImageStorageException() {
        long routeId = 1L;
        String principal = "validUser";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        when(multipartFile.isEmpty()).thenReturn(true);
        when(multipartFile.getOriginalFilename()).thenReturn("image.png");

        MultipartFile[] files = new MultipartFile[]{multipartFile};

        assertThrows(CampingRouteImageStorageException.class, () -> {
            service.storeImages(principal, files, routeId);
        });
    }

    @Test
    void givenNonExistingRoute_whenStoreImages_thenThrowCampingRouteNotFoundException() {
        long routeId = 999L;
        String principal = "validUser";
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        MultipartFile[] files = new MultipartFile[]{multipartFile};

        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.storeImages(principal, files, routeId);
        });
    }

    @Test
    void givenUnauthorizedUser_whenStoreImages_thenThrowNotPermittedException() {
        long routeId = 1L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        MultipartFile[] files = new MultipartFile[]{multipartFile};

        assertThrows(NotPermittedException.class, () -> {
            service.storeImages("otherUser", files, routeId);
        });
    }

    @Test
    void givenFileWithoutExtension_whenStoreImages_thenFileStoredSuccessfully() throws IOException {
        long routeId = 2L;
        String principal = "validUser";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("filewithoutdot");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("file content".getBytes()));

        MultipartFile[] files = new MultipartFile[]{file};

        service.storeImages(principal, files, routeId);

        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        assertTrue(Files.exists(routeDir));
        assertTrue(Files.list(routeDir).findFirst().isPresent());
    }

    @Test
    void givenIOException_whenStoreImages_thenThrowCampingRouteImageStorageException() throws IOException {
        long routeId = 3L;
        String principal = "validUser";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        MultipartFile[] files = new MultipartFile[]{file};

        assertThrows(CampingRouteImageStorageException.class, () -> {
            service.storeImages(principal, files, routeId);
        });
    }

    @Test
    void givenValidFiles_whenStoreImages_thenFilesStoredSuccessfully() throws IOException {
        long routeId = 1L;
        String principal = "validUser";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);

        when(file1.isEmpty()).thenReturn(false);
        when(file1.getOriginalFilename()).thenReturn("image1.png");
        when(file1.getInputStream()).thenReturn(new ByteArrayInputStream("file content".getBytes()));

        when(file2.isEmpty()).thenReturn(false);
        when(file2.getOriginalFilename()).thenReturn("image2.jpg");
        when(file2.getInputStream()).thenReturn(new ByteArrayInputStream("file content".getBytes()));

        MultipartFile[] files = new MultipartFile[]{file1, file2};

        service.storeImages(principal, files, routeId);

        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        assertTrue(Files.exists(routeDir));
        assertEquals(2, Files.list(routeDir).count());
    }

    @Test
    void givenNonExistingDirectory_whenStoreImages_thenDirectoryCreated() throws IOException {
        long routeId = 1L;
        String principal = "validUser";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("image.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("file content".getBytes()));

        MultipartFile[] files = new MultipartFile[]{file};

        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        assertFalse(Files.exists(routeDir));

        service.storeImages(principal, files, routeId);

        assertTrue(Files.exists(routeDir));
    }

    @Test
    void givenExistingDirectoryWithFiles_whenGetImageNames_thenReturnListOfFiles() throws IOException {
        long routeId = 2L;
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve("image1.png"));
        Files.createFile(routeDir.resolve("image2.jpg"));

        // Note: getImageNames does not require user validation, so no stubbing needed.

        ResponseEntity<CampingRouteImageNamesDto> response = service.getImageNames(routeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CampingRouteImageNamesDto dto = response.getBody();
        assertNotNull(dto);
        List<String> imageNames = dto.getImageNames();
        assertEquals(2, imageNames.size());
        assertTrue(imageNames.contains("image1.png"));
        assertTrue(imageNames.contains("image2.jpg"));
    }

    @Test
    void givenNonExistingDirectory_whenGetImageNames_thenThrowCampingRouteImageNotFound() {
        long routeId = 3L;
        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.getImageNames(routeId);
        });
    }

    @Test
    void givenExistingFile_whenGetImage_thenReturnResource() throws IOException {
        long routeId = 4L;
        String imageName = "image.png";
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.write(routeDir.resolve(imageName), "test".getBytes());

        ResponseEntity<Resource> response = service.getImage(routeId, imageName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Resource resource = response.getBody();
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void givenNonExistingFile_whenGetImage_thenThrowCampingRouteImageNotFound() {
        long routeId = 5L;
        String imageName = "no_such_image.png";
        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.getImage(routeId, imageName);
        });
    }

    @Test
    void givenAuthorizedUserAndExistingFile_whenDeleteImage_thenFileDeleted() throws IOException {
        long routeId = 6L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        String imageName = "delete_me.png";
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve(imageName));

        ResponseEntity<Void> response = service.deleteImage(principal, routeId, imageName);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(Files.exists(routeDir.resolve(imageName)));
    }

    @Test
    void givenAuthorizedUserAndNonExistingFile_whenDeleteImage_thenThrowCampingRouteImageNotFound() {
        long routeId = 7L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        String imageName = "nonexistent.png";

        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.deleteImage(principal, routeId, imageName);
        });
    }

    @Test
    void givenUnauthorizedUser_whenDeleteImage_thenThrowNotPermittedException() throws IOException {
        long routeId = 8L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        String imageName = "some.png";
        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve(imageName));

        assertThrows(NotPermittedException.class, () -> {
            service.deleteImage("otherUser", routeId, imageName);
        });
    }

    @Test
    void givenNonExistingRoute_whenDeleteImage_thenThrowCampingRouteNotFoundException() {
        long routeId = 999L;
        String principal = "someUser";
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.deleteImage(principal, routeId, "whatever.png");
        });
    }

    @Test
    void givenAuthorizedUserAndExistingDirectory_whenDeleteAllImages_thenAllDeleted() throws IOException {
        long routeId = 10L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve("img1.png"));
        Files.createFile(routeDir.resolve("img2.png"));

        ResponseEntity<Void> response = service.deleteAllImage(principal, routeId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(Files.exists(routeDir));
    }

    @Test
    void givenAuthorizedUserAndNonExistingDirectory_whenDeleteAllImages_thenThrowCampingRouteImageNotFound() {
        long routeId = 11L;
        String principal = "owner";
        CampingRouteEntity route = mockRouteEntity(principal, routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        assertThrows(CampingRouteImageNotFound.class, () -> {
            service.deleteAllImage(principal, routeId);
        });
    }

    @Test
    void givenUnauthorizedUser_whenDeleteAllImages_thenThrowNotPermittedException() throws IOException {
        long routeId = 12L;
        CampingRouteEntity route = mockRouteEntity("owner", routeId);
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.of(route));

        Path routeDir = tempDir.resolve("files").resolve("camping_route_images").resolve(String.valueOf(routeId));
        Files.createDirectories(routeDir);
        Files.createFile(routeDir.resolve("img.png"));

        assertThrows(NotPermittedException.class, () -> {
            service.deleteAllImage("otherUser", routeId);
        });
    }

    @Test
    void givenNonExistingRoute_whenDeleteAllImages_thenThrowCampingRouteNotFoundException() {
        long routeId = 999L;
        String principal = "someone";
        when(campingRouteRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(CampingRouteNotFoundException.class, () -> {
            service.deleteAllImage(principal, routeId);
        });
    }
}
