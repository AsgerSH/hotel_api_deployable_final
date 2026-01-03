package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "playlists")
@Getter
@Setter
@NoArgsConstructor
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="playlist_id")
    private Integer playlistId;

    @Column(nullable=false, length=100)
    private String name;

    @Column(length=500)
    private String description;

    @Column(name="img", length=255)
    private String img;

    @ManyToOne(optional=false)
    @JoinColumn(name="owner_username", referencedColumnName="username")
    private dat.security.entities.User owner;

    @OneToMany(mappedBy="playlist", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("position ASC")
    private List<PlaylistSong> songs = new ArrayList<>();
}
