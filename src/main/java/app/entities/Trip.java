package app.entities;

import app.dtos.TripDTO;
import app.enums.Category;
import app.entities.Guide;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "trip")

public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "trip_name", nullable = false, unique = true, length = 100)
    private String name;

    @Setter
    @Column(name = "departure", nullable = false, length = 100)
    private LocalDateTime departure;

    @Setter
    @Column(name = "arrival", nullable = false, length = 100)
    private LocalDateTime arrival;

    @Setter
    @Column(name = "location", nullable = false, length = 200)
    private double location;

    @Setter
    @Column(name = "price", nullable = false)
    private double price;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_category", nullable = false)
    private Category category;

    @Setter
    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide;


    public Trip(String name, LocalDateTime departure, LocalDateTime arrival, double location, double price, Guide guide, Category category) {
        this.name = name;
        this.departure = departure;
        this.arrival = arrival;
        this.location = location;
        this.price = price;
        this.guide = guide;
        this.category = category;
    }

    public Trip(TripDTO tripDTO) {
        this.id = tripDTO.getId();
        this.name = tripDTO.getName();
        this.departure = tripDTO.getDeparture();
        this.arrival = tripDTO.getArrival();
        this.location = tripDTO.getLocation();
        this.price = tripDTO.getPrice();
        this.category = tripDTO.getCategory();
    }
}
