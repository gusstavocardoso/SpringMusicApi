package com.example.musicapi.repository;

import com.example.musicapi.model.Album;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AlbumRepository {
    private final List<Album> albums = new ArrayList<>();

    public List<Album> findAll() {
        return new ArrayList<>(albums);
    }

    public Optional<Album> findByBandAndAlbum(String bandName, String albumName) {
        return albums.stream()
                .filter(album -> album.getBandName().equalsIgnoreCase(bandName) && album.getAlbumName().equalsIgnoreCase(albumName))
                .findFirst();
    }

    public void save(Album album) {
        albums.add(album);
    }

    public void update(Album album) {
        delete(album.getBandName(), album.getAlbumName());
        save(album);
    }

    public void delete(String bandName, String albumName) {
        albums.removeIf(album -> album.getBandName().equalsIgnoreCase(bandName) && album.getAlbumName().equalsIgnoreCase(albumName));
    }
}
