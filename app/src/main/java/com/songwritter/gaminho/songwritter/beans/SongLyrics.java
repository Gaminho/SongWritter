package com.songwritter.gaminho.songwritter.beans;


import java.io.Serializable;
import java.util.List;

public class SongLyrics implements Serializable {

    private String id;
    private String title;
    private String content;
    private String author;
    private long creation;
    private long lastUpdate;
    private List<Instrumental> beats;
    private List<MemoRecord> memoRecords;

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

    public SongLyrics(String title, String content, long creation, long lastUpdate) {
        this.title = title;
        this.content = content;
        this.author = "";
        this.creation = creation;
        this.lastUpdate = lastUpdate;
    }

    public SongLyrics(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public SongLyrics(String id, String title, String content, String author, long creation, long lastUpdate, List<Instrumental> beats, List<MemoRecord> memoRecords) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.creation = creation;
        this.lastUpdate = lastUpdate;
        this.beats = beats;
        this.memoRecords = memoRecords;
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

    public List<Instrumental> getBeats() {
        return beats;
    }

    public void setBeats(List<Instrumental> beats) {
        this.beats = beats;
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

    public List<MemoRecord> getMemoRecords() {
        return memoRecords;
    }

    public void setMemoRecords(List<MemoRecord> memoRecords) {
        this.memoRecords = memoRecords;
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
                ", beats=" + beats +
                ", memoRecords=" + memoRecords +
                '}';
    }

}
