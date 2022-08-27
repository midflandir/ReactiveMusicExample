package ec.com.reactive.music.service;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Album;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.domain.entities.Song;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlaylistService {
    Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists();
    Mono<ResponseEntity<PlaylistDTO>> findPlaylistById(String id);
    Mono<ResponseEntity<PlaylistDTO>> addSongtolaylist(String id, SongDTO songDTO);

    Mono<ResponseEntity<PlaylistDTO>> savePlaylist(PlaylistDTO playlistDTO);
    Mono<ResponseEntity<PlaylistDTO>> updatePlaylist (String id, PlaylistDTO aDto);

    Mono<ResponseEntity<PlaylistDTO>> removeSongtolaylist(String id, SongDTO songDTO);

    Mono<ResponseEntity<String>> deletePlaylist (String idPlaylist);

    //ModelMapper functions
    Playlist DTOToEntity (PlaylistDTO playlistDTO);

    Song DTOToEntitySong(SongDTO songDTO);

    PlaylistDTO entityToDTO(Playlist playlist);

}
