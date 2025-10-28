package app.daos.impl;

import app.daos.IDAO;
import app.dtos.TripDTO;
import app.entities.Trip;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TripDAO implements IDAO<TripDTO, Integer>  {

    private static TripDAO instance;
    private static EntityManagerFactory emf;

    public static TripDAO getInstance(EntityManagerFactory _emf) {
        emf = _emf;
        if (instance == null) {
            instance = new TripDAO();
        }
        return instance;
    }


    @Override
    public TripDTO read(Integer id) {
       try (EntityManager em = emf.createEntityManager()) {
           Trip trip = em.find(Trip.class, id);
           if (trip != null) {
               return new TripDTO(trip);
           } else {
               return null;
           }
       }
    }

    @Override
    public List<TripDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<TripDTO> query = em.createQuery("SELECT new app.dtos.TripDTO(t) FROM Trip t", TripDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public TripDTO create(TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = new Trip(tripDTO);
            em.persist(trip);
            em.getTransaction().commit();
            return new TripDTO(trip);
        }
    }

    @Override
    public TripDTO update(Integer integer, TripDTO tripDTO) {
        try (EntityManager em = emf.createEntityManager())  {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, integer);
            trip.setName(tripDTO.getName());
            trip.setStart(tripDTO.getStart());
            trip.setEnd(tripDTO.getEnd());
            trip.setPrice(tripDTO.getPrice());
            trip.setLocation(tripDTO.getLocation());
            trip.setCategory(tripDTO.getCategory());
            Trip mergedTrip = em.merge(trip);
            em.getTransaction().commit();
            return mergedTrip != null ? new TripDTO(mergedTrip) : null;
        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Trip trip = em.find(Trip.class, integer);
            if (trip != null) {
                em.remove(trip);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager())  {
            Trip trip = em.find(Trip.class, integer);
            return trip != null;
        }
    }
}
