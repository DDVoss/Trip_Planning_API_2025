package app.dtos;

import app.entities.Guide;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class GuideDTO {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private Integer yearsOfExperience;

    public GuideDTO(Guide guide) {
        this.id = guide.getId();
        this.name = guide.getName();
        this.email = guide.getEmail();
        this.phone = guide.getPhone();
        this.yearsOfExperience = guide.getYearsOfExperience();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof GuideDTO guideDTO)) return false;

        return getId().equals(guideDTO.getId());
    }

    @Override
    public int hashCode()
    {
        return getId().hashCode();
    }
}
