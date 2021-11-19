package com.app.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Author implements Serializable {
    private static final long serialVersionUID = 236668729L;


  private int id;
  private String firstName;
  private String lastName;
  private boolean visible = true;
  private User modifiedBy;



    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", visible=" + visible +
                ", modifiedBy=" + modifiedBy +
                '}';
    }
}
