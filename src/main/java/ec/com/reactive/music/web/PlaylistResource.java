package ec.com.reactive.music.web;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.service.impl.PlaylistServiceImpl;
import ec.com.reactive.music.service.impl.SongServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class PlaylistResource {
    @Autowired
    private PlaylistServiceImpl playlistService;


    @Autowired
    private SongServiceImpl songService;

    @GetMapping("/findAllPlaylists")
    private Mono<ResponseEntity<Flux<PlaylistDTO>>> getAlbums(){
        return playlistService.findAllPlaylists();
    }

    //GET
    @GetMapping("/findPlaylist/{id}")
    private Mono<ResponseEntity<PlaylistDTO>> getAlbumById(@PathVariable String id){
        return playlistService.findPlaylistById(id);
    }

    //POST
    @PostMapping("/savePlaylist")
    private Mono<ResponseEntity<PlaylistDTO>> postAlbum(@RequestBody PlaylistDTO aDto){
        return playlistService.savePlaylist(aDto);
    }

    //PUT
    @PutMapping("/updatePlaylist/{id}")
    private Mono<ResponseEntity<PlaylistDTO>> putAlbum(@PathVariable String id , @RequestBody PlaylistDTO aDto){
        return playlistService.updatePlaylist(id,aDto);
    }

    @PutMapping("/addSongtolaylist/{id}/{songid}")
    private Mono<ResponseEntity<PlaylistDTO>> addSongtolaylist(@PathVariable String id , @PathVariable  String songid){


        return songService.findSongById(songid)
                .flatMap(songDTOResponseEntity -> songDTOResponseEntity.getStatusCode().is4xxClientError()?
                        playlistService.addSongtolaylist("SONG_NOT_FOUND", new SongDTO())
                        : playlistService.addSongtolaylist(id, songDTOResponseEntity.getBody()));

    }

    @PutMapping("/removeSongtolaylist/{id}/{songid}")
    private Mono<ResponseEntity<PlaylistDTO>> removeSongtolaylist(@PathVariable String id , @PathVariable  String songid){


        return songService.findSongById(songid)
                .flatMap(songDTOResponseEntity -> songDTOResponseEntity.getStatusCode().is4xxClientError()?
                        playlistService.removeSongtolaylist("SONG_NOT_FOUND", new SongDTO())
                        : playlistService.removeSongtolaylist(id, songDTOResponseEntity.getBody()));

    }

    //DELETE
    @DeleteMapping("/deletePlaylist/{id}")
    private Mono<ResponseEntity<String>> deletePlaylist(@PathVariable String id){
        return playlistService.deletePlaylist(id);
    }

}
