package com.app.controller;

import java.io.IOException;
import java.rmi.RemoteException;

import com.app.MainApp;

import com.app.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


/**
 * контроллер авторизации
 */
public class AuthController {


    @FXML
    TextField fLogin;
    @FXML
    PasswordField fPassword;


    @FXML
    public void initialize() {

    }

    @FXML
    private void login() throws IOException {
        String login = fLogin.getText();
        String password = fPassword.getText();
        if (login.isBlank() || password.isBlank()){
            showAlert("Заполните оба поля!");
            return;
        }
        try {
            User user = MainApp.getBookService().auth(login,password);
            MainApp.setUser(user);
            MainApp.setRoot("main");
        }catch (RemoteException e){
           showAlert(e.getCause().getMessage());
        }


    }


    @FXML
    private void register() throws IOException {
        // если нажал кнопку регистрации - вызовем форму регистрации
        MainApp.setRoot("register");
    }

    // выводит сообщение об ошибке
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("");
        alert.setHeaderText("Внимание!");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // если нажал кнопку отмена - то завершим приложение
    @FXML
    private void cancel() throws IOException {
        MainApp.exit();
    }
}
