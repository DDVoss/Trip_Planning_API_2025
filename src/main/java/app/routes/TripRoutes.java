package app.routes;

import app.controllers.impl.TripController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private final TripController tripController = new TripController();

    protected EndpointGroup getRoutes() {

        return () -> {
            // Specialized Endpoints
            post("/populate", tripController::populate, Role.ANYONE);
            get("/guide/totalprice", tripController::getTotalPriceOfTrips, Role.ANYONE);
            put("/assign/{tripId}/guide/{guideId}", tripController::assignGuideToTrip, Role.USER);

            // CRUD Endpoints
            get("/", tripController::readAll, Role.ANYONE);
            post("/", tripController::create, Role.USER);

            // Read, Update, Delete by ID
            get("/{id}", tripController::read, Role.ANYONE);
            put("/{id}", tripController::update, Role.USER);
            delete("/{id}", tripController::delete, Role.USER);
        };
    }
}
