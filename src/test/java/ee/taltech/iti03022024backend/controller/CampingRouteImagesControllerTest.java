package ee.taltech.iti03022024backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.dto.CampingRouteImageNamesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CampingRouteImagesControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Path rootDir = Paths.get("files", "camping_route_images");

    @BeforeEach
    void setUp() throws Exception {
        if (Files.exists(rootDir)) {
            Files.walk(rootDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectories(rootDir);
    }

    @Test
    void givenImagesAndCampingRouteId_whenAddImagesToCampingRoute_thenReturnsOk() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg",
                MediaType.IMAGE_JPEG_VALUE, "test-image-1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.png",
                MediaType.IMAGE_PNG_VALUE, "test-image-2".getBytes());

        mvc.perform(multipart("/api/camping_routes/images/{id}", 1L)
                        .file(file1)
                        .file(file2)
                        .with(user("user1")))
                .andExpect(status().isOk());
    }

    @Test
    void givenCampingRouteId_whenGetImageNames_thenReturnsOkAndImageNames() throws Exception {
        givenImagesAndCampingRouteId_whenAddImagesToCampingRoute_thenReturnsOk();

        mvc.perform(get("/api/public/camping_routes/images/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.imageNames").isArray());
    }

    @Test
    void givenCampingRouteIdAndImageName_whenGetImage_thenReturnsOkAndImage() throws Exception {
        givenImagesAndCampingRouteId_whenAddImagesToCampingRoute_thenReturnsOk();

        var result = mvc.perform(get("/api/public/camping_routes/images/{id}", 1L))
                .andReturn();
        CampingRouteImageNamesDto responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CampingRouteImageNamesDto.class
        );

        String actualImageName = responseDto.getImageNames().get(0);
        mvc.perform(get("/api/public/camping_routes/images/{id}/{imageName}", 1L, actualImageName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenCampingRouteIdAndImageName_whenDeleteImage_thenReturnsNoContent() throws Exception {
        givenImagesAndCampingRouteId_whenAddImagesToCampingRoute_thenReturnsOk();

        var result = mvc.perform(get("/api/public/camping_routes/images/{id}", 1L))
                .andReturn();
        CampingRouteImageNamesDto responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                CampingRouteImageNamesDto.class
        );

        String imageNameToDelete = responseDto.getImageNames().get(0);
        mvc.perform(delete("/api/camping_routes/images/{id}/{imageName}", 1L, imageNameToDelete)
                        .with(user("user1")))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/public/camping_routes/images/{id}/{imageName}", 1L, imageNameToDelete))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenCampingRouteId_whenDeleteAllImages_thenReturnsNoContent() throws Exception {
        givenImagesAndCampingRouteId_whenAddImagesToCampingRoute_thenReturnsOk();

        mvc.perform(delete("/api/camping_routes/images/{id}", 1L)
                        .with(user("user1")))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/public/camping_routes/images/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}
