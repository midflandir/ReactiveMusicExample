package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.AlbumDTO;
import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.dto.SongDTO;
import ec.com.reactive.music.domain.entities.Album;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.domain.entities.Song;
import ec.com.reactive.music.repository.IPlaylistRepository;
import ec.com.reactive.music.repository.ISongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

        @Mock
        ISongRepository songRepository;

        ModelMapper modelMapper; //Helper - Apoyo/Soporte

        SongServiceImpl songService;

        @BeforeEach
        void init(){
            modelMapper = new ModelMapper();
            songService = new SongServiceImpl(songRepository,modelMapper);
        }

        //The test is focused on the status code
        @Test
        @DisplayName("findAllPlaylistsError()")
        void findAllPlaylistsError() {

            ArrayList<Song> songlist = new ArrayList<>();

            ArrayList<SongDTO> listSonglistDTO = songlist
                    .stream().map(song ->
                            modelMapper.map(song,SongDTO.class))
                    .collect(Collectors.toCollection(ArrayList::new));

            var fluxResult = Flux.fromIterable(songlist);
            var fluxResultDTO = Flux.fromIterable(listSonglistDTO);

            ResponseEntity<Flux<SongDTO>> respEntResult =
                    new ResponseEntity<>(fluxResultDTO, HttpStatus.NO_CONTENT);


            Mockito.when(songRepository.findAll()).thenReturn(fluxResult);


            var service = songService.findAllSongs();


            StepVerifier.create(service)
                    .expectNextMatches(fluxResponseEntity ->
                            fluxResponseEntity
                                    .getStatusCode()
                                    .compareTo(HttpStatus.NO_CONTENT) == 204);

        }


    @DisplayName("findAllSongs()")
    @Test
    void findAllSongs() {


        ArrayList<Song> songlist = new ArrayList<>();
        songlist.add(new Song());
        songlist.add(new Song());

        ArrayList<SongDTO> listSonglistDTO = songlist
                .stream().map(song ->
                        modelMapper.map(song,SongDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));

        var fluxResult = Flux.fromIterable(songlist);
        var fluxResultDTO = Flux.fromIterable(listSonglistDTO);

        ResponseEntity<Flux<SongDTO>> respEntResult =
                new ResponseEntity<>(fluxResultDTO, HttpStatus.NO_CONTENT);


        Mockito.when(songRepository.findAll()).thenReturn(fluxResult);


        var service = songService.findAllSongs();


        StepVerifier.create(service)
                .expectNextMatches(fluxResponseEntity -> fluxResponseEntity.getStatusCode().is3xxRedirection())
                .expectComplete().verify();

    }


    @Test
    @DisplayName("findAlbumById()")
    void findSongById() {
        Song songExpected = new Song();
        songExpected.setIdSong("12345678-9");
        songExpected.setIdAlbum("2456457657");
        songExpected.setName("Carl Magno");
        songExpected.setArrangedBy("Copernico");
        songExpected.setLyricsBy("Cristobal Colon");
        songExpected.setProducedBy("Albert Eintein");

        var songDToexpected = modelMapper.map(songExpected, SongDTO.class);

        ResponseEntity<SongDTO> albumDTOResponse = new ResponseEntity<>(songDToexpected,HttpStatus.FOUND);

        Mockito.when(songRepository.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));

        var service = songService.findSongById("12345678-9");

        StepVerifier.create(service)
                .expectNext(albumDTOResponse)
                .expectComplete()
                .verify();

        Mockito.verify(songRepository).findById("12345678-9");
    }

    @Test
    @DisplayName("findSongByIdError()")
    void findSongByIdError() { //Not found

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Mockito.when(songRepository.findById(Mockito.any(String.class))).thenReturn(Mono.empty());

        var service = songService.findSongById("12345678-9");

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete().verify();

        Mockito.verify(songRepository).findById("12345678-9");
    }
    @Test
    @DisplayName("saveSong()")
    void saveSong(){
        Song songExpected = new Song();
        songExpected.setIdSong("12345678-9");
        songExpected.setIdAlbum("2456457657");
        songExpected.setName("Carl Magno");
        songExpected.setArrangedBy("Copernico");
        songExpected.setLyricsBy("Cristobal Colon");
        songExpected.setProducedBy("Albert Eintein");

        var songDTOExpected = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.CREATED);

        Mockito.when(songRepository.save(Mockito.any(Song.class))).thenReturn(Mono.just(songExpected));

        var service = songService.saveSong(songDTOExpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        Mockito.verify(songRepository).save(songExpected);
    }

    @Test
    @DisplayName("saveSongError()")
    void saveSongError(){
        Song songExpected = new Song();
        songExpected.setIdSong("12345678-9");
        songExpected.setIdAlbum("Does not exist");
        songExpected.setName("Carl Magno");
        songExpected.setArrangedBy("Copernico");
        songExpected.setLyricsBy("Cristobal Colon");
        songExpected.setProducedBy("Albert Eintein");

        var songDTOExpected = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOExpected,HttpStatus.NOT_ACCEPTABLE);


        var service = songService.saveSong(songDTOExpected);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();
    }


    @Test
    @DisplayName("updateSong()")
    void updateSong(){
        Song songExpected = new Song();
        songExpected.setIdSong("12345678-9");
        songExpected.setIdAlbum("2456457657");
        songExpected.setName("Carl Magno");
        songExpected.setArrangedBy("Copernico");
        songExpected.setLyricsBy("Cristobal Colon");
        songExpected.setProducedBy("Albert Eintein");

        var songEdited = songExpected.toBuilder().name("songTestingEdited").build();

        var songDTOEdited = modelMapper.map(songEdited,SongDTO.class);


        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(songDTOEdited,HttpStatus.ACCEPTED);

        //You need to mock the findById first and because you use it previous the save/update
        Mockito.when(songRepository.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));
        Mockito.when(songRepository.save(Mockito.any(Song.class))).thenReturn(Mono.just(songEdited));

        var service = songService.updateSong("12345678-9", songDTOEdited);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee
        Mockito.verify(songRepository).save(songEdited);

    }


    @Test
    @DisplayName("updateSongError()")
    void updateSongError(){
        Song songExpected = new Song();

        var songDTOEdited = modelMapper.map(songExpected,SongDTO.class);

        ResponseEntity<SongDTO> songDTOResponse = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        Mockito.when(songRepository.findById(Mockito.any(String.class))).thenReturn(Mono.just(songExpected));

        var service = songService.updateSong("45656", songDTOEdited);

        StepVerifier.create(service)
                .expectNext(songDTOResponse)
                .expectComplete()
                .verify();

        //Si está utilizando lo que yo mockee

    }


    @Test
    @DisplayName("deleteSong()")
    void deleteSong(){
        Song songExpected = new Song();
        songExpected.setIdSong("12345678-9");
        songExpected.setIdAlbum("2456457657");
        songExpected.setName("Carl Magno");
        songExpected.setArrangedBy("Copernico");
        songExpected.setLyricsBy("Cristobal Colon");
        songExpected.setProducedBy("Albert Eintein");

        ResponseEntity<String> responseDelete = new ResponseEntity<>(songExpected.getIdSong(),HttpStatus.ACCEPTED);

        Mockito.when(songRepository.findById(Mockito.any(String.class)))
                .thenReturn(Mono.just(songExpected));
        Mockito.when(songRepository.deleteById(Mockito.any(String.class)))
                .thenReturn(Mono.empty());


        var service = songService.deleteSong("12345678-9");


        StepVerifier.create(service).expectNext(responseDelete).expectComplete().verify();

        Mockito.verify(songRepository).findById("12345678-9");
     //   Mockito.verify(songRepository).deleteById("12345678-9");

    }

    @Test
    @DisplayName("deleteSong()")
    void deleteSongError(){
        Song songExpected = new Song();

        ResponseEntity<String> responseDelete = new ResponseEntity<>(songExpected.getIdSong(),HttpStatus.NOT_FOUND);

        Mockito.when(songRepository.findById(Mockito.any(String.class)))
                .thenReturn(Mono.just(songExpected));
        Mockito.when(songRepository.deleteById(Mockito.any(String.class)))
                .thenReturn(Mono.empty());


        var service = songService.deleteSong("12345678-9");


        StepVerifier.create(service).expectNext(responseDelete).expectComplete().verify();

        Mockito.verify(songRepository).findById("12345678-9");
        //   Mockito.verify(songRepository).deleteById("12345678-9");

    }


}