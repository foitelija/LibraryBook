package com.app.dao;

import com.app.model.*;


import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDao extends AbstractDao implements DAO<Book,Integer> {

    private static String SQL_BY_ID = "select b.id, b.name,  b.genre_id, b.author_id,g.name as genre_name, a.first_name as author_first_name,\n" +
            "a.last_name  as author_last_name, u.first_name  as user_first_name, u.last_name  as user_last_name, u.role_name as user_role\n" +
            "from books b join genres g on g.id = b.genre_id join authors a on a.id  = b.author_id join users u on u.login = b.modified_login where b.id = ?";

    private static String SQL_ALL = "select b.id, b.name,b.is_visible,  b.genre_id, b.author_id, g.name as genre_name, a.first_name as author_first_name,\n" +
            "a.last_name  as author_last_name, u.first_name  as user_first_name, u.last_name  as user_last_name, u.role_name as user_role\n" +
            "from books b join genres g on g.id = b.genre_id join authors a on a.id  = b.author_id join users u on u.login = b.modified_login";

    private static String SQL_UPDATE = "UPDATE books SET name=?,  is_visible=?, author_id=?, genre_id=?, modified_login=? WHERE id=?;\n";

    private static String SQL_DELETE = "delete from books where id=?";

    private static String SQL_INS = "INSERT INTO books ( name,  is_visible, author_id, genre_id,modified_login) VALUES(?, ?, ?, ?,?);";

    private static String SQL_BY_AUTHOR  ="select b.id, b.name, b.is_visible, b.genre_id,b.author_id, g.name as genre_name, a.first_name as author_first_name,\n" +
            "a.last_name  as author_last_name, u.first_name  as user_first_name, u.last_name  as user_last_name, u.role_name as user_role\n" +
            "from books b join genres g on g.id = b.genre_id join authors a on a.id  = b.author_id join users u on u.login = b.modified_login where b.author_id  = ?";


    private static String SQL_GENRE_COUNT = "select g.name, count(b.id) from books b join genres g on b.genre_id  = g.id\n" +
            "group by g.name order by count(b.id) desc;";

    public BookDao(DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    @Override
    public Optional<Book> get(Integer id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_BY_ID);
        pst.setInt(1,id);
        ResultSet  rs = pst.executeQuery();
        Book book = null;
        while (rs.next()){
             book = new Book();
             book.setId(rs.getInt("id"));
             book.setName(rs.getString("name"));
             Author author = new Author();
             author.setId(rs.getInt("author_id"));
             author.setFirstName(rs.getString("author_first_name"));
             author.setLastName(rs.getString("author_last_name"));
             book.setAuthor(author);
             Genre genre =new Genre();
             genre.setId(rs.getInt("genre_id"));
             genre.setName(rs.getString("genre_name"));
             book.setGenre(genre);
             User user = new User();
             user.setLogin("modified_login");
             user.setFirstName(rs.getString("user_first_name"));
             user.setLastName(rs.getString("user_last_name"));
             user.setRole(Role.valueOf(rs.getString("user_role")));
             book.setModifiedBy(user);

        }
        rs.close();pst.close();
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> getAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        Connection connection = getConnection();
        Statement st = connection.createStatement();
        ResultSet  rs = st.executeQuery(SQL_ALL);

        while (rs.next()){
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setName(rs.getString("name"));
            Author author = new Author();
            author.setId(rs.getInt("author_id"));
            author.setFirstName(rs.getString("author_first_name"));
            author.setLastName(rs.getString("author_last_name"));
            book.setAuthor(author);
            Genre genre =new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            book.setGenre(genre);
            book.setVisible(rs.getBoolean("is_visible"));
            User user = new User();
            user.setLogin("modified_login");
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role")));
            book.setModifiedBy(user);
            books.add(book);
        }
        rs.close();st.close();
        return books;
    }


    public List<Book> getByAuthor(Author a) throws SQLException {
        List<Book> books = new ArrayList<>();
        Connection connection = getConnection();
        PreparedStatement  pst = connection.prepareStatement(SQL_BY_AUTHOR);
        pst.setInt(1,a.getId());
        ResultSet  rs = pst.executeQuery();

        while (rs.next()){
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setName(rs.getString("name"));
            Author author = new Author();
            author.setId(rs.getInt("author_id"));
            author.setFirstName(rs.getString("author_first_name"));
            author.setLastName(rs.getString("author_last_name"));
            book.setAuthor(author);
            Genre genre =new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            book.setGenre(genre);
            book.setVisible(rs.getBoolean("is_visible"));
            User user = new User();
            user.setLogin("modified_login");
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role")));
            book.setModifiedBy(user);
            books.add(book);
        }
        rs.close();pst.close();
        return books;
    }

    @Override
    public Integer save(Book book) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_INS,Statement.RETURN_GENERATED_KEYS);
        pst.setString(1,book.getName());
        pst.setBoolean(2,book.isVisible());
        pst.setInt(3,book.getAuthor().getId());
        pst.setInt(4,book.getGenre().getId());
        pst.setString(5,book.getModifiedBy().getLogin());
        int affectedRows =  pst.executeUpdate();
        int id = 0;
        if (affectedRows>0){
            ResultSet keys = pst.getGeneratedKeys();
            while (keys.next())
            id =keys.getInt(1);
            keys.close();
        }
        pst.close(); 
        return id;
    }

    @Override
    public void update(Book book) throws SQLException {
         Connection connection = getConnection();
         PreparedStatement pst = connection.prepareStatement(SQL_UPDATE);
         pst.setString(1,book.getName());
         pst.setBoolean(2,book.isVisible());
         pst.setInt(3,book.getAuthor().getId());
         pst.setInt(4,book.getGenre().getId());
         pst.setString(5,book.getModifiedBy().getLogin());
         pst.setInt(6,book.getId());
         pst.executeUpdate();
         pst.close();
    }

    @Override
    public void delete(Integer id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_DELETE);
        pst.setInt(1,id);
        pst.executeUpdate();
        pst.close();

    }

    /**
     * возвращает кол-во книг по жанрам
     * @return список
     * @throws SQLException
     */
    public List<GenreReport> genreCount() throws SQLException {
        List<GenreReport> list =new ArrayList<>();
        Connection connection = getConnection();
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(SQL_GENRE_COUNT);
        while (rs.next()){
            GenreReport report = new GenreReport();
            report.setName(rs.getString(1));
            report.setCount(rs.getInt(2));
            list.add(report);
        }
        rs.close();st.close();
        return list;
    }
}
