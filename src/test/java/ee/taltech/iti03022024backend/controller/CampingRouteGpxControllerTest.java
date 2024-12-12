package ee.taltech.iti03022024backend.controller;

import ee.taltech.iti03022024backend.AbstractIntegrationTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class CampingRouteGpxControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @Transactional
    void givenGpxFileAndCampingRouteId_whenAddGpxFileToCampingRoute_thenReturnsOK() throws Exception {
        File originalFile = new File("src/test/resources/anija.gpx");
        MockMultipartFile upload = new MockMultipartFile("file", "anija.gpx",
                MediaType.TEXT_XML_VALUE,
                Files.readAllBytes(originalFile.toPath()));

        mvc.perform(multipart("/api/camping_routes/gpx/{id}", 1L)
                        .file(upload)
                        .with(user("user1")))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void givenCampingRouteId_whenGetGpx_thenReturnOkAndGpxFile() throws Exception {
        givenGpxFileAndCampingRouteId_whenAddGpxFileToCampingRoute_thenReturnsOK();
        mvc.perform(get("/api/public/camping_routes/gpx/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    void givenCampingRouteId_whenDeleteGpx_thenReturnsNoContent() throws Exception {
        givenGpxFileAndCampingRouteId_whenAddGpxFileToCampingRoute_thenReturnsOK();
        mvc.perform(delete("/api/camping_routes/gpx/{id}", 1L)
                        .with(user("user1")))
                .andExpect(status().isNoContent());
    }

}
