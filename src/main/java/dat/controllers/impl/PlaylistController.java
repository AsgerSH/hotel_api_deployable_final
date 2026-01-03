package dat.controllers.impl;

import dat.daos.impl.PlaylistDAO;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import dat.dtos.PlaylistDTO;
import dat.dtos.CreatePlaylistDTO;
import dat.dtos.AddSongDTO;

import dat.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

public class PlaylistController {

    private final PlaylistDAO dao;

    public PlaylistController(){
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        dao = PlaylistDAO.getInstance(emf);
    }

    private String requireUsername(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null) throw new UnauthorizedResponse("No user in context");
        return user.getUsername();
    }

    public void create(Context ctx) {
        CreatePlaylistDTO dto = ctx.bodyAsClass(CreatePlaylistDTO.class);

        String ownerUsername = requireUsername(ctx);

        PlaylistDTO created = dao.create(dto, ownerUsername);
        ctx.status(201).json(created);
    }

    public void read(Context ctx){
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        String username = requireUsername(ctx);
        ctx.json(dao.readByIdAndOwner(id, username));
    }


    public void myPlaylists(Context ctx){
        String username = requireUsername(ctx);
        ctx.json(dao.readByOwner(username));
    }

    public void addSong(Context ctx){
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        AddSongDTO dto = ctx.bodyAsClass(AddSongDTO.class);
        ctx.json(dao.addSong(id, dto.getSongId()));
    }

    public void removeSong(Context ctx) {
        Integer playlistId = ctx.pathParamAsClass("id", Integer.class).get();
        Integer songId = ctx.pathParamAsClass("songId", Integer.class).get();

        String username = requireUsername(ctx);
        PlaylistDTO updated = dao.removeSong(playlistId, songId, username);

        ctx.status(200).json(updated);
    }

    public void deletePlaylist(Context ctx) {
        Integer playlistId = ctx.pathParamAsClass("id", Integer.class).get();

        String username = requireUsername(ctx);
        dao.deletePlaylist(playlistId, username);

        ctx.status(204);
    }
}
