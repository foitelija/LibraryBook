package com.app.model;



import java.io.Serializable;

public class Genre  implements Serializable {

    private static final long serialVersionUID = 836668729L;

    private int id;
    private String name;
    private  boolean visible=true;

    private User modifiedBy;

    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
        this.visible = true;
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
