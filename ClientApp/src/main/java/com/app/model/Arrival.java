package com.app.model;






import java.io.Serializable;
import java.time.LocalDate;



public  class Arrival implements Serializable {
    private static final long serialVersionUID = 136668729L;
    int id;
    LocalDate arrivalDate;
    Book book;
    int yearOf;
    int price;
    int amount;
    boolean visible = true;
    int pages;
    String bookPublisher;
    private User modifiedBy;

    public Arrival() {
    }

    public Arrival(Arrival other){
        this.id = other.id;
        this.arrivalDate = other.arrivalDate;
        this.book = other.book;
        this.yearOf=other.yearOf;
        this.price=other.price;
        this.amount=other.amount;
        this.visible = other.visible;
        this.pages=other.pages;
        this.bookPublisher=other.bookPublisher;
        this.modifiedBy=other.modifiedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public int getYearOf() {
        return yearOf;
    }

    public void setYearOf(int yearOf) {
        this.yearOf = yearOf;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public User getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(User modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
