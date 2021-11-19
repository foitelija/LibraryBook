package com.app.model;

import java.io.Serializable;

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

    public int getYearOf() {
        return yearOf;
    }

    public void setYearOf(int yearOf) {
        this.yearOf = yearOf;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
    }

    @Override
    public String toString() {
        return "YearReport{" +
                "yearOf=" + yearOf +
                ", bookCount=" + bookCount +
                '}';
    }
}
