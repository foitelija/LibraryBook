package com.app.service;

import com.app.model.Author;
import com.app.model.Book;
import com.app.report.WordGenerator;
import com.app.report.WordGeneratorBook;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookDocService extends Service<String> {


    Author author;
    List<Book> books;

    public void startService(Author author, List<Book> books) {

        if (!isRunning()) {
            this.author = author;
            this.books = books;

            reset();
            start();
        }

    }


    public boolean stopService() {

        if (isRunning()) {
            return cancel();
        }
        return false;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                LocalDateTime time = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
                String fileName = "Автор_" + author + "_" + formatter.format(time) + ".docx";
                try {
                    WordGenerator wordGenerator
                            = new WordGeneratorBook(author, books);

                    wordGenerator.createWord( fileName);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return fileName;

            }
        };
    }

    ;
}
