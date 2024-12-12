package ee.taltech.iti03022024backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "view")
public class ViewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // automatically set the date when the view is created
    @CreatedDate
    @Column(name = "created_at")
    private Date date = new Date();

    @ManyToOne
    @JoinColumn(name = "camping_route_id")
    private CampingRouteEntity campingRoute;
}
