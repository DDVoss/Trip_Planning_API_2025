package app.dtos;

import app.entities.Trip;
import app.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class TripDTO {
    private Integer id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private double location;
    private double price;
    private Category category;
    private Set<GuideDTO> guides = new HashSet<>();

    public TripDTO(Trip trip)   {
        this.id = trip.getId();
        this.name = trip.getName();
        this.start = trip.getStart();
        this.end = trip.getEnd();
        this.location = trip.getLocation();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
    }

}
