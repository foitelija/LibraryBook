package com.app;

import com.app.model.User;
import com.app.service.BookService;
import javafx.application.Application;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class MainApp extends Application {
    private static Scene scene;
    private static BookService bookService;
    private static User user;
    private static  Stage stage;
    private static boolean main = false;

    public static boolean isMain() {
        return main;
    }

    public static void setMain(boolean main) {
        MainApp.main = main;
    }

    public static BookService getBookService() {
        return bookService;
    }



    public static User getUser() {
        return user;
    }


    public static void setUser(User u) {
        user = u;
    }

    public MainApp() {
    }

    public static void exit() {
        Platform.exit();
    }

    @Override
    public void start(final Stage stage) throws IOException {
        this.stage = stage;
        scene = new Scene(loadFXML("auth"), 400, 200);
        stage.setMinHeight(200);
        stage.setMinWidth(400);
        stage.setMaxHeight(800);
        stage.setMaxWidth(1400);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

        if (fxml.equals("auth")) {
            stage.setHeight(220);stage.setWidth(400);
            stage.setResizable(false);
        }else
        if (fxml.equals("register"))
        {

            stage.setHeight(350);stage.setWidth(400);
            stage.setResizable(false);
        }
        else {

            stage.setHeight(600);stage.setWidth(1000);
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
            stage.setResizable(true);
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }





    static final String SERVICE_NAME="BookService";


    static String ip;
    static int port;

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        ip = args[0];
        port = Integer.parseInt(args[1]);
        System.setProperty("java.rmi.server.hostname",ip);
        connect();
        launch(args);
    }

    public static void connect(){
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(ip,port);
            bookService = (BookService) registry.lookup(SERVICE_NAME);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
    }



}
