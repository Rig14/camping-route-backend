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
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import java.util.*;

class CampingRouteServiceTest {

    @Mock
    private CampingRouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CampingRouteMapper mapper;

    private CampingRouteService campingRouteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(dto);
        verify(routeRepository, times(1)).save(any(CampingRouteEntity.class));
    }

    @Test
    void getCampingRoutesByUserId_shouldReturnListOfRoutes() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        CampingRouteSearchRequest searchRequest = new CampingRouteSearchRequest();
        searchRequest.setKeyword(String.valueOf(user.getId()));
        searchRequest.setPageNumber(0);
        searchRequest.setPageSize(10);

        CampingRouteEntity routeEntity1 = new CampingRouteEntity();
        routeEntity1.setId(1L);
        routeEntity1.setUser(user);

        CampingRouteEntity routeEntity2 = new CampingRouteEntity();
        routeEntity2.setId(2L);
        routeEntity2.setUser(user);

        Page<CampingRouteEntity> page = new PageImpl<>(List.of(routeEntity1, routeEntity2),
                PageRequest.of(0, 10), 2);

        when(routeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(mapper.toDtoList(anyList())).thenReturn(Collections.singletonList(new CampingRouteDto()));

        ResponseEntity<PageResponse<CampingRouteDto>> response = campingRouteService.getCampingRoutesByUserId(searchRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        PageResponse<CampingRouteDto> pageResponse = response.getBody();
        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.getContent()).hasSize(2);
        assertThat(pageResponse.getTotalPages()).isEqualTo(1);
        assertThat(pageResponse.getTotalElements()).isEqualTo(2);
        verify(routeRepository).findAll(any(Specification.class), any(Pageable.class));
        verify(mapper, times(2)).toDto(any(CampingRouteEntity.class));
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
        assertThat(response.getStatusCode().value()).isEqualTo(200);
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
        assertThat(response.getStatusCode().value()).isEqualTo(204);
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
        when(mapper.toDtoList(anyList())).thenReturn(Collections.singletonList(new CampingRouteDto()));

        // when
        ResponseEntity<PageResponse<CampingRouteDto>> response = campingRouteService.getCampingRoutesForHomepage(searchRequest);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
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
        when(mapper.toDtoList(anyList())).thenReturn(Collections.singletonList(new CampingRouteDto()));

        // when
        ResponseEntity<PageResponse<CampingRouteDto>> response = campingRouteService.findCampingRoute(searchRequest);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }
}
