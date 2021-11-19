package com.app;


import com.app.service.BookService;
import com.app.service.impl.BookServiceImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.logging.LogManager;

/**
 * сервер RMI
 */
public class Server {

    /**
     * название сервиса
     */
    static final String SERVICE_NAME="BookService";
    public static void main(String[] args) throws RemoteException, SQLException {

        try {
            LogManager.getLogManager().readConfiguration(Server.class.getClassLoader().getResourceAsStream("loggin.properties"));
        } catch (IOException e) {

        }


        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        // первым параметром передаем порт, на котором будет работать сервер
        int port = Integer.parseInt(args[0]);
        // регистрируем порт
        Registry registry = LocateRegistry.createRegistry(port);
        // создаем сервиc
        BookService server =new BookServiceImpl();
        // привязываем сервис к имени
        registry.rebind(SERVICE_NAME, server);
        System.out.println("Server "+SERVICE_NAME+" start..");



    }






}
