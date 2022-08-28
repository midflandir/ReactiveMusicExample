package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.IPlaylistRepository;
import ec.com.reactive.music.service.IPlaylistService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

@Service
    @AllArgsConstructor
    public class PlaylistServiceImpl implements IPlaylistService {

    @Autowired
    private IPlaylistRepository iPlaylistRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Mono<ResponseEntity<Flux<PlaylistDTO>>> findAllPlaylists() {

        return this.iPlaylistRepository
                .findAll()
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NO_CONTENT.toString())))
                .map(playlist -> entityToDTO(playlist))
                .collectList()
                .map(playlistDTOS -> new ResponseEntity<>(Flux.fromIterable(playlistDTOS),HttpStatus.FOUND))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(Flux.empty(),HttpStatus.NO_CONTENT)));

    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> findPlaylistById(String id) {
        //Handling errors
        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString()))) //Capture the error
                .map(this::entityToDTO)
                .map(playlistDTO -> new ResponseEntity<>(playlistDTO, HttpStatus.FOUND)) //Mono<ResponseEntity<AlbumDTO>>
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND))); //Handle the error
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> savePlaylist(PlaylistDTO playlistDTO) {
        return this.iPlaylistRepository
                .save(DTOToEntity(playlistDTO))
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.EXPECTATION_FAILED.toString())))
                .map(playlist -> entityToDTO(playlist))
                .map(playlistDTO1 -> new ResponseEntity<>(playlistDTO1,HttpStatus.CREATED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> updatePlaylist(String id, PlaylistDTO aDto) {

        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    aDto.setIdPlaylist(playlist.getIdPlaylist());
                    return this.savePlaylist(aDto);
                })
                .map(albumDTOResponseEntity -> new ResponseEntity<>(albumDTOResponseEntity.getBody(),HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));
    }


    @Override
    public Mono<ResponseEntity<PlaylistDTO>> addSongtolaylist(String id, SongDTO songDTO) {

        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    playlist.getSongs().add(DTOToEntitySong(songDTO));
                    playlist.setDuration(
                            Calculatetimeadding(playlist.getDuration(), songDTO.getDuration()));
                    return  this.savePlaylist(entityToDTO(playlist));
                })
                .map(albumDTOResponseEntity -> new ResponseEntity<>(albumDTOResponseEntity.getBody(),HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));
    }

    @Override
    public Mono<ResponseEntity<PlaylistDTO>> removeSongtolaylist(String id, SongDTO songDTO) {

        return this.iPlaylistRepository
                .findById(id)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> {
                    playlist.getSongs().remove(DTOToEntitySong(songDTO));
                    playlist.setDuration(
                            Calculatetimesubtracting(playlist.getDuration(), songDTO.getDuration()));
                    return  this.savePlaylist(entityToDTO(playlist));
                })
                .map(albumDTOResponseEntity -> new ResponseEntity<>(albumDTOResponseEntity.getBody(),HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_MODIFIED)));
    }

    public LocalTime Calculatetimeadding(LocalTime playlisttime, LocalTime songtime){
        LocalTime duration = playlisttime
                .plusHours(songtime.getHour())
                .plusMinutes(songtime.getMinute())
                .plusSeconds(songtime.getSecond());
        return duration;
    }

    public LocalTime Calculatetimesubtracting(LocalTime playlisttime, LocalTime songtime){
        LocalTime duration = playlisttime
                .minusHours(songtime.getHour())
                .minusMinutes(songtime.getMinute())
                .minusSeconds(songtime.getSecond());
        return duration;
    }
    //I have to change the implementation vs what we did in class because the unit test was not working properly
    @Override
    public Mono<ResponseEntity<String>> deletePlaylist(String idPlaylist) {
        return this.iPlaylistRepository
                .findById(idPlaylist)
                .switchIfEmpty(Mono.error(new Throwable(HttpStatus.NOT_FOUND.toString())))
                .flatMap(playlist -> this.iPlaylistRepository
                        .deleteById(playlist.getIdPlaylist())
                        .map(monoVoid -> new ResponseEntity<>(idPlaylist, HttpStatus.ACCEPTED)))
                .thenReturn(new ResponseEntity<>(idPlaylist, HttpStatus.ACCEPTED))
                .onErrorResume(throwable -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND)));

    }

    @Override
    public Playlist DTOToEntity(PlaylistDTO playlistDTO) {
        return this.modelMapper.map(playlistDTO, Playlist.class);
    }

    @Override
    public Song DTOToEntitySong(SongDTO songDTO) {
        return this.modelMapper.map(songDTO, Song.class);
    }

    @Override
    public PlaylistDTO entityToDTO(Playlist playlist) {

        return this.modelMapper.map(playlist,PlaylistDTO.class);
    }


}
