package ee.taltech.iti03022024backend.entity;

import jakarta.persistence.*;
import jdk.jfr.MemoryAddress;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Entity
@Table(name = "CampingRoute")
public class CampingRouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private String location;
    private String thumbnailUrl;

    @ManyToOne
    private UserEntity user;

    @OneToMany
    private List<CommentEntity> comment; // one CampingRoute can have many comments
}
