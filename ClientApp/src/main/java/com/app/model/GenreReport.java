package com.app.model;


import java.io.Serializable;

public class GenreReport implements Serializable {

    private static final long serialVersionUID = 23484545629L;
    private String name;
    private int count;

    public GenreReport() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
