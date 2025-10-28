package app.daos.impl;

import app.daos.IDAO;
import app.dtos.GuideDTO;
import app.entities.Guide;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GuideDAO implements IDAO<GuideDTO, Integer> {

    private static GuideDAO instance;
    private static EntityManagerFactory emf;

    public static GuideDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GuideDAO();
        }
        return instance;
    }


    @Override
    public GuideDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager())  {
            Guide guide = em.find(Guide.class, id);
            if (guide != null)  {
                return new GuideDTO(guide);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<GuideDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<GuideDTO> query = em.createQuery("SELECT new app.dtos.GuideDTO(g) FROM Guide g", GuideDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public GuideDTO create(GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Guide guide = new Guide(guideDTO);
            em.persist(guide);
            em.getTransaction().commit();
            return new GuideDTO(guide);
        }
    }

    @Override
    public GuideDTO update(Integer integer, GuideDTO guideDTO) {
        try (EntityManager em = emf.createEntityManager())  {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, integer);
            guide.setName(guideDTO.getName());
            guide.setEmail(guideDTO.getEmail());
            guide.setPhone(guideDTO.getPhone());
            guide.setYearsOfExperience(guideDTO.getYearsOfExperience());
            Guide mergedGuide = em.merge(guide);
            em.getTransaction().commit();
            return mergedGuide != null ? new GuideDTO(mergedGuide) : null;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager())  {
            em.getTransaction().begin();
            Guide guide = em.find(Guide.class, integer);
            if (guide != null)  {
                em.remove(guide);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager())  {
            Guide guide = em.find(Guide.class, integer);
            return guide != null;

        }
    }
}
