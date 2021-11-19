package com.app.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class YearReport  implements Serializable {


    private static final long serialVersionUID = 54645648729L;

    int yearOf;
    int bookCount;

    public YearReport() {
    }

    public YearReport(int yearOf, int bookCount) {
        this.yearOf = yearOf;
        this.bookCount = bookCount;
    }
}
