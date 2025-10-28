package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.impl.TripDAO;
import app.dtos.TripDTO;
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
                .check(h -> h.getStart() != null && !h.getStart().isBefore(h.getEnd()), "Trip departure must be set and before arrival")
                .check(h -> h.getEnd() != null && !h.getEnd().isAfter(h.getStart()), "Trip arrival must be set and after departure")
                .check(h -> h.getLocation() >= 0, "Trip location must be non-negative")
                .check(h -> h.getPrice() >= 0, "Trip price must be non-negative")
                .check(h -> h.getCategory() != null, "Trip category must be set")
                .get();
    }
}
