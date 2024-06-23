package com.example.musicapi.controller;

import com.example.musicapi.model.Album;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    final List<Album> albumList = new ArrayList<>();

    @GetMapping
    public List<Album> getAllAlbums() {
        return albumList;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable String id) {
        Optional<Album> album = albumList.stream().filter(a -> a.getId().equals(id)).findFirst();
        return album.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<String> addAlbum(@RequestBody Album album) {
        boolean albumExists = albumList.stream().anyMatch(a -> a.getBandName().equalsIgnoreCase(album.getBandName()) && a.getAlbumName().equalsIgnoreCase(album.getAlbumName()));
        if (albumExists) {
            return new ResponseEntity<>("Album already exists for this band.", HttpStatus.CONFLICT);
        }
        album.setId(UUID.randomUUID().toString());
        albumList.add(album);
        return new ResponseEntity<>("Album added successfully.", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateAlbum(@PathVariable String id, @RequestBody Album updatedAlbum) {
        Optional<Album> existingAlbum = albumList.stream().filter(a -> a.getId().equals(id)).findFirst();
        if (existingAlbum.isEmpty()) {
            return new ResponseEntity<>("Album not found.", HttpStatus.NOT_FOUND);
        }
        boolean duplicateAlbum = albumList.stream().anyMatch(a -> a.getBandName().equalsIgnoreCase(updatedAlbum.getBandName()) && a.getAlbumName().equalsIgnoreCase(updatedAlbum.getAlbumName()) && !a.getId().equals(id));
        if (duplicateAlbum) {
            return new ResponseEntity<>("An album with the same name already exists for this band.", HttpStatus.CONFLICT);
        }
        Album album = existingAlbum.get();
        album.setBandName(updatedAlbum.getBandName());
        album.setAlbumName(updatedAlbum.getAlbumName());
        album.setYear(updatedAlbum.getYear());
        album.setMembers(updatedAlbum.getMembers());
        return new ResponseEntity<>("Album updated successfully.", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable String id) {
        boolean removed = albumList.removeIf(album -> album.getId().equals(id));
        if (removed) {
            return new ResponseEntity<>("Album deleted successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Album not found.", HttpStatus.NOT_FOUND);
        }
    }
}
