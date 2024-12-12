package ee.taltech.iti03022024backend.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import ee.taltech.iti03022024backend.dto.CampingRouteDto;
import ee.taltech.iti03022024backend.dto.CampingRouteSearchRequest;
import ee.taltech.iti03022024backend.dto.PageResponse;
import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.UserEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.exception.NotPermittedException;
import ee.taltech.iti03022024backend.mapping.CampingRouteMapper;
import ee.taltech.iti03022024backend.mapping.CampingRouteMapperImpl;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import java.util.*;


@ExtendWith(MockitoExtension.class)
class CampingRouteServiceTest {

    @Mock
    private CampingRouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private CampingRouteMapper mapper = new CampingRouteMapperImpl();

    private CampingRouteService campingRouteService;

    @BeforeEach
    void setUp() {
        campingRouteService = new CampingRouteService(routeRepository, userRepository, mapper);
    }

    @Test
    void createCampingRoute_shouldReturnCreatedCampingRoute() {
        // given
        String principal = "testUser";
        CampingRouteDto dto = new CampingRouteDto();
        dto.setName("Test Route");

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(principal);

        CampingRouteEntity routeEntity = new CampingRouteEntity();
        routeEntity.setUser(userEntity);

        when(userRepository.findByUsername(principal)).thenReturn(Optional.of(userEntity));
        when(mapper.toEntity(dto)).thenReturn(routeEntity);
        when(routeRepository.save(any(CampingRouteEntity.class))).thenReturn(routeEntity);
        when(mapper.toDto(routeEntity)).thenReturn(dto);

        // when
        ResponseEntity<CampingRouteDto> response = campingRouteService.createCampingRoute(principal, dto);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(routeRepository, times(1)).save(any(CampingRouteEntity.class));
    }

    @Test
    void getCampingRoutesByUserId_shouldReturnListOfRoutes() {
        // given
        CampingRouteSearchRequest searchRequest = new CampingRouteSearchRequest();
        searchRequest.setKeyword("1"); // userId as a string
        searchRequest.setPageNumber(0);
        searchRequest.setPageSize(10);

        List<CampingRouteEntity> routes = new ArrayList<>();
        CampingRouteEntity route = new CampingRouteEntity();
        routes.add(route);

        Page<CampingRouteEntity> page = new PageImpl<>(routes, PageRequest.of(0, 10), routes.size());

        when(routeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(mapper.toDto(route)).thenReturn(new CampingRouteDto());

        // when
        ResponseEntity<PageResponse<CampingRouteDto>> response = campingRouteService.getCampingRoutesByUserId(searchRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(routeRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getCampingRoute_shouldReturnCampingRoute() {
        // given
        long routeId = 1L;
        CampingRouteEntity routeEntity = new CampingRouteEntity();
        CampingRouteDto routeDto = new CampingRouteDto();

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(routeEntity));
        when(mapper.toDto(routeEntity)).thenReturn(routeDto);

        // when
        ResponseEntity<CampingRouteDto> response = campingRouteService.getCampingRoute(routeId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(routeDto);
    }

    @Test
    void getCampingRoute_shouldThrowCampingRouteNotFoundException_whenRouteNotFound() {
        // given
        long routeId = 1L;

        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        // when & Assert
        assertThatThrownBy(() -> campingRouteService.getCampingRoute(routeId))
                .isInstanceOf(CampingRouteNotFoundException.class)
                .hasMessageContaining("Camping route with id of " + routeId + " does not exist");
    }

    @Test
    void deleteCampingRoute_shouldDeleteCampingRoute() {
        // given
        String principal = "testUser";
        long routeId = 1L;

        CampingRouteEntity routeEntity = new CampingRouteEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(principal);
        routeEntity.setUser(userEntity);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(routeEntity));

        // when
        ResponseEntity<Void> response = campingRouteService.deleteCampingRoute(principal, routeId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(routeRepository, times(1)).deleteById(routeId);
    }

    @Test
    void deleteCampingRoute_shouldThrowNotPermittedException_whenUserDoesNotOwnRoute() {
        // given
        String principal = "otherUser";
        long routeId = 1L;

        CampingRouteEntity routeEntity = new CampingRouteEntity();
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        routeEntity.setUser(userEntity);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(routeEntity));

        // when & Assert
        assertThatThrownBy(() -> campingRouteService.deleteCampingRoute(principal, routeId))
                .isInstanceOf(NotPermittedException.class)
                .hasMessageContaining("You are not permitted to delete this camping route.");
    }

    @Test
    void getCampingRoutesForHomepage_shouldReturnPageOfCampingRoutes() {
        // given
        CampingRouteSearchRequest searchRequest = new CampingRouteSearchRequest();
        searchRequest.setPageNumber(0);
        searchRequest.setPageSize(10);

        Page<CampingRouteEntity> page = new PageImpl<>(Collections.singletonList(new CampingRouteEntity()));

        when(routeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // when
        ResponseEntity<PageResponse<CampingRouteDto>> response = campingRouteService.getCampingRoutesForHomepage(searchRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    void findCampingRoute_shouldReturnPageOfCampingRoutes_withSearch() {
        // given
        CampingRouteSearchRequest searchRequest = new CampingRouteSearchRequest();
        searchRequest.setPageNumber(0);
        searchRequest.setPageSize(10);
        searchRequest.setKeyword("test");

        Page<CampingRouteEntity> page = new PageImpl<>(Collections.singletonList(new CampingRouteEntity()));

        when(routeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // when
        ResponseEntity<PageResponse<CampingRouteDto>> response = campingRouteService.findCampingRoute(searchRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }
}
