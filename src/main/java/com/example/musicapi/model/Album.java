package com.example.musicapi.model;

import java.util.List;

public class Album {
    private String id;
    private String bandName;
    private String albumName;
    private int year;
    private List<String> members;

    public Album() {
    }

    public Album(String id, String bandName, String albumName, int year, List<String> members) {
        this.id = id;
        this.bandName = bandName;
        this.albumName = albumName;
        this.year = year;
        this.members = members;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
