package dat.routes;

import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
import dat.routes.PlaylistRoute;


import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final HotelRoute hotelRoute = new HotelRoute();
    private final RoomRoute roomRoute = new RoomRoute();

    public EndpointGroup getRoutes() {
        return () -> {
                path("/hotels", hotelRoute.getRoutes());
                path("/rooms", roomRoute.getRoutes());
                path("/playlists", new PlaylistRoute().getRoutes());

        };
    }
}
