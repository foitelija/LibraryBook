package com.app.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public  class User implements Serializable {
    private static final long serialVersionUID = 705770758L;

    String login;
    String password;
    String firstName;
    String lastName;
    Role role;

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                '}';
    }
}
