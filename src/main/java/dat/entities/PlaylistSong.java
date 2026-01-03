package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import dat.security.entities.User;



@Entity
@Table(name="playlist_songs")
@Getter
@Setter
@NoArgsConstructor
public class PlaylistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional=false)
    @JoinColumn(name="playlist_id")
    private Playlist playlist;

    // Her gemmer du bare "songId" fra jeres katalog (fra db.json / senere rigtig Song tabel)
    @Column(name="song_id", nullable=false)
    private Integer songId;

    @Column(nullable=false)
    private Integer position;
}
