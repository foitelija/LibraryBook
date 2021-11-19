package com.app.controller;

import java.io.IOException;
import java.rmi.RemoteException;


import com.app.MainApp;
import com.app.model.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * контроллер овечает за регистрацию
 */
public class RegisterController {

    @FXML
    TextField fLogin;
    @FXML
    TextField fPassword;

    @FXML
    TextField fFirstName;
    @FXML
    TextField fLastName;
    @FXML
    ComboBox chRole;


    // инициализация
    @FXML
    public void initialize() {
        // создадим список ролей
        ObservableList<String> roles = FXCollections.observableArrayList();
        roles.addAll("Администратор");
        roles.addAll("Менеджер");
        chRole.setItems(roles); chRole.getSelectionModel().select(1);

    }

    @FXML
    private void register() throws IOException {

        // считаем поля и проверим пусты ли
        String firstName = fFirstName.getText();
        if (firstName.isEmpty()){
            showAlert("Имя не может быть пустым");
            return;
        }

        String lastName = fLastName.getText();
        if (lastName.isEmpty()){
            showAlert("Фамилия не может быть пустой");
            return;
        }
        String login = fLogin.getText();
        if (login.isEmpty()){
            showAlert("Логин не может быть пустым");
            return;
        }
        String password = fPassword.getText();
        if (password.isEmpty()){
            showAlert("Пароль не может быть пустым");
            return;
        }

        String strRole = (String) chRole.getSelectionModel().getSelectedItem();
        // если все ок
        try {
            Role role =Role.MANAGER;
            if (strRole.equalsIgnoreCase("Администратор")) role=Role.ADMIN;
            // сохраним пользователя в базе
            MainApp.getBookService().saveUser(login,firstName,lastName,password,role.name());
            // переключимся на входи
            MainApp.setRoot("auth");
            // выведем сообщение
            showAlert("Теперь Вы можете ввойти под своим логином");

        }catch (RemoteException e){
            // сообщение об ошибке
            showAlert(e.getCause().getMessage());
        }
    }

    // если нажали отмена - вернемся на форму входа
    @FXML
    private void cancel() throws IOException {
        MainApp.setRoot("auth");
    }


    // выводит сообщение с типом INFORMATION
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
