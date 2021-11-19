package com.app.report;

import com.app.model.Arrival;
import com.app.model.Book;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WordGeneratorArrival implements WordGenerator{

    final LocalDate from;
    final LocalDate to;
    List<Arrival> arrivals;

    XWPFDocument document;
    XWPFTable table;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String fileName;
    private File doc;

    public WordGeneratorArrival(LocalDate from, LocalDate to, List<Arrival> arrivals) {
        this.from = from;
        this.to = to;
        this.arrivals = arrivals;
    }

    public void checkDir(){
        String _dir = System.getProperty("user.dir")+"/report";
        File directory =new File(_dir);
        directory.mkdir();
        doc = new File(directory.getAbsolutePath().concat("/").concat(fileName).concat(".docx"));
    }

    @Override
    public void createWord(final String fileName) throws IOException {
        this.fileName = fileName;
        checkDir();
        document = new XWPFDocument();
        XWPFParagraph p = document.createParagraph();
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setFontSize(16);

        r.setText("Поступление книг за период с "+ dateTimeFormatter.format(from)+" по "+dateTimeFormatter.format(to) );
        table = document.createTable();
        headerTable(table);
        arrivals.sort((final Arrival a1,final Arrival a2)->{
            if (a1.getArrivalDate().compareTo(a2.getArrivalDate())==0){
                Book b1 = a1.getBook();
                Book b2 = a2.getBook();
                if (b1.getName().equalsIgnoreCase(b2.getName())){
                    String au1 = b1.getAuthor().toString();
                    String au2 = b2.getAuthor().toString();
                    return au1.compareTo(au2);
                } else return b1.getName().compareTo(b2.getName());
            }else return a1.getArrivalDate().compareTo(a2.getArrivalDate());
        });
        fillTable(table);

        FileOutputStream out = new FileOutputStream(doc);
        document.write(out);
        document.close();
        out.close();

    }
    public void headerTable(XWPFTable table){

        CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
        width.setType(STTblWidth.DXA);
        width.setW(BigInteger.valueOf(9072));

        XWPFTableRow row = table.getRow(0);

        XWPFParagraph paragraph = row.getCell(0).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setText("Дата");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Книга");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Жанр");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Автор");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Издательство");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Страниц");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Год");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Стоимость");

        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Кол-во");


    }
    public void fillTable(XWPFTable table){
        for(Arrival arrival:arrivals){
            XWPFTableRow row = table.createRow();

            int i=0;
            row.getCell(i).removeParagraph(0);
            XWPFParagraph paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setText(dateTimeFormatter.format(arrival.getArrivalDate()));

            i++;
            Book book = arrival.getBook();
            row.getCell(i).removeParagraph(0);
             paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
             run = paragraph.createRun();
            run.setText(book.getName());

            i++;
            row.getCell(i).removeParagraph(0);
            paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setText(book.getGenre().getName());

            i++;
            row.getCell(i).removeParagraph(0);
            paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setText(book.getAuthor().toString());

            i++;
            row.getCell(i).removeParagraph(0);
            paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setText(arrival.getBookPublisher());

            i++;
            row.getCell(i).removeParagraph(0);
            paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setText(String.format("%d",arrival.getPages()));

            i++;
            row.getCell(i).removeParagraph(0);
            paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setText(String.format("%d",arrival.getYearOf()));

            i++;
            row.getCell(i).removeParagraph(0);
            paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setText(String.format("%d",arrival.getPrice()));

            i++;
            row.getCell(i).removeParagraph(0);
            paragraph = row.getCell(i).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            run = paragraph.createRun();
            run.setText(String.format("%d",arrival.getAmount()));


        }
    }
}
