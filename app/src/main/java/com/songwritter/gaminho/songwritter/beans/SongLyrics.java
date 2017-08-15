package com.songwritter.gaminho.songwritter.beans;


import java.io.Serializable;

public class SongLyrics implements Serializable {

    private String id;
    private String title;
    private String content;
    private String author;
    private long creation;
    private long lastUpdate;

    public SongLyrics() {
    }

    public SongLyrics(String title, String content, long lastUpdate) {
        this.title = title;
        this.content = content;
        this.lastUpdate = lastUpdate;
    }

    public SongLyrics(String title, String content, String author, long lastUpdate) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.lastUpdate = lastUpdate;
    }

    public SongLyrics(String title, String content, String author, long creation, long lastUpdate) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.creation = creation;
        this.lastUpdate = lastUpdate;
    }

    public SongLyrics(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "SongLyrics{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", creation=" + creation +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

}
