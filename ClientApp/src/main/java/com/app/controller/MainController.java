package com.app.controller;

import com.app.MainApp;
import com.app.model.*;
import com.app.service.ArrivalDocService;
import com.app.service.BookDocService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * основной котроллер, отвечает за заботу приложение
 */
public class MainController {


    // жанры
    ObservableList<Genre> genres = FXCollections.observableArrayList();
    //авторы
    ObservableList<Author> authors = FXCollections.observableArrayList();


    // книги
    ObservableList<Book> books = FXCollections.observableArrayList();

    // поступления
    ObservableList<Arrival> arrivals = FXCollections.observableArrayList();

    // комбокс фильтр авторв
    @FXML
    ComboBox authorFilter;


    // таблица жанры
    @FXML
    private TableView tabGenre;

    // таблица авторы
    @FXML
    private TableView tabAuthor;


    // таблица книги
    @FXML
    private TableView tabBook;

    // таблица поступлений
    @FXML
    private TableView tabArrival;

    // для фильтра поступлений по дате
    @FXML
    DatePicker fromDate;
    @FXML
    DatePicker toDate;

    // круговая диаграмма
    @FXML
    PieChart pieChart;

    // гистограмма
    @FXML
    BarChart<String,Number> barChart ;

    @FXML
    CategoryAxis osaX;

    @FXML
    NumberAxis osaY;




    // сервисы генерации docx для отчетов
    BookDocService bookDocService;
    ArrivalDocService arrivalDocService;




    // инициалицазия
    @FXML
    public void initialize() {


        // инциализация таблиц, создание полей
        initGenreTable();
        initAuthorTable();
        initBookTable();
        initArrivalTable();
        // установка начальной даты для фильтра
        fromDate.setValue(LocalDate.now().minusDays(7));
        toDate.setValue(LocalDate.now());

        // создание сервисов для генерации docx
        bookDocService = new BookDocService();

        // если создали docx без ошибок
        bookDocService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                String msg = (String) event.getSource().getValue();
                showInfo("Соханено в "+msg);
            }
        });

        // если ошибка
        bookDocService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Throwable throwable = bookDocService.getException();
                throwable.printStackTrace();
                showAlert(throwable.getMessage());

            }
        });

        arrivalDocService=new ArrivalDocService();

        arrivalDocService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                String msg = (String) event.getSource().getValue();
                showInfo("Соханено в "+msg);

            }
        });

        arrivalDocService.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                Throwable throwable = arrivalDocService.getException();
                throwable.printStackTrace();
                showAlert(throwable.getMessage());
            }
        });


        // надписи для осей
        osaX.setLabel("Год");
        osaY.setLabel("Книги");

        // загрузим жанры, авторы и книги
        // книги грузятся по умолчанию для первого автора
        // эти же книги будут использоваться для формирования поступления
        // т.е. предлагать для выбора список книг определенного автора
        // типа если у книг много - то что бы список был покороче
        loadGenres();
        loadAuthors();
        loadBooks();


    }

    // загрузка поступлений в таблицу
    public void loadArrival() {
        final LocalDate from = fromDate.getValue();
        final LocalDate to = toDate.getValue();
        // в отдельном потоке, так как их может быть много
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    arrivals.clear();
                    arrivals.addAll(MainApp.getBookService().getAllArrivalByPeriod(from, to, MainApp.getUser().getLogin()));
                } catch (RemoteException e) {
                    showAlert("Ошибка " + e.getMessage());
                    if (e.getMessage().contains("Connection refused")){
                        MainApp.connect();
                    }
                }
            }
        });
    }


    // инициализация таблицы книг
    private void initBookTable() {

        // cоздадим колонки согласно типма данных
        TableColumn<Book, String> nameColumn = new TableColumn<Book, String>("Название");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Book, String>("name"));
        nameColumn.setPrefWidth(250);
        tabBook.getColumns().add(nameColumn);



        TableColumn<Book, Genre> genreColumn = new TableColumn<Book, Genre>("Жанр");
        genreColumn.setCellValueFactory(
                Item -> {
                    SimpleObjectProperty property = new SimpleObjectProperty();
                    String name = String.format("%s", Item.getValue().getGenre().getName());
                    property.setValue(name);
                    return property;
                }
        );
        genreColumn.setPrefWidth(250);
        tabBook.getColumns().add(genreColumn);

        TableColumn<Book, Author> authorColumn = new TableColumn<Book, Author>("Автор");
        authorColumn.setCellValueFactory(new PropertyValueFactory<Book,Author>("author"));
        authorColumn.setCellValueFactory(
                Item -> {
                    SimpleObjectProperty property = new SimpleObjectProperty();
                    String name = String.format("%s", Item.getValue().getAuthor().getLastName() + " "
                            + Item.getValue().getAuthor().getFirstName());
                    property.setValue(name);
                    return property;
                }
        );
        authorColumn.setPrefWidth(250);
        tabBook.getColumns().add(authorColumn);

        // если зашли под админом - то покажем кто последний правил
        if (MainApp.getUser().getRole() == Role.ADMIN) {
            TableColumn<Book, User> modifiedBy = new TableColumn<Book, User>("Изменил");
            modifiedBy.setCellValueFactory(new PropertyValueFactory<Book,User>("modifiedBy"));
            modifiedBy.setCellValueFactory(
                    Item -> {
                        SimpleObjectProperty property = new SimpleObjectProperty();
                        String name = String.format("%s %s", Item.getValue().getModifiedBy().getFirstName(), Item.getValue().getModifiedBy().getFirstName());
                        property.setValue(name);
                        return property;
                    }
            );
            modifiedBy.setPrefWidth(250);
            tabBook.getColumns().add(modifiedBy);

            TableColumn<Book, Boolean> visible = new TableColumn<Book, Boolean>("Видмый");
            visible.setCellValueFactory(new PropertyValueFactory<Book, Boolean>("visible"));
            visible.setPrefWidth(100);
            tabBook.getColumns().add(visible);
        }

        // укажем откуда брать книги
        tabBook.setItems(books);
        // выбирать можно только 1 книгу
        tabBook.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // создадим контекстное меню и добавим обработчки
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemAdd = new MenuItem("Добавить");
        itemAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Author author = (Author) authorFilter.getSelectionModel().getSelectedItem();
                // вызовем дилаго создания книги и получим что ввели
                Optional<Book> result = bookDialog(new Book(author)).showAndWait();
                if (result.isPresent()) {
                    try {
                        Book book = result.get();
                        // сохраним в базу через сервер
                        book = MainApp.getBookService().createBook(book.getName(), book.getGenre(), book.getAuthor(), MainApp.getUser().getLogin());
                        books.add(book);
                        tabBook.refresh();
                        tabBook.getSelectionModel().select(book);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }
                }
            }
        });

        contextMenu.getItems().add(itemAdd);

        MenuItem itemEdit = new MenuItem("Редактировать");
        itemEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Book book = (Book) tabBook.getSelectionModel().getSelectedItem();
                if (book == null) return;
                int idx = books.indexOf(book);
                Optional<Book> result = bookDialog(book).showAndWait();
                if (result.isPresent()) {
                    try {
                        Book updBook = result.get();
                        updBook.setId(book.getId());
                        updBook = MainApp.getBookService().updateBook(updBook, MainApp.getUser().getLogin());
                        final int id = book.getId();
                        books.removeIf(g -> g.getId() == id);
                        books.add(idx, updBook);
                        tabBook.refresh();
                        tabBook.getSelectionModel().select(updBook);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }

                }
            }
        });
        contextMenu.getItems().add(itemEdit);

        MenuItem itemDel = new MenuItem("Удалить");
        // так как у нас удаление на уровне сервера, не админу не даст удалить
        // то тут не заморачиваемся и просто даем "удалять" всем
        itemDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Book book = (Book) tabBook.getSelectionModel().getSelectedItem();
                if (book == null) return;
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + book.getName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.showAndWait();

                    if (alert.getResult() != ButtonType.YES) {
                        return;
                    }
                    boolean delete = MainApp.getBookService().deleteBook(book, MainApp.getUser().getLogin());
                    if (delete) books.remove(book);

                } catch (RemoteException e) {
                    String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                    showAlert(msg);
                }
            }
        });
        contextMenu.getItems().add(itemDel);

        // если пользователь админ - то разрешим делать видимым то что скрыл менеджер
        if (MainApp.getUser().getRole() == Role.ADMIN) {

            MenuItem itemVisible = new MenuItem("Сделать видимым");
            itemVisible.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Book book = (Book) tabBook.getSelectionModel().getSelectedItem();
                    int idx = books.indexOf(book);
                    book.setVisible(true);
                    try {
                        Book bookUpd = new Book(book.getId(), book.getName(), book.getAuthor(), book.getGenre(),  true);
                        bookUpd = MainApp.getBookService().updateBook(bookUpd, MainApp.getUser().getLogin());
                        final int id = book.getId();
                        books.removeIf(g -> g.getId() == id);
                        books.add(idx, bookUpd);
                        tabBook.getSelectionModel().select(bookUpd);

                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }

                }
            });
            contextMenu.getItems().add(itemVisible);
        }

        // данные можно обновлять
        MenuItem itemRefresh = new MenuItem("Обновить");
        itemRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadBooks();
            }
        });
        contextMenu.getItems().add(itemRefresh);

        tabBook.setContextMenu(contextMenu);
    }

    // инициализация таблицы поступлений
    private void initArrivalTable() {

        TableColumn<Arrival, LocalDate> dateColumn = new TableColumn<Arrival, LocalDate>("Дата прихода");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalDate"));
        dateColumn.setCellFactory(column -> new TableCell<Arrival, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(formatter.format(date));
                }
        }});
        tabArrival.getColumns().add(dateColumn);

        TableColumn<Arrival, Book> bookColumn = new TableColumn<Arrival, Book>("Книга");
        bookColumn.setCellValueFactory(new PropertyValueFactory<Arrival,Book>("book"));
        bookColumn.setCellValueFactory(
                Item -> {
                    SimpleObjectProperty property = new SimpleObjectProperty();
                    String name = String.format("%s", Item.getValue().getBook().getName());
                    property.setValue(name);
                    return property;
                }
        );
        bookColumn.setPrefWidth(250);
        tabArrival.getColumns().add(bookColumn);

        TableColumn<Arrival, Book> authorColumn = new TableColumn<Arrival, Book>("Автор");
        authorColumn.setCellValueFactory(new PropertyValueFactory<Arrival,Book>("book"));
        authorColumn.setCellValueFactory(
                Item -> {
                    SimpleObjectProperty property = new SimpleObjectProperty();
                    String name = String.format("%s", Item.getValue().getBook().getAuthor());
                    property.setValue(name);
                    return property;
                }
        );
        bookColumn.setPrefWidth(250);
        tabArrival.getColumns().add(authorColumn);


        TableColumn<Arrival, Book> genreColumn = new TableColumn<Arrival, Book>("Жанр");
        genreColumn.setCellValueFactory(new PropertyValueFactory<Arrival,Book>("book"));
        genreColumn.setCellValueFactory(
                Item -> {
                    SimpleObjectProperty property = new SimpleObjectProperty();
                    String name = String.format("%s", Item.getValue().getBook().getGenre());
                    property.setValue(name);
                    return property;
                }
        );
        genreColumn.setPrefWidth(250);
        tabArrival.getColumns().add(genreColumn);

        TableColumn<Arrival, String> publishColumn = new TableColumn<Arrival, String>("Издательство");
        publishColumn.setCellValueFactory(new PropertyValueFactory<Arrival, String>("bookPublisher"));
        publishColumn.setPrefWidth(250);
        tabArrival.getColumns().add(publishColumn);


        TableColumn<Arrival, Integer> yearColumn = new TableColumn<Arrival, Integer>("Год выпуска");
        yearColumn.setCellValueFactory(new PropertyValueFactory<Arrival, Integer>("yearOf"));
        yearColumn.setPrefWidth(200);
        tabArrival.getColumns().add(yearColumn);

        TableColumn<Arrival, Integer> pagesColumn = new TableColumn<Arrival, Integer>("Кол-во страниц");
        pagesColumn.setCellValueFactory(new PropertyValueFactory<Arrival, Integer>("pages"));
        pagesColumn.setPrefWidth(250);
        tabArrival.getColumns().add(pagesColumn);

        TableColumn<Arrival, Integer> priceColumn = new TableColumn<Arrival, Integer>("Цена");
        priceColumn.setCellValueFactory(new PropertyValueFactory<Arrival, Integer>("price"));
        priceColumn.setPrefWidth(200);
        tabArrival.getColumns().add(priceColumn);


        TableColumn<Arrival, Integer> amountColumn = new TableColumn<Arrival, Integer>("Кол-во");
        amountColumn.setCellValueFactory(new PropertyValueFactory<Arrival, Integer>("amount"));
        amountColumn.setPrefWidth(200);

        tabArrival.getColumns().add(amountColumn);


        if (MainApp.getUser().getRole() == Role.ADMIN) {
            TableColumn<Arrival, User> modifiedBy = new TableColumn<Arrival, User>("Изменил");
            modifiedBy.setCellValueFactory(
                    Item -> {
                        SimpleObjectProperty property = new SimpleObjectProperty();
                        String name = String.format("%s %s", Item.getValue().getModifiedBy().getFirstName(), Item.getValue().getModifiedBy().getFirstName());
                        property.setValue(name);
                        return property;
                    }
            );
            modifiedBy.setPrefWidth(250);
            tabArrival.getColumns().add(modifiedBy);

            TableColumn<Arrival, Boolean> visible = new TableColumn<Arrival, Boolean>("Видмый");
            visible.setCellValueFactory(new PropertyValueFactory<Arrival, Boolean>("visible"));
            visible.setPrefWidth(100);
            tabArrival.getColumns().add(visible);
        }
        tabArrival.setItems(arrivals);
        tabArrival.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemAdd = new MenuItem("Добавить");
        itemAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                Optional<Arrival> result = arrivalDialog(new Arrival()).showAndWait();
                if (result.isPresent()) {
                    try {
                        Arrival arrival = result.get();
                        arrival = MainApp.getBookService().createArrival(arrival, MainApp.getUser().getLogin());
                        arrivals.add(arrival);
                        tabArrival.refresh();
                        tabArrival.getSelectionModel().select(arrival);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }
                }
            }
        });

        contextMenu.getItems().add(itemAdd);

        MenuItem itemEdit = new MenuItem("Редактировать");
        itemEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Arrival arrival = (Arrival) tabArrival.getSelectionModel().getSelectedItem();
                if (arrival == null) return;
                int idx = arrivals.indexOf(arrival);
                Optional<Arrival> result = arrivalDialog(arrival).showAndWait();
                if (result.isPresent()) {
                    try {
                        Arrival updArrival = result.get();

                        updArrival = MainApp.getBookService().updateArrival(updArrival, MainApp.getUser().getLogin());
                        final int id = arrival.getId();
                        arrivals.removeIf(g -> g.getId() == id);
                        arrivals.add(idx, updArrival);
                        tabArrival.refresh();
                        tabArrival.getSelectionModel().select(updArrival);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }

                }
            }
        });
        contextMenu.getItems().add(itemEdit);

        MenuItem itemDel = new MenuItem("Удалить");
        itemDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Arrival arrival = (Arrival) tabArrival.getSelectionModel().getSelectedItem();
                if (arrival == null) return;
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить приход книги" + arrival.getBook().getName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.showAndWait();

                    if (alert.getResult() != ButtonType.YES) {
                        return;
                    }
                    boolean delete = MainApp.getBookService().deleteArrival(arrival, MainApp.getUser().getLogin());
                    if (delete) arrivals.remove(arrival);

                } catch (RemoteException e) {
                    String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                    showAlert(msg);
                }
            }
        });
        contextMenu.getItems().add(itemDel);

        if (MainApp.getUser().getRole() == Role.ADMIN) {

            MenuItem itemVisible = new MenuItem("Сделать видимым");
            itemVisible.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Arrival arrival = (Arrival) tabArrival.getSelectionModel().getSelectedItem();
                    int idx = arrivals.indexOf(arrival);
                    arrival.setVisible(true);
                    try {
                        Arrival arrivalUpd =new Arrival(arrival);
                        arrival.setVisible(true);
                        arrivalUpd = MainApp.getBookService().updateArrival(arrivalUpd, MainApp.getUser().getLogin());
                        final int id = arrivalUpd.getId();
                        arrivals.removeIf(g -> g.getId() == id);
                        arrivals.add(idx, arrivalUpd);
                        tabArrival.getSelectionModel().select(arrivalUpd);

                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }

                }
            });
            contextMenu.getItems().add(itemVisible);
        }

        MenuItem itemRefresh = new MenuItem("Обновить");
        itemRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadArrival();
            }
        });
        contextMenu.getItems().add(itemRefresh);

        tabArrival.setContextMenu(contextMenu);
    }

    // инициализация таблицы авторов
    private void initAuthorTable() {
        TableColumn<Author, String> firstNameColumn = new TableColumn<Author, String>("Имя");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Author, String>("firstName"));
        firstNameColumn.setPrefWidth(250);
        tabAuthor.getColumns().add(firstNameColumn);
        TableColumn<Author, String> LastNameColumn = new TableColumn<Author, String>("Фамилия");
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<Author, String>("lastName"));
        LastNameColumn.setPrefWidth(250);
        tabAuthor.getColumns().add(LastNameColumn);
        if (MainApp.getUser().getRole() == Role.ADMIN) {
            TableColumn<Author, User> modifiedBy = new TableColumn<Author, User>("Изменил");
            modifiedBy.setCellValueFactory(
                    Item -> {
                        SimpleObjectProperty property = new SimpleObjectProperty();
                        String name = String.format("%s %s", Item.getValue().getModifiedBy().getFirstName(), Item.getValue().getModifiedBy().getFirstName());
                        property.setValue(name);
                        return property;
                    }
            );
            modifiedBy.setPrefWidth(250);
            tabAuthor.getColumns().add(modifiedBy);

            TableColumn<Author, Boolean> visible = new TableColumn<Author, Boolean>("Видмый");
            visible.setCellValueFactory(new PropertyValueFactory<Author, Boolean>("visible"));
            visible.setPrefWidth(100);
            tabAuthor.getColumns().add(visible);
        }
        tabAuthor.setItems(authors);
        tabAuthor.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemAdd = new MenuItem("Добавить");
        itemAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                Optional<Author> result = authorDialog(new Author()).showAndWait();
                if (result.isPresent()) {
                    try {
                        Author author = result.get();
                        author = MainApp.getBookService().createAuthor(author.getFirstName(), author.getLastName(), MainApp.getUser().getLogin());
                        authors.add(author);
                        tabAuthor.refresh();
                        tabAuthor.getSelectionModel().select(author);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }
                }
            }
        });

        contextMenu.getItems().add(itemAdd);

        MenuItem itemEdit = new MenuItem("Редактировать");
        itemEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Author author = (Author) tabAuthor.getSelectionModel().getSelectedItem();
                if (author == null) return;
                int idx = authors.indexOf(author);
                Optional<Author> result = authorDialog(author).showAndWait();
                if (result.isPresent()) {
                    try {
                        Author updAuthor = result.get();
                        updAuthor.setId(author.getId());
                        updAuthor = MainApp.getBookService().updateAuthor(updAuthor, MainApp.getUser().getLogin());
                        final int id = author.getId();
                        authors.removeIf(g -> g.getId() == id);
                        authors.add(idx, updAuthor);
                        tabAuthor.refresh();
                        tabAuthor.getSelectionModel().select(updAuthor);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }
                }
            }
        });
        contextMenu.getItems().add(itemEdit);

        MenuItem itemDel = new MenuItem("Удалить");
        itemDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Author author = (Author) tabAuthor.getSelectionModel().getSelectedItem();
                if (author == null) return;
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + author.getFirstName() + " " + author.getLastName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.showAndWait();

                    if (alert.getResult() != ButtonType.YES) {
                        return;
                    }
                    boolean delete = MainApp.getBookService().deleteAuthor(author, MainApp.getUser().getLogin());
                    if (delete) authors.remove(author);

                } catch (RemoteException e) {
                    String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                    showAlert(msg);
                }
            }
        });
        contextMenu.getItems().add(itemDel);

        if (MainApp.getUser().getRole() == Role.ADMIN) {

            MenuItem itemVisible = new MenuItem("Сделать видимым");
            itemVisible.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Author author = (Author) tabAuthor.getSelectionModel().getSelectedItem();
                    int idx = authors.indexOf(author);
                    author.setVisible(true);
                    try {
                        Author authorUpd = new Author(author.getFirstName(), author.getLastName());
                        authorUpd = MainApp.getBookService().updateAuthor(authorUpd, MainApp.getUser().getLogin());
                        final int id = author.getId();
                        authors.removeIf(g -> g.getId() == id);
                        authors.add(idx, authorUpd);
                        tabAuthor.getSelectionModel().select(authorUpd);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }

                }
            });
            contextMenu.getItems().add(itemVisible);
        }

        MenuItem itemRefresh = new MenuItem("Обновить");
        itemRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadAuthors();
            }
        });
        contextMenu.getItems().add(itemRefresh);

        tabAuthor.setContextMenu(contextMenu);
    }

    // инициализация таблицы жанров
    private void initGenreTable() {
        TableColumn<Genre, String> nameColumn = new TableColumn<Genre, String>("Название");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Genre, String>("name"));
        nameColumn.setPrefWidth(250);
        tabGenre.getColumns().add(nameColumn);
        if (MainApp.getUser().getRole() == Role.ADMIN) {
            TableColumn<Genre, User> modifiedBy = new TableColumn<Genre, User>("Изменил");
            modifiedBy.setCellValueFactory(new PropertyValueFactory<Genre,User>("modifiedBy"));
            modifiedBy.setCellValueFactory(
                    Item -> {
                        SimpleObjectProperty property = new SimpleObjectProperty();
                        String name = String.format("%s %s", Item.getValue().getModifiedBy().getFirstName(), Item.getValue().getModifiedBy().getFirstName());
                        property.setValue(name);
                        return property;
                    }
            );
            modifiedBy.setPrefWidth(250);
            tabGenre.getColumns().add(modifiedBy);

            TableColumn<Genre, Boolean> visible = new TableColumn<Genre, Boolean>("Видмый");
            visible.setCellValueFactory(new PropertyValueFactory<Genre, Boolean>("visible"));
            visible.setPrefWidth(100);
            tabGenre.getColumns().add(visible);
        }
        tabGenre.setItems(genres);
        tabGenre.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem itemAdd = new MenuItem("Добавить");
        itemAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Genre genre = new Genre(); //(Genre) tGenre.getSelectionModel().getSelectedItem();
                Optional<Genre> result = genreDialog(genre).showAndWait();
                if (result.isPresent()) {
                    try {
                        genre.setName(result.get().getName());
                        genre = MainApp.getBookService().createGenre(genre.getName(), MainApp.getUser().getLogin());
                        genres.add(genre);
                        tabGenre.refresh();
                        tabGenre.getSelectionModel().select(genre);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }
                }
            }
        });

        contextMenu.getItems().add(itemAdd);

        MenuItem itemEdit = new MenuItem("Редактировать");
        itemEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Genre genre = (Genre) tabGenre.getSelectionModel().getSelectedItem();
                if (genre == null) return;
                int idx = genres.indexOf(genre);
                Optional<Genre> result = genreDialog(genre).showAndWait();
                if (result.isPresent()) {
                    try {
                        Genre updGenre = new Genre();
                        updGenre.setId(genre.getId());
                        updGenre.setName(result.get().getName());
                        updGenre = MainApp.getBookService().updateGenre(updGenre, MainApp.getUser().getLogin());
                        final int id = genre.getId();
                        genres.removeIf(g -> g.getId() == id);
                        genres.add(idx, updGenre);
                        tabGenre.refresh();
                        tabGenre.getSelectionModel().select(updGenre);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }
                }
            }
        });
        contextMenu.getItems().add(itemEdit);

        MenuItem itemDel = new MenuItem("Удалить");
        itemDel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Genre genre = (Genre) tabGenre.getSelectionModel().getSelectedItem();
                if (genre == null) return;
                try {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + genre.getName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    alert.showAndWait();

                    if (alert.getResult() != ButtonType.YES) {
                        return;
                    }
                    boolean delete = MainApp.getBookService().deleteGenre(genre, MainApp.getUser().getLogin());
                    if (delete) genres.remove(genre);

                } catch (RemoteException e) {
                    String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                    showAlert(msg);
                }
            }
        });
        contextMenu.getItems().add(itemDel);

        if (MainApp.getUser().getRole() == Role.ADMIN) {

            MenuItem itemVisible = new MenuItem("Сделать видимым");
            itemVisible.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Genre genre = (Genre) tabGenre.getSelectionModel().getSelectedItem();
                    int idx = genres.indexOf(genre);
                    genre.setVisible(true);
                    try {
                        Genre genreUpd = MainApp.getBookService().updateGenre(genre, MainApp.getUser().getLogin());
                        genres.removeIf(g -> g.getId() == genre.getId());
                        genres.add(idx, genreUpd);
                        tabGenre.getSelectionModel().select(genreUpd);
                    } catch (RemoteException e) {
                        String msg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
                        showAlert(msg);
                    }

                }
            });
            contextMenu.getItems().add(itemVisible);
        }

        MenuItem itemRefresh = new MenuItem("Обновить");
        itemRefresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                loadGenres();
            }
        });
        contextMenu.getItems().add(itemRefresh);

        tabGenre.setContextMenu(contextMenu);


    }

    // загрузка жанров
    private void loadGenres() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    genres.clear();
                    genres.addAll(MainApp.getBookService().getAllGenre(MainApp.getUser().getLogin()));
                } catch (RemoteException e) {
                    showAlert("Ошибка " + e.getMessage());
                    if (e.getMessage().contains("Connection refused")){
                        // попробуем переподключится
                        MainApp.connect();
                    }
                }
            }
        });
    }

    // загрузка книг
    private void loadBooks() {
        // так загрузка по фильтру авторов - то применим фильтр
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                applyAuthorFilter();
            }
        });
    }

    // загрузка авторов
    private void loadAuthors() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Author selectedItem = (Author) authorFilter.getSelectionModel().getSelectedItem();
                    authors.clear();
                    authors.addAll(MainApp.getBookService().getAllAuthor(MainApp.getUser().getLogin()));
                    if (selectedItem!=null && authors.contains(selectedItem)) authorFilter.setItems(authors);
                    else authorFilter.getSelectionModel().selectFirst();
                    ObservableList<Author> forFilter = FXCollections.observableArrayList(authors);
                    forFilter.add(0,new Author("","Все"));
                    authorFilter.setItems(forFilter);
                    loadBooks();

                } catch (RemoteException e) {
                    showAlert("Ошибка " + e.getMessage());
                    if (e.getMessage().contains("Connection refused")){
                        MainApp.connect();
                    }
                }
            }
        });
    }

    // вызывате Alert WARNING с текстом message
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка!");
        alert.setHeaderText("Внимание!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // вызывате Alert INFORMATION с текстом message
    public void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Внимание!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // возвращает диалог Gengre
    Dialog genreDialog(Genre genre) {
        final Dialog<Genre> dialog = new Dialog<>();
        dialog.setTitle("Жанр");
        if (genre.getId() == 0)
            dialog.setHeaderText("Добавление нового жанра");
        else dialog.setHeaderText("Редактирование  жанра");
        dialog.setResizable(false);

        Label label1 = new Label("Название: ");
        TextField tName = new TextField();
        tName.setText(genre.getName());

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(tName, 2, 1);
        dialog.getDialogPane().setContent(grid);

        final ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        final ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeOk);


        dialog.setResultConverter(new javafx.util.Callback<ButtonType, Genre>() {
            @Override
            public Genre call(ButtonType buttonType) {
                if (buttonType == buttonTypeOk) {
                    return new Genre(tName.getText());
                }
                return null;
            }
        });


        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (tName.getText() == null || tName.getText().isBlank()) {
                showAlert("Название нужно указать!");
                event.consume();
            }
        });

        return dialog;
    }


    // возвращает диалог автора
    Dialog authorDialog(Author author) {
        final Dialog<Author> dialog = new Dialog<>();
        dialog.setTitle("Автор");
        if (author.getId() == 0)
            dialog.setHeaderText("Добавление нового автора");
        else dialog.setHeaderText("Редактирование  автора");
        dialog.setResizable(false);

        Label label1 = new Label("Имя: ");
        TextField tFirstName = new TextField();
        tFirstName.setText(author.getFirstName());

        Label label2 = new Label("Фамилия: ");
        TextField tLastName = new TextField();
        tLastName.setText(author.getLastName());

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(tFirstName, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(tLastName, 2, 2);
        grid.setHgap(5);
        grid.setVgap(5);
        dialog.getDialogPane().setContent(grid);

        final ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        final ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeOk);


        dialog.setResultConverter(new javafx.util.Callback<ButtonType, Author>() {
            @Override
            public Author call(ButtonType buttonType) {
                if (buttonType == buttonTypeOk) {
                    return new Author(tFirstName.getText(), tLastName.getText());
                }
                return null;
            }
        });


        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (tLastName.getText() == null || tLastName.getText().isBlank()) {
                showAlert("Фамилию нужно указать!");
                event.consume();
            }
        });

        return dialog;
    }

    // возвращает диалог книги
    Dialog bookDialog(Book book) {
        final Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Книна");
        if (book.getId() == 0)
            dialog.setHeaderText("Добавление новой книги");
        else dialog.setHeaderText("Редактирование  книги");
        dialog.setResizable(false);

        Label label1 = new Label("Название");
        TextField tName = new TextField();
        tName.setText(book.getName());




        Label label3 = new Label("Жанр");
        ChoiceBox<Genre> tGenre = new ChoiceBox<>();
        tGenre.setItems(genres);

        if (book.getId() > 0) tGenre.getSelectionModel().select(book.getGenre());

        Label label4 = new Label("Автор");
        ChoiceBox<Author> tAuthor = new ChoiceBox<>();
        tAuthor.setItems(authors);
        if (book.getId() > 0) tAuthor.getSelectionModel().select(book.getAuthor());

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(tName, 2, 1);

        grid.add(label3, 1, 3);
        grid.add(tGenre, 2, 3);
        grid.add(label4, 1, 4);
        grid.add(tAuthor, 2, 4);
        grid.setHgap(5);
        grid.setVgap(5);
        dialog.getDialogPane().setContent(grid);

        final ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        final ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeOk);


        dialog.setResultConverter(new javafx.util.Callback<ButtonType, Book>() {
            @Override
            public Book call(ButtonType buttonType) {
                if (buttonType == buttonTypeOk) {
                    Author author = tAuthor.getSelectionModel().getSelectedItem();
                    Genre genre = tGenre.getSelectionModel().getSelectedItem();
                    return new Book(tName.getText(), author, genre,  true);
                }
                return null;
            }
        });


        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (tName.getText() == null || tName.getText().isBlank()) {
                showAlert("Название нужно указать!");
                event.consume();
            }
            if (tGenre.getSelectionModel().getSelectedItem() == null) {
                showAlert("Жанр нужно указать!");
                event.consume();
            }
            if (tAuthor.getSelectionModel().getSelectedItem() == null) {
                showAlert("Автора нужно указать!");
                event.consume();
            }

        });

        return dialog;
    }

    // применить фильтр по автору, т.е. загрузить с сервера
    @FXML
    public void applyAuthorFilter() {
        Author author = (Author) authorFilter.getSelectionModel().getSelectedItem();
        if (author == null && !authors.isEmpty())
            author = authors.get(0);
        if (author==null) return;
        books.clear();
        try {
            List<Book> list = new ArrayList<>();
            if (author.getId()==0) list.addAll(MainApp.getBookService().getAllBook(MainApp.getUser().getLogin()));
                else list.addAll(MainApp.getBookService().getAllBookByAuthor(author, MainApp.getUser().getLogin()));
            books.addAll(list);
        } catch (RemoteException e) {
            showAlert(e.getMessage());
            if (e.getMessage().contains("Connection refused")){
                MainApp.connect();
            }
        }
    }

    // диалог поступления книги
    Dialog<Arrival> arrivalDialog(Arrival src) {
        Arrival arrival = new Arrival(src);

        final Dialog<Arrival> dialog = new Dialog<>();
        dialog.setTitle("Книга");
        if (arrival.getId() == 0)
            dialog.setHeaderText("Поступление новой книги");
        else dialog.setHeaderText("Редактирование прихода");
        dialog.setResizable(false);


        Label labelAdd = new Label("Дата поступления");
        DatePicker dateArrivalPicker = new DatePicker();
        if (arrival.getArrivalDate()!=null) dateArrivalPicker.setValue(arrival.getArrivalDate());


        Label labelBook= new Label("Книга");
        ChoiceBox<Book> tBook = new ChoiceBox<>();
        tBook.setItems(books);
        if (arrival.getBook()!=null) tBook.getSelectionModel().select(arrival.getBook());
        else tBook.getSelectionModel().selectFirst();

        Label labelPublisher = new Label("Издательство");
        TextField tPublisher = new TextField();
        if (arrival.getBookPublisher()!=null) tPublisher.setText(arrival.getBookPublisher());

        Label labelYear = new Label("Год выпуска");
        Spinner<Integer> tYear = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactoryYear = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, arrival.getYearOf());
        tYear.setEditable(true);
        tYear.setValueFactory(valueFactoryYear);

        Label labelPages = new Label("Кол-во страниц");
        Spinner<Integer> tPages = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactoryPages =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, arrival.getPages());
        tPages.setEditable(true);
        tPages.setValueFactory(valueFactoryPages);

        Label labelPrice = new Label("Цена");
        Spinner<Integer> tPrice = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactoryPrice = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, arrival.getPrice());
        tPrice.setEditable(true);
        tPrice.setValueFactory(valueFactoryPrice);

        Label labelAmount = new Label("Кол-во");
        Spinner<Integer> tAmount = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactoryAmount = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, arrival.getAmount());
        tAmount.setEditable(true);
        tAmount.setValueFactory(valueFactoryAmount);



        GridPane grid = new GridPane();

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow( Priority.ALWAYS );

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow( Priority.ALWAYS );



        grid.getColumnConstraints().addAll( new ColumnConstraints( 40 ), col1, new ColumnConstraints( 250 ), col2 );

        grid.add(labelAdd, 1, 1);
        grid.add(dateArrivalPicker, 2, 1);

        grid.add(labelBook, 1, 2);
        grid.add(tBook, 2, 2);

        grid.add(labelPublisher, 1, 3);
        grid.add(tPublisher, 2, 3);

        grid.add(labelPages, 1, 4);
        grid.add(tPages, 2, 4);

        grid.add(labelYear, 1, 5);
        grid.add(tYear, 2, 5);

        grid.add(labelPrice, 1, 6);
        grid.add(tPrice, 2, 6);

        grid.add(labelAmount, 1, 7);
        grid.add(tAmount, 2, 7);

        grid.setHgap(5);
        grid.setVgap(5);
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setAlignment(Pos.CENTER_LEFT);



        dialog.getDialogPane().setContent(grid);
        grid.getScene().getWindow().sizeToScene();;


        final ButtonType buttonTypeOk = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        final ButtonType buttonTypeCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel, buttonTypeOk);


        dialog.setResultConverter(new javafx.util.Callback<ButtonType, Arrival>() {
            @Override
            public Arrival call(ButtonType buttonType) {
                if (buttonType == buttonTypeOk) {
                    arrival.setArrivalDate(dateArrivalPicker.getValue());
                    arrival.setBook(tBook.getSelectionModel().getSelectedItem());
                    arrival.setPrice(tPrice.getValue());
                    arrival.setYearOf(tYear.getValue());
                    arrival.setAmount(tAmount.getValue());
                    arrival.setPages(tPages.getValue());
                    arrival.setBookPublisher(tPublisher.getText());
                    return arrival;
                }
                return null;
            }
        });


        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {

            if (dateArrivalPicker.getValue() == null) {
                showAlert("Дату нужно указать!");
                event.consume();
                return;
            }

            if (tBook.getSelectionModel().getSelectedItem() == null ) {
                showAlert("Книгу нужно указать!");
                event.consume();
                return;
            }



            if (tPublisher.getText() == null || tPublisher.getText().isBlank()) {
                showAlert("Издательство нужно указать!");
                event.consume();
                return;
            }

            if (tPages.getValue()==null || tPages.getValue()<=0){
                showAlert("Количество страниц нужно указать!");
                event.consume();
                return;
            }


            if (tPrice.getValue() == null ||tPrice.getValue()<=0) {
                showAlert("Стоимость нужно указать!");
                event.consume();
            }
            if (tYear.getValue() == null ||tYear.getValue()<=0) {
                showAlert("Год нужно указать!");
                event.consume();
                return;
            }
            if (tAmount.getValue() == null ||tAmount.getValue()<=0) {
                showAlert("Количество нужно указать!");
                event.consume();
                return;
            }

        });

        return dialog;
    }

    // обовим поступления за период
    @FXML
    void updatePeriod()
    {
        loadArrival();
    }

    // генерирует отчет по поступлениям
    @FXML
    void reportArrival(){
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        // запустим сервис
        arrivalDocService.startService(from,to,arrivals);


    }
    // генерирует отчет по книгам по автору
    @FXML
    void reportBook(){
        Author author = (Author) authorFilter.getSelectionModel().getSelectedItem();
        if (author==null) return;
        List<Book> bookList = new ArrayList<>(books);
        bookDocService.startService(author,books);

    }

    // строит круговую диаграмму
    @FXML
    private void buildChart(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pieChart.getData().clear();
                try {

                    List<GenreReport> list = MainApp.getBookService().getGenreCount(MainApp.getUser().getLogin());
                    for(GenreReport report:list){
                        PieChart.Data slice = new PieChart.Data(report.getName(), report.getCount());
                        pieChart.getData().add(slice);
                    }

                    pieChart.getData().forEach(data ->
                            data.nameProperty().bind(
                                    Bindings.concat(
                                            data.getName(), "- ", String.format("%.0f", data.pieValueProperty().getValue())
                                    )
                            )
                    );


                } catch (RemoteException e) {
                    e.printStackTrace();
                    showAlert(e.getMessage());
                    if (e.getMessage().contains("Connection refused")){
                        MainApp.connect();
                    }
                }
            }
        });
    }

    // строит гистограмму
    @FXML
    private void buildBar(){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                barChart.getData().clear();
                try {


                    ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();


                    List<YearReport> list = MainApp.getBookService().getYearCount(MainApp.getUser().getLogin());

                    for(YearReport report:list){
                        XYChart.Series series = new XYChart.Series();
                        series.setName(String.format("%d",report.getYearOf()));
                        series.getData().add(new XYChart.Data(String.format("%d",report.getYearOf()), report.getBookCount()));
                        barChartData.add(series);
                    }
                    barChart.setData(barChartData);


                } catch (RemoteException e) {
                    e.printStackTrace();
                    showAlert(e.getMessage());
                    if (e.getMessage().contains("Connection refused")){
                        MainApp.connect();
                    }
                }
            }
        });
    }

    // всё)

}

