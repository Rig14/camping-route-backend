package ee.taltech.iti03022024backend.entity;

import org.springframework.data.jpa.domain.Specification;

public class CampingRouteSpecifications {
    public static Specification<CampingRouteEntity> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return null;
            }
            String likePattern = '%' + keyword.toLowerCase() + '%';
            System.out.println("This is the like pattern: " + likePattern);
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), likePattern),
                    criteriaBuilder.like(root.get("description"), likePattern),
                    criteriaBuilder.like(root.get("location"), likePattern)
            );
        };
    }
}
