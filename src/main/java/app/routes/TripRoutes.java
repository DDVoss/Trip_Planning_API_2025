package app.routes;

import app.controllers.impl.TripController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TripRoutes {

    private final TripController tripController = new TripController();

    protected EndpointGroup getRoutes() {

        return () -> {
           get("/", tripController::readAll);
           get("/{id}", tripController::read);
           post("/", tripController::create);
           put("/{id}", tripController::update);
           delete("/{id}", tripController::delete);
        };
    }
}
