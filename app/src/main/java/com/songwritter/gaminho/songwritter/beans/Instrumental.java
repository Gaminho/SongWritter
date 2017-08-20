package com.songwritter.gaminho.songwritter.beans;

import java.io.Serializable;

public class Instrumental implements Serializable {

    public enum Type {
        FACE_A, FACE_B
    }

    private String path;
    private String title;
    private Type type;
    private String author;

    public Instrumental() {
    }

    public Instrumental(String title, String path, Type type, String author) {
        this.title = title;
        this.path = path;
        this.type = type;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "Instrumental{" +
                "path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", author='" + author + '\'' +
                '}';
    }

}
