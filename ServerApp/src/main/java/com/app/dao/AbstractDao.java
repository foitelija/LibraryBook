package com.app.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *  абстрактный клас Dao
 */
public abstract class AbstractDao {
    private DataSource dataSource;
    private Connection connection;


    public AbstractDao(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.connection = dataSource.getConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    public void startTransaction() throws SQLException {
        this.connection.setAutoCommit(false);
    }
    public void commit() throws SQLException {
        this.connection.commit();
        this.connection.setAutoCommit(true);
    }
    public void rollback() throws SQLException {
        this.connection.rollback();
        this.connection.setAutoCommit(true);
    }

}
