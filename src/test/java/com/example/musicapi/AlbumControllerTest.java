package com.example.musicapi;

import com.example.musicapi.controller.AlbumController;
import com.example.musicapi.model.Album;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlbumController.class)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumController albumController;

    private List<Album> albums;

    @BeforeEach
    public void setup() {
        albums = new ArrayList<>();
        Album album1 = new Album(UUID.randomUUID().toString(), "The Beatles", "Abbey Road", 1969, Arrays.asList("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr"));
        Album album2 = new Album(UUID.randomUUID().toString(), "Pink Floyd", "The Dark Side of the Moon", 1973, Arrays.asList("Roger Waters", "David Gilmour", "Richard Wright", "Nick Mason"));
        albums.add(album1);
        albums.add(album2);

        when(albumController.getAllAlbums()).thenReturn(albums);
        when(albumController.getAlbumById(album1.getId())).thenReturn(new ResponseEntity<>(album1, HttpStatus.OK));
        when(albumController.addAlbum(any(Album.class))).thenAnswer(invocation -> {
            Album newAlbum = invocation.getArgument(0);
            Optional<Album> existingAlbum = albums.stream()
                    .filter(a -> a.getBandName().equalsIgnoreCase(newAlbum.getBandName()) && a.getAlbumName().equalsIgnoreCase(newAlbum.getAlbumName()))
                    .findFirst();
            if (existingAlbum.isPresent()) {
                return new ResponseEntity<>("Album already exists for this band.", HttpStatus.CONFLICT);
            }
            newAlbum.setId(UUID.randomUUID().toString());
            albums.add(newAlbum);
            return new ResponseEntity<>("Album added successfully.", HttpStatus.CREATED);
        });
        when(albumController.updateAlbum(any(String.class), any(Album.class))).thenAnswer(invocation -> {
            String id = invocation.getArgument(0);
            Album updatedAlbum = invocation.getArgument(1);
            Optional<Album> existingAlbum = albums.stream()
                    .filter(a -> a.getId().equals(id))
                    .findFirst();
            if (existingAlbum.isEmpty()) {
                return new ResponseEntity<>("Album not found.", HttpStatus.NOT_FOUND);
            }
            Optional<Album> duplicateAlbum = albums.stream()
                    .filter(a -> a.getBandName().equalsIgnoreCase(updatedAlbum.getBandName()) &&
                            a.getAlbumName().equalsIgnoreCase(updatedAlbum.getAlbumName()) &&
                            !a.getId().equals(id))
                    .findFirst();
            if (duplicateAlbum.isPresent()) {
                return new ResponseEntity<>("An album with the same name already exists for this band.", HttpStatus.CONFLICT);
            }
            Album album = existingAlbum.get();
            album.setBandName(updatedAlbum.getBandName());
            album.setAlbumName(updatedAlbum.getAlbumName());
            album.setYear(updatedAlbum.getYear());
            album.setMembers(updatedAlbum.getMembers());
            return new ResponseEntity<>("Album updated successfully.", HttpStatus.OK);
        });
        when(albumController.deleteAlbum(album1.getId())).thenReturn(new ResponseEntity<>("Album deleted successfully.", HttpStatus.OK));
    }

    @Test
    public void testGetAllAlbums() throws Exception {
        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bandName", is(albums.get(0).getBandName())))
                .andExpect(jsonPath("$[1].bandName", is(albums.get(1).getBandName())));
    }

    @Test
    public void testGetAlbumById() throws Exception {
        mockMvc.perform(get("/api/albums/{id}", albums.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bandName", is(albums.get(0).getBandName())));
    }

    @Test
    public void testAddAlbum() throws Exception {
        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"bandName\": \"The Beatles\", \"albumName\": \"Let It Be\", \"year\": 1970, \"members\": [\"John Lennon\", \"Paul McCartney\", \"George Harrison\", \"Ringo Starr\"] }"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Album added successfully."));
    }

    @Test
    public void testAddExistingAlbum() throws Exception {
        Album existingAlbum = albums.get(0);

        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"bandName\": \"" + existingAlbum.getBandName() + "\", \"albumName\": \"" + existingAlbum.getAlbumName() + "\", \"year\": 1969, \"members\": [\"John Lennon\", \"Paul McCartney\", \"George Harrison\", \"Ringo Starr\"] }"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Album already exists for this band."));
    }

    @Test
    public void testUpdateAlbum() throws Exception {
        mockMvc.perform(put("/api/albums/{id}", albums.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"bandName\": \"The Beatles\", \"albumName\": \"Abbey Road (Updated)\", \"year\": 1969, \"members\": [\"John Lennon\", \"Paul McCartney\", \"George Harrison\", \"Ringo Starr\"] }"))
                .andExpect(status().isOk())
                .andExpect(content().string("Album updated successfully."));
    }

    @Test
    public void testUpdateNonExistingAlbum() throws Exception {
        String nonExistingId = UUID.randomUUID().toString();

        mockMvc.perform(put("/api/albums/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"bandName\": \"The Beatles\", \"albumName\": \"Abbey Road (Updated)\", \"year\": 1969, \"members\": [\"John Lennon\", \"Paul McCartney\", \"George Harrison\", \"Ringo Starr\"] }"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Album not found."));
    }

    @Test
    public void testDeleteAlbum() throws Exception {
        mockMvc.perform(delete("/api/albums/{id}", albums.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Album deleted successfully."));
    }
}
