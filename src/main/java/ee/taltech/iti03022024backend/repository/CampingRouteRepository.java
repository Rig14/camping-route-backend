package ee.taltech.iti03022024backend.repository;

import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CampingRouteRepository extends JpaRepository<CampingRouteEntity, Long>, JpaSpecificationExecutor<CampingRouteEntity> {
    List<CampingRouteEntity> findByUser_Id(long id);
}
