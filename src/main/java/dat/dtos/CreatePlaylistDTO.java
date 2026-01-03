package dat.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CreatePlaylistDTO {
    private String name;
    private String description;
    private String img;
}
