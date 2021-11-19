package com.app.dao;

import java.sql.SQLException;

/**
 * реализаця фабрики Dao
 */
public class DAOFactory extends AbstractDAOFactory {

    @Override
    public UserDao getUserDao() throws SQLException {
        return new UserDao(dataSource);
    }

    @Override
    public GenreDao getGenreDao() throws SQLException {
        return new GenreDao(dataSource);
    }

    @Override
    public BookDao getBookDao() throws SQLException {
        return new BookDao(dataSource);
    }

    @Override
    public AuthorDao getAuthorDao() throws SQLException {
        return new AuthorDao(dataSource);
    }

    @Override
    public ArrivalDao getArrivalDao() throws SQLException {
        return new ArrivalDao(dataSource);
    }
}
