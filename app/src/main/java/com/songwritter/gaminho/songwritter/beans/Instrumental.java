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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instrumental that = (Instrumental) o;

        if (!path.equals(that.path)) return false;
        if (!title.equals(that.title)) return false;
        if (type != that.type) return false;
        return author.equals(that.author);

    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + author.hashCode();
        return result;
    }

    public boolean isValid() throws IllegalArgumentException {
        if(this.getPath() == null || this.getPath().isEmpty())
            throw  new IllegalArgumentException("Path can not be null");


        else if(this.getTitle() == null || this.getTitle().isEmpty())
            throw  new IllegalArgumentException("Title can not be null");


        else if(this.getAuthor() == null || this.getAuthor().isEmpty())
            throw  new IllegalArgumentException("Author can not be null");

        else if(this.getType() == null)
            throw  new IllegalArgumentException("Type can not be null");

        return true;
    }


}
