package com.app.dao;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * класс фабрика DataSource
 */
public class DataSourceFactory {
    /**
     * создает подключение к базе и возвращает DataSource
     * @return dataSource
     */
    public static DataSource getDataSource() {
        String url = "jdbc:mysql://%s:%s/%s";

        Properties props = new Properties();
        FileInputStream fis = null;
        MysqlDataSource myDS = null;
        try {
            fis = new FileInputStream("db.conf");
            props.load(fis);
            myDS = new MysqlDataSource();
            String host = props.getProperty("DB_HOST");
            String port = props.getProperty("DB_PORT");
            String base = props.getProperty("DB_NAME");
            String user = props.getProperty("DB_USERNAME");
            String pass = props.getProperty("DB_PASSWORD");
            String URL = String.format(url,host,port,base);
            myDS.setURL(URL);
            myDS.setUser(user);
            myDS.setPassword(pass);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return myDS;
    }


}
