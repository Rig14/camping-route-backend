package ee.taltech.iti03022024backend.repository;

import ee.taltech.iti03022024backend.entity.ViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewRepository extends JpaRepository<ViewEntity, Long> {
    // find all by camping route ID and get the count
    long countByCampingRoute_Id(long id);

    // delete by camping route ID
    void deleteByCampingRoute_Id(long id);
}
