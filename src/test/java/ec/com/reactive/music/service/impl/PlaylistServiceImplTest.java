package ec.com.reactive.music.service.impl;

import ec.com.reactive.music.domain.dto.PlaylistDTO;
import ec.com.reactive.music.domain.entities.Playlist;
import ec.com.reactive.music.repository.IPlaylistRepository;
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
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.stream.Collectors;
@ExtendWith(MockitoExtension.class)
class PlaylistServiceImplTest {
    @Mock
    IPlaylistRepository playlistRepository;

    ModelMapper modelMapper; //Helper - Apoyo/Soporte

    PlaylistServiceImpl playlistService;

    @BeforeEach
    void init(){
        modelMapper = new ModelMapper();
        playlistService = new PlaylistServiceImpl(playlistRepository,modelMapper);
    }

    //The test is focused on the status code
    @Test
    @DisplayName("findAllPlaylists()")
    void findAllPlaylists() {
        //1. Que tipo de prueba voy a hacer? Exitosa o fallida. - Exitosa -> Resultado: Mono<ResponseEntity<Flux<AlbumDTO>>>

        //2. Armar el escenario con la respuesta esperada
        ArrayList<Playlist> playlists = new ArrayList<>();
       // playlists.add(new Playlist());
       // playlists.add(new Playlist());

        ArrayList<PlaylistDTO> listPlaylistDTO = playlists
                .stream().map(playlist ->
                        modelMapper.map(playlist,PlaylistDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));

        var fluxResult = Flux.fromIterable(playlists);
        var fluxResultDTO = Flux.fromIterable(listPlaylistDTO);

        //La respuesta esperada
        ResponseEntity<Flux<PlaylistDTO>> respEntResult =
                new ResponseEntity<>(fluxResultDTO, HttpStatus.NO_CONTENT);

        //3. Mockeo - Mockear el resultado esperado
        Mockito.when(playlistRepository.findAll()).thenReturn(fluxResult);

        //4. Servicio
        var service = playlistService.findAllPlaylists();

        //5. Stepverifier
        StepVerifier.create(service)
                .expectNextMatches(fluxResponseEntity ->
                                fluxResponseEntity
                                        .getStatusCode()
                                        .compareTo(HttpStatus.NO_CONTENT) == 204);
        //6. Verificación de que se está usando lo que se mockeo en el punto 3
        // Mockito.verify(playlistRepository.findAll());

    }
}