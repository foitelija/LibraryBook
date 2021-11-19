package com.app.service;

import com.app.model.Arrival;
import com.app.model.Author;
import com.app.model.Book;
import com.app.report.WordGenerator;
import com.app.report.WordGeneratorArrival;
import com.app.report.WordGeneratorBook;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ArrivalDocService extends Service<String> {


    List<Arrival> arrivals;
    LocalDate from;
    LocalDate to;
    public void startService(LocalDate from, LocalDate to, List<Arrival> arrivals) {

        if (!isRunning()) {
            this.arrivals = arrivals;
            this.from=from;
            this.to=to;
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

                String fileName = "Поступления_" + from.format(DateTimeFormatter.ofPattern("ddMMyy"))
                +"_"+to.format(DateTimeFormatter.ofPattern("ddMMyy"))
                        + "_" + formatter.format(time) + ".docx";
                try {
                    WordGenerator wordGenerator
                            = new WordGeneratorArrival(from,to,arrivals);
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
