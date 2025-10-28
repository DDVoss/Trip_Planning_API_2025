package app.entities;

import app.dtos.GuideDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "guide")

public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "guide_name", nullable = false, length = 100)
    private String name;

    @Setter
    @Column(name = "guide_email", nullable = false, unique = true, length = 100)
    private String email;

    @Setter
    @Column(name = "guide_phone", nullable = false, unique = true, length = 11)
    private String phone;

    @Setter
    @Column(name = "years_of_experience", nullable = false)
    private Integer yearsOfExperience;

    @OneToMany(mappedBy = "guide", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Trip> trips = new HashSet<>();


    public void setTrip(Set<Trip> trips)    {
        if (trips != null) {
            this.trips = trips;
            for (Trip trip : trips) {
                trip.setGuide(this);
            }
        }
    }


    public void addTrip(Trip trip)  {
        if (trip != null) {
            this.trips.add(trip);
            trip.setGuide(this);
        }
    }

    public Guide(String name, String email, String phone, Integer yearsOfExperience) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Guide (GuideDTO guideDTO) {
        this.id = guideDTO.getId();
        this.name = guideDTO.getName();
        this.email = guideDTO.getEmail();
        this.phone = guideDTO.getPhone();
        this.yearsOfExperience = guideDTO.getYearsOfExperience();
    }
}

