package ee.taltech.iti03022024backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "camping_route_id")
    private CampingRouteEntity campingRoute;
}
