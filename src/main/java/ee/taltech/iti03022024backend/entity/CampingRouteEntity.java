package ee.taltech.iti03022024backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "camping_route")
public class CampingRouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private String location;
    private String thumbnailUrl;

    @ManyToOne
    @JoinColumn(name = "user_data_id")
    private UserEntity user;

    @OneToMany(mappedBy = "campingRoute", cascade = CascadeType.ALL)
    private List<CommentEntity> comment; // one CampingRoute can have many comments

    @OneToMany(mappedBy = "campingRoute", cascade = CascadeType.ALL)
    private List<ViewEntity> views;
}
