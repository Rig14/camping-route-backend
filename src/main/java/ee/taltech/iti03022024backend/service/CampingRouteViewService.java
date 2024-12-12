package ee.taltech.iti03022024backend.service;

import ee.taltech.iti03022024backend.dto.ViewDto;
import ee.taltech.iti03022024backend.entity.ViewEntity;
import ee.taltech.iti03022024backend.exception.CampingRouteNotFoundException;
import ee.taltech.iti03022024backend.repository.CampingRouteRepository;
import ee.taltech.iti03022024backend.repository.UserRepository;
import ee.taltech.iti03022024backend.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CampingRouteViewService {
    private static final String CAMPING_ROUTE_DOES_NOT_EXIST = "Camping route with id of %d does not exist";

    private final ViewRepository viewRepository;
    private final CampingRouteRepository campingRouteRepository;

    public ResponseEntity<ViewDto> addViewForCampingRoute(long campingRouteId) {
        log.info("Adding view for camping route with id {}", campingRouteId);

        var campingRoute = campingRouteRepository.findById(campingRouteId).orElseThrow(() -> new CampingRouteNotFoundException(String.format(CAMPING_ROUTE_DOES_NOT_EXIST, campingRouteId)));

        var view = new ViewEntity();
        view.setCampingRoute(campingRoute);
        viewRepository.save(view);

        log.info("View added for camping route with id {}", campingRouteId);

        return ResponseEntity.ok(new ViewDto(campingRouteId, viewRepository.countByCampingRoute_Id(campingRouteId)));
    }

    public ResponseEntity<ViewDto> getViewCountForCampingRoute(long campingRouteId) {
        log.info("Fetching view count for camping route with id {}", campingRouteId);

        if (campingRouteRepository.findById(campingRouteId).isEmpty()) {
            throw new CampingRouteNotFoundException("Camping route with id of " + campingRouteId + " does not exist");
        }

        return ResponseEntity.ok(new ViewDto(campingRouteId, viewRepository.countByCampingRoute_Id(campingRouteId)));
    }
}
