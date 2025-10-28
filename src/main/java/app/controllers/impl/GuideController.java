package app.controllers.impl;

import app.config.HibernateConfig;
import app.controllers.IController;
import app.daos.IDAO;
import app.daos.impl.GuideDAO;
import app.daos.impl.TripDAO;
import app.dtos.GuideDTO;
import app.dtos.TripDTO;
import app.entities.Guide;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class GuideController implements IController<GuideDTO, Integer> {

    private final GuideDAO dao;

    public GuideController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = GuideDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();

        // DTO
        GuideDTO guideDTO = dao.read(id);

        // Response
        if (guideDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(guideDTO, GuideDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json("Trip not found");
        }
    }

    @Override
    public void readAll(Context ctx) {
        // Request
        List<GuideDTO> guideDTOList = dao.readAll();

        // Response
        if (guideDTOList != null && !guideDTOList.isEmpty()) {
            ctx.res().setStatus(200);
            ctx.json(guideDTOList, TripDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json("No trips found");
        }
    }

    @Override
    public void create(Context ctx) {
        // Request
        GuideDTO jsonRequest = ctx.bodyAsClass(GuideDTO.class);

        // DTO
        GuideDTO guideDTO = dao.create(jsonRequest);

        // Response
        ctx.res().setStatus(201);
        ctx.json(guideDTO, GuideDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // Request
        int id = ctx.pathParamAsClass("id", Integer.class).check(this::validatePrimaryKey, "Not a valid id").get();

        // DTO
        GuideDTO guideDTO = dao.update(id, validateEntity(ctx));

        // Response
        if (guideDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(guideDTO, GuideDTO.class);
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
    public GuideDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(GuideDTO.class)
                .check(g -> g.getName() != null && !g.getName().isEmpty(), "Guide name must not be empty")
                .check(g -> g.getEmail() != null && !g.getEmail().isEmpty(), "Guide email must not be empty")
                .check(g -> g.getPhone() != null && !g.getPhone().isEmpty(), "Phone number must not be null")
                .check(g -> g.getYearsOfExperience() >= 0, "Years of experience must be non-negative")
                .get();
    }
}
