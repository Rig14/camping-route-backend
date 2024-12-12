package ee.taltech.iti03022024backend.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
public class CampingRouteSpecifications {
    private CampingRouteSpecifications() {}

    public static Specification<CampingRouteEntity> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<CampingRouteEntity> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return null;
            }
            String likePattern = '%' + keyword.toLowerCase() + '%';
            log.info("This is the like pattern: {}", likePattern);
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), likePattern),
                    criteriaBuilder.like(root.get("description"), likePattern),
                    criteriaBuilder.like(root.get("location"), likePattern)
            );
        };
    }
}
