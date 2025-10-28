package app.daos.impl;

import app.daos.IDAO;
import app.dtos.TripDTO;
import app.entities.Guide;
import app.entities.Trip;
import app.enums.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    public void populate()  {
        try (EntityManager em = emf.createEntityManager())  {
            em.getTransaction().begin();

            Guide daniel = new Guide("Daniel", "daniel@eksempel.dk", "10203040", 2);
            Guide jon = new Guide("Jon", "jon@eksempel.dk", "40506070", 5);
            Guide thomas = new Guide("Thomas", "thomas@eksempel.dk", "70809010", 10);

            Set<Trip> danielTrips = getDanielsTrips(daniel);
            Set<Trip> jonTrips = getJonsTrips(jon);
            Set<Trip> thomasTrips = getThomasTrips(thomas);
            daniel.setTrip(danielTrips);
            jon.setTrip(jonTrips);
            thomas.setTrip(thomasTrips);

            em.persist(daniel);
            em.persist(jon);
            em.persist(thomas);

            em.getTransaction().commit();
        }

    }

    @NotNull
    private static Set<Trip> getDanielsTrips(Guide daniel) {
        Trip trip1 = new Trip(
                "Copenhagen City Tour",
                LocalDateTime.of(2026, 7, 1, 10, 0, 0),
                LocalDateTime.of(2026, 7, 1, 12, 0, 0),
                37.7749,
                122.94,
                daniel,
                Category.City
        );

        Trip trip2 = new Trip(
                "Historical Landmarks",
                LocalDateTime.of(2026, 7, 2, 9, 0, 0),
                LocalDateTime.of(2026, 7, 2, 11, 30, 0),
                34.0522,
                118.00,
                daniel,
                Category.City
        );

        Trip trip3 = new Trip(
                "Nature Hike",
                LocalDateTime.of(2026, 7, 3, 8, 0, 0),
                LocalDateTime.of(2026, 7, 3, 14, 0, 0),
                36.1699,
                115.50,
                daniel,
                Category.Forest
        );

        Trip[] tripArray = {trip1, trip2, trip3};
        return Set.of(tripArray);
    }

    private static Set<Trip> getJonsTrips(Guide jon) {
        Trip trip1 = new Trip(
                "Mountain Adventure",
                LocalDateTime.of(2026, 8, 5, 7, 0, 0),
                LocalDateTime.of(2026, 8, 5, 15, 0, 0),
                40.7128,
                74.00,
                jon,
                Category.Mountain
        );

        Trip trip2 = new Trip(
                "River Rafting",
                LocalDateTime.of(2026, 8, 6, 9, 0, 0),
                LocalDateTime.of(2026, 8, 6, 13, 0, 0),
                39.7392,
                104.99,
                jon,
                Category.Lake
        );

        Trip[] tripArray = {trip1, trip2};
        return Set.of(tripArray);
    }

    private static Set<Trip> getThomasTrips(Guide thomas) {
        Trip trip1 = new Trip(
                "Mountain Exploration",
                LocalDateTime.of(2026, 9, 10, 6, 0, 0),
                LocalDateTime.of(2026, 9, 10, 18, 0, 0),
                33.4484,
                112.07,
                thomas,
                Category.Mountain
        );

        Trip trip2 = new Trip(
                "Coastal Walk",
                LocalDateTime.of(2026, 9, 11, 8, 0, 0),
                LocalDateTime.of(2026, 9, 11, 12, 0, 0),
                25.7617,
                80.19,
                thomas,
                Category.Beach
        );

        Trip[] tripArray = {trip1, trip2};
        return Set.of(tripArray);
    }
}
