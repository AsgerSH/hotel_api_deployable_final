package dat.daos.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

import dat.security.entities.User;

import dat.entities.Playlist;
import dat.entities.PlaylistSong;
import dat.dtos.PlaylistDTO;
import dat.dtos.CreatePlaylistDTO;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PlaylistDAO {

    private static PlaylistDAO instance;
    private static EntityManagerFactory emf;

    public static PlaylistDAO getInstance(EntityManagerFactory _emf){
        if(instance == null){
            emf = _emf;
            instance = new PlaylistDAO();
        }
        return instance;
    }

    public PlaylistDTO create(CreatePlaylistDTO dto, String ownerUsername){
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            User owner = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :u",
                            User.class
                    ).setParameter("u", ownerUsername)
                    .getSingleResult();

            Playlist p = new Playlist();
            p.setName(dto.getName());
            p.setDescription(dto.getDescription());
            p.setImg(dto.getImg());
            p.setOwner(owner);

            em.persist(p);

            em.getTransaction().commit();
            return new PlaylistDTO(p);
        }
    }


    public PlaylistDTO read(Integer playlistId){
        try(EntityManager em = emf.createEntityManager()){
            Playlist p = em.find(Playlist.class, playlistId);
            return new PlaylistDTO(p);
        }
    }

    public List<PlaylistDTO> readByOwner(String ownerUsername){
        try(EntityManager em = emf.createEntityManager()){
            TypedQuery<Playlist> q = em.createQuery(
                    "SELECT p FROM Playlist p WHERE p.owner.username = :u", Playlist.class);
            q.setParameter("u", ownerUsername);
            return q.getResultList().stream().map(PlaylistDTO::new).toList();
        }
    }

    public PlaylistDTO addSong(Integer playlistId, Integer songId){
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();

            Playlist p = em.find(Playlist.class, playlistId);

            int nextPos = p.getSongs().stream()
                    .mapToInt(PlaylistSong::getPosition)
                    .max().orElse(0) + 1;

            PlaylistSong ps = new PlaylistSong();
            ps.setPlaylist(p);
            ps.setSongId(songId);
            ps.setPosition(nextPos);

            p.getSongs().add(ps);
            em.persist(ps);

            em.getTransaction().commit();
            return new PlaylistDTO(p);
        }
    }

    public PlaylistDTO removeSong(Integer playlistId, Integer songId, String ownerUsername) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Playlist p = em.find(Playlist.class, playlistId);
            if (p == null) {
                throw new IllegalArgumentException("Playlist not found");
            }

            // Owner check
            if (p.getOwner() == null || !p.getOwner().getUsername().equals(ownerUsername)) {
                throw new SecurityException("Not allowed");
            }

            // Find playlistSong row
            PlaylistSong toRemove = p.getSongs().stream()
                    .filter(ps -> ps.getSongId().equals(songId))
                    .findFirst()
                    .orElse(null);

            if (toRemove == null) {
                // nothing to remove -> return playlist as-is (or throw)
                em.getTransaction().commit();
                return new PlaylistDTO(p);
            }

            // Remove from collection + delete entity
            p.getSongs().remove(toRemove);
            em.remove(em.contains(toRemove) ? toRemove : em.merge(toRemove));

            // Re-index positions so order stays clean (1..n)
            int pos = 1;
            for (PlaylistSong ps : p.getSongs().stream()
                    .sorted(java.util.Comparator.comparingInt(PlaylistSong::getPosition))
                    .toList()) {
                ps.setPosition(pos++);
            }

            em.getTransaction().commit();
            return new PlaylistDTO(p);
        }
    }

    public void deletePlaylist(Integer playlistId, String ownerUsername) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Playlist p = em.find(Playlist.class, playlistId);
            if (p == null) {
                throw new IllegalArgumentException("Playlist not found");
            }

            // Owner check
            if (p.getOwner() == null || !p.getOwner().getUsername().equals(ownerUsername)) {
                throw new SecurityException("Not allowed");
            }

            // orphanRemoval=true + cascade=ALL på Playlist.songs gør at playlist_songs bliver slettet automatisk
            em.remove(p);

            em.getTransaction().commit();
        }
    }

    public PlaylistDTO readByIdAndOwner(int id, String username) {
        EntityManager em = emf.createEntityManager();
        try {
            Playlist p = em.createQuery(
                            "SELECT p FROM Playlist p WHERE p.id = :id AND p.owner.username = :u",
                            Playlist.class
                    ).setParameter("id", id)
                    .setParameter("u", username)
                    .getSingleResult();

            return new PlaylistDTO(p);
        } finally {
            em.close();
        }
    }



}
