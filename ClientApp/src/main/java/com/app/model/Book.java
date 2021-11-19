package com.app.model;




import java.io.Serializable;


public  class Book implements Serializable {

    private static final long serialVersionUID = 481547477L;

    private int id;
    private String name;
    private Author author;
    private Genre genre;
    private boolean visible = true;
    private User modifiedBy;


    public Book() {
    }

    public Book(Author author) {
        this.author = author;
    }

    public Book(int id, String name, Author author, Genre genre, boolean visible) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.visible = visible;
    }
    public Book( String name, Author author, Genre genre, boolean visible) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.visible = visible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return name;
    }
}
