package com.app.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GenreReport implements Serializable {
    private static final long serialVersionUID = 23484545629L;
    String name;
    int count;
}
