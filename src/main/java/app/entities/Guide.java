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

    @OneToMany
    @JoinColumn(name = "guide_id")
    private Set<Trip> trips = new HashSet<>();

    public Guide(String name, String email, String phone, Integer yearsOfExperience) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Guide (GuideDTO guideDTO) {
        this.name = guideDTO.getName();
        this.email = guideDTO.getEmail();
        this.phone = guideDTO.getPhone();
        this.yearsOfExperience = guideDTO.getYearsOfExperience();
    }
}
