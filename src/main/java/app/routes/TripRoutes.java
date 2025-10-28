package app.routes;

import app.controllers.impl.TripController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private final TripController tripController = new TripController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/populate", tripController::populate, Role.ANYONE);
            get("/", tripController::readAll, Role.ANYONE);
            get("/{id}", tripController::read, Role.ANYONE);
            post("/", tripController::create, Role.USER);
            put("/{id}", tripController::update, Role.USER);
            delete("/{id}", tripController::delete, Role.ADMIN);
        };
    }
}
