package app.routes;

import app.controllers.impl.TripController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private final TripRoutes tripRoutes = new TripRoutes();
    private final GuideRoutes guideRoutes = new GuideRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", ctx -> ctx.result("Welcome to the API"));
            path("/trip", tripRoutes.getRoutes());
            path("/guide", guideRoutes.getRoutes());
        };
    }
}
