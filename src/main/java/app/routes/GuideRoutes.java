package app.routes;

import app.controllers.impl.GuideController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GuideRoutes {
    private final GuideController guideController = new GuideController();

    protected EndpointGroup getRoutes() {
        return () -> {
            get("/", guideController::readAll, Role.ANYONE);
            get("/{id}", guideController::read, Role.ANYONE);
            post("/", guideController::create, Role.USER);
            put("/{id}", guideController::update, Role.USER);
            delete("/{id}", guideController::delete, Role.USER);
        };
    }
}
