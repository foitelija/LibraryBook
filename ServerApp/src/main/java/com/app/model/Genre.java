package com.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Genre  implements Serializable {

    private static final long serialVersionUID = 836668729L;

    private int id;
    private String name;
    private  boolean visible=true;

    private User modifiedBy;



    public Genre(String name) {
        this.name = name;
        this.visible = true;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", visible=" + visible +
                ", modifiedBy=" + modifiedBy +
                '}';
    }
}
