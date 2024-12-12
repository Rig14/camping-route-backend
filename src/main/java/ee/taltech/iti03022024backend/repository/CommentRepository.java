package ee.taltech.iti03022024backend.repository;

import ee.taltech.iti03022024backend.entity.CampingRouteEntity;
import ee.taltech.iti03022024backend.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByCampingRoute(CampingRouteEntity campingRouteEntity);
    List<CommentEntity> findByUser_Id(long id);
}
