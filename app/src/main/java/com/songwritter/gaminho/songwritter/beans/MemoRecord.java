package com.songwritter.gaminho.songwritter.beans;


import java.io.Serializable;

public class MemoRecord implements Serializable {

    private String path;
    private String title;
    private String author;
    private long creation;
    private long duration;

    public MemoRecord() {
    }

    public MemoRecord(String path, String title, String author, long creation, long duration) {
        this.path = path;
        this.title = title;
        this.author = author;
        this.creation = creation;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MemoRecord{" +
                "path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", creation=" + creation +
                ", duration=" + duration +
                '}';
    }

    public boolean isValid() throws IllegalArgumentException {
        if(this.getPath() == null || this.getPath().isEmpty())
            throw  new IllegalArgumentException("Path can not be null");


        else if(this.getTitle() == null || this.getTitle().isEmpty())
            throw  new IllegalArgumentException("Title can not be null");


        else if(this.getAuthor() == null || this.getAuthor().isEmpty())
            throw  new IllegalArgumentException("Author can not be null");

        else if(this.getDuration() == 0)
            throw  new IllegalArgumentException("Duration can not be 0");

        else if(this.getCreation() == 0)
            throw  new IllegalArgumentException("Creation can not be 0");

        return true;
    }
}
