package com.songwritter.gaminho.songwritter.beans;

import java.io.Serializable;

public class Instrumental implements Serializable {

    public enum Type {
        FACE_A, FACE_B
    }

    private String path;
    private String title;
    private Type type;

    public Instrumental() {
    }

    public Instrumental(String path, String title, Type type) {
        this.path = path;
        this.type = type;
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
