package dat.dtos;

import dat.entities.Playlist;
import dat.entities.PlaylistSong;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistDTO {
    private Integer playlistId;
    private String name;
    private String description;
    private String img;
    private String ownerUsername;
    private List<Integer> songIds;

    public PlaylistDTO(Playlist p) {
        this.playlistId = p.getPlaylistId();
        this.name = p.getName();
        this.description = p.getDescription();
        this.img = p.getImg();
        this.ownerUsername = p.getOwner().getUsername();
        this.songIds = p.getSongs().stream()
                .sorted(Comparator.comparingInt(PlaylistSong::getPosition))
                .map(PlaylistSong::getSongId)
                .toList();
    }
}
