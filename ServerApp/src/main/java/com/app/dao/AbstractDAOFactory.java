package com.app.dao;


import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * абстрактная фабрика Dao
 */
public abstract class AbstractDAOFactory {

    protected DataSource dataSource;

    public AbstractDAOFactory() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    public abstract UserDao getUserDao() throws SQLException;
    public abstract GenreDao getGenreDao() throws SQLException;
    public abstract BookDao getBookDao() throws SQLException;
    public abstract AuthorDao getAuthorDao() throws SQLException;
    public abstract ArrivalDao getArrivalDao() throws SQLException;
}
