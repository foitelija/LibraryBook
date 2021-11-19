package com.app.model;




import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@NoArgsConstructor
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

    @Override
    public String toString() {
        return "Arrival{" +
                "id=" + id +
                ", arrivalDate=" + arrivalDate +
                ", book=" + book +
                ", yearOf=" + yearOf +
                ", price=" + price +
                ", amount=" + amount +
                ", visible=" + visible +
                ", pages=" + pages +
                ", bookPublisher='" + bookPublisher + '\'' +
                ", modifiedBy=" + modifiedBy +
                '}';
    }
}
