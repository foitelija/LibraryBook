package com.app.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public  class Book implements Serializable {

    private static final long serialVersionUID = 481547477L;

    private int id;
    private String name;
    private Author author;
    private Genre genre;
    private boolean visible = true;
    private User modifiedBy;


    public Book(int id, String name, Author author, Genre genre,  boolean visible) {
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

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author=" + author +
                ", genre=" + genre +
                ", visible=" + visible +
                ", modifiedBy=" + modifiedBy +
                '}';
    }
}
