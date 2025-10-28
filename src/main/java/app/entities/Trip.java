package app.entities;

import app.dtos.TripDTO;
import app.enums.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

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
    @Column(name = "start", nullable = false, length = 100)
    private LocalDateTime start;

    @Setter
    @Column(name = "end", nullable = false, length = 100)
    private LocalDateTime end;

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

    public void assignGuide(Guide guide) {
        if (this.guide != null) {
            this.guide.getTrips().remove(this);
        }
        this.guide = guide;
        if (guide != null) {
            guide.getTrips().add(this);
        }
    }

    public Trip(String name, LocalDateTime start, LocalDateTime end, double location, double price, Guide guide, Category category) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.location = location;
        this.price = price;
        this.guide = guide;
        this.category = category;
    }

    public Trip(TripDTO tripDTO) {
        this.id = tripDTO.getId();
        this.name = tripDTO.getName();
        this.start = tripDTO.getStart();
        this.end = tripDTO.getEnd();
        this.location = tripDTO.getLocation();
        this.price = tripDTO.getPrice();
        this.category = tripDTO.getCategory();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o == null || getClass() != o.getClass())) return false;

        Trip trip = (Trip) o;
        return Objects.equals(name, trip.name) &&
                Objects.equals(start, trip.start) &&
                Objects.equals(end, trip.end) &&
                Objects.equals(location, trip.location) &&
                Objects.equals(price, trip.price) &&
                Objects.equals(category, trip.category) &&
                Objects.equals(guide, trip.guide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, start, end, location, price, category, guide);
    }
}
