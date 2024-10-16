package ee.taltech.iti03022024backend.repository;

import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampingRouteRepository extends JpaRepository<CampingRouteEntity, Long> {
    List<CampingRouteEntity> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location);
}
