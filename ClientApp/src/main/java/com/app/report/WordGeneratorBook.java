package com.app.report;

import com.app.model.Author;
import com.app.model.Book;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class WordGeneratorBook implements WordGenerator
{
   final Author author;
   final List<Book> books;
   private String fileName;
   private File doc;

   XWPFDocument document;
   XWPFTable table;

    public WordGeneratorBook(Author author, List<Book> books) {
        this.author = author;
        this.books = books;
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
        r.setText("Автор: "+author);
        table = document.createTable();
        headerTable(table);
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
        run.setText("Название");


        XWPFTableCell cell = row.getCell(0);

        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
        CTTcPr pr = cell.getCTTc().addNewTcPr();
        pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(2880));


        paragraph = row.addNewTableCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        run = paragraph.createRun();
        run.setBold(true);
        run.setText("Жанр");

        cell = row.getCell(1);
        cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);

        cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
        pr = cell.getCTTc().addNewTcPr();
        pr.addNewNoWrap();
        cellWidth.setW(BigInteger.valueOf(1880));


    }
    public void fillTable(XWPFTable table){
        for(Book book:books){
          XWPFTableRow row = table.createRow();

          row.getCell(0).removeParagraph(0);
          XWPFParagraph paragraph = row.getCell(0).addParagraph();
          paragraph.setAlignment(ParagraphAlignment.CENTER);
          XWPFRun run = paragraph.createRun();
          run.setText(book.getName());


          row.getCell(1).removeParagraph(0);
          paragraph = row.getCell(1).addParagraph();
          paragraph.setAlignment(ParagraphAlignment.CENTER);
          run = paragraph.createRun();
          run.setText(book.getGenre().getName());


        }
    }


}


