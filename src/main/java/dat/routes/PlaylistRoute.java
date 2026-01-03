package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

import dat.controllers.impl.PlaylistController;

import dat.security.enums.Role;



public class PlaylistRoute {
    private final PlaylistController controller = new PlaylistController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/me", controller::myPlaylists, Role.USER, Role.ADMIN);
            post("/", controller::create, Role.USER, Role.ADMIN);

            path("/{id}", () -> {
                get(controller::read, Role.USER, Role.ADMIN);

                // add song
                post("/songs", controller::addSong, Role.USER, Role.ADMIN);

                // remove song from playlist
                delete("/songs/{songId}", controller::removeSong, Role.USER, Role.ADMIN);

                // delete entire playlist
                delete(controller::deletePlaylist, Role.USER, Role.ADMIN);
            });
        };
    }
}
