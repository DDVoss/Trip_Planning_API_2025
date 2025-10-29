package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.TripDAO;
import app.dtos.TripDTO;
import app.entities.Trip;
import app.enums.Category;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TripController implements IController<TripDTO, Integer> {

    private final TripDAO dao;

    public TripController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = TripDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();

        // DTO
        TripDTO tripDTO = dao.read(id);

        // Response
        if (tripDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(tripDTO, TripDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json("Trip not found");
        }
    }

    @Override
    public void readAll(Context ctx) {
        String categoryParam = ctx.queryParam("category");

        if (categoryParam != null && !categoryParam.isEmpty())  {
            getTripsByCategory(ctx);
        } else {
            // Request
            List<TripDTO> tripDTOList = dao.readAll();

            // Response
            if (tripDTOList != null && !tripDTOList.isEmpty()) {
                ctx.res().setStatus(200);
                ctx.json(tripDTOList, TripDTO.class);
            } else {
                ctx.res().setStatus(404);
                ctx.json("No trips found");
            }
        }
    }

    @Override
    public void create(Context ctx) {
        // Request
        TripDTO jsonRequest = ctx.bodyAsClass(TripDTO.class);

        // DTO
        TripDTO tripDTO = dao.create(jsonRequest);

        // Response
        ctx.res().setStatus(201);
        ctx.json(tripDTO, TripDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();

        // DTO
        TripDTO tripDTO = dao.update(id, validateEntity(ctx));

        // Response
        if (tripDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(tripDTO, TripDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json("Trip not found");
        }
    }

    @Override
    public void delete(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();
        dao.delete(id);

        // Response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public TripDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(TripDTO.class)
                .check(h -> h.getName() != null && !h.getName().isEmpty(), "Trip name must be set")
                .check(h -> h.getStart() != null && h.getStart().isBefore(h.getEnd()), "Trip start must be before end")
                .check(h -> h.getEnd() != null && h.getEnd().isAfter(h.getStart()), "Trip end must be after start")
                .check(h -> h.getLocation() >= 0, "Trip location must be non-negative")
                .check(h -> h.getPrice() >= 0, "Trip price must be non-negative")
                .check(h -> h.getCategory() != null, "Trip category must be set")
                .get();
    }


    public void populate(Context ctx) {
        dao.populate();
        ctx.res().setStatus(200);
        ctx.json("Trip data populated");
    }

    public void assignGuideToTrip(Context ctx) {
        // Request
        int tripId = ctx.pathParamAsClass("tripId", Integer.class).check(this::validatePrimaryKey, "Not a valid trip id").get();
        int guideId = ctx.pathParamAsClass("guideId", Integer.class).get();

        // Assign guide to trip
        boolean success = dao.AssignGuideToTrip(tripId, guideId);

        // Response
        if (success) {
            ctx.res().setStatus(200);
            ctx.json("Guide assigned to trip successfully");
        } else {
            ctx.res().setStatus(404);
            ctx.json("Trip or Guide not found");
        }
    }

    public void getTotalPriceOfTrips(Context ctx) {
        double totalPrice = dao.findTotalPriceForTrips();

        if (totalPrice >= 0) {
            ctx.res().setStatus(200);
            ctx.json("Total price of all trips: " + totalPrice);
        } else {
            ctx.res().setStatus(500);
            ctx.json("Error calculating total price");
        }
    }

    public void getTripsByCategory(Context ctx) {
        // Request
        String categoryParam = ctx.queryParam("category");

        try {
            Category category = Category.valueOf(categoryParam);

            // DTO
            List<TripDTO> trips = dao.findAllTripsByCategory(category);

            //Response
            if (trips != null && !trips.isEmpty()) {
                ctx.res().setStatus(200);
                ctx.json(trips, TripDTO.class);
            } else {
                ctx.res().setStatus(404);
                ctx.json("No trips found for category: " + category);
            }
        } catch (IllegalArgumentException e) {
            ctx.res().setStatus(400);
            ctx.json("Invalid category: " + categoryParam);
        }
    }
}
