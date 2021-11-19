package com.app.dao;

import com.app.model.*;



import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * реализация ArrivalDao
 */
public class ArrivalDao extends AbstractDao implements DAO<Arrival,Integer>  {

    private static  final String
    SQL_SELECT = "SELECT a.id, a.amount, a.date_add, a.price,a.pages,a.book_publisher, a.book_id, a.modified_login, year_of, is_visible,\n" +
            "b.author_id, b.genre_id, b.name  as book_name, b.is_visible  as book_visible,\n" +
            "au.first_name  as author_first_name, au.last_name  as author_last_name, \n" +
            "g.name as genre_name, \n" +
            "u.first_name as user_first_name, u.last_name  as user_last_name, u.role_name as user_role \n" +
            "FROM arrival a join books b on a.book_id  = b.id\n" +
            "join authors au on au.id  = b.author_id \n" +
            "join  genres g on b.genre_id  = g.id \n" +
            "join  users u  on u.login  = a.modified_login ";

    private static  final String
    SQL_BY_ID = "SELECT a.id, a.amount, a.date_add, a.price,a.pages,a.book_publisher, a.book_id, a.modified_login, year_of, a.is_visible,\n" +
            "b.author_id, b.genre_id, b.name  as book_name, b.is_visible  as book_visible,\n" +
            "au.first_name  as author_first_name, au.last_name  as author_last_name, \n" +
            "g.name as genre_name," +
            "u.first_name as user_first_name, u.last_name  as user_last_name, u.role_name as user_role \n" +
            "FROM arrival a join books b on a.book_id  = b.id " +
            "join authors au on au.id  = b.author_id \n" +
            "join  genres g on b.genre_id  = g.id \n" +
            "join  users u  on u.login  = a.modified_login \n" +
            "where a.id=? ";
    private static  final String
    SQL_BY_PERIOD = "SELECT a.id,a.amount, a.date_add, a.price, a.pages,a.book_publisher, a.book_id, a.modified_login, a.year_of, a.is_visible,\n" +
            "b.author_id, b.genre_id, b.name  as book_name, " +
            "au.first_name  as author_first_name, au.last_name  as author_last_name, " +
            "g.name as genre_name," +
            "u.first_name as user_first_name, u.last_name  as user_last_name, " +
            "u.role_name as user_role " +
            "FROM arrival a join books b on a.book_id  = b.id " +
            "join authors au on au.id  = b.author_id " +
            "join  genres g on b.genre_id  = g.id " +
            "join  users u  on u.login  = a.modified_login " +
            "where a.date_add between ? and ?";

    private static final String
    SQL_INS ="INSERT INTO arrival (amount, date_add, price, book_id, modified_login, year_of, is_visible,pages,book_publisher) VALUES(?, ?,?,?, ?, ?, ?,?,?);";

    private static final String
    SQL_UPD = "UPDATE arrival SET amount=?, date_add=?, price=?, book_id=?, modified_login=?, year_of=?, is_visible=?,pages=?, book_publisher=? WHERE id=?;";
    private static final String
            SQL_DEL="DELETE FROM arrival WHERE id=?";

    private static String SQL_REPORT = "select a.year_of, sum(a.amount) from arrival a "+
                                "group by a.year_of order by sum(a.amount) desc";


    public ArrivalDao(DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    @Override
    public Optional<Arrival> get(Integer id) throws SQLException {

        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_BY_ID);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        Arrival arrival= null;
        while (rs.next()){
            arrival = new Arrival();
            arrival.setId(rs.getInt("id"));
            arrival.setAmount(rs.getInt("amount"));
            arrival.setArrivalDate(rs.getDate("date_add").toLocalDate());
            arrival.setPrice(rs.getInt("price"));
            arrival.setYearOf(rs.getInt("year_of"));
            arrival.setVisible(rs.getBoolean("is_visible"));
            arrival.setPages((rs.getInt("pages")));
            arrival.setBookPublisher(rs.getString("book_publisher"));
            Genre genre =new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            Author author =new Author();
            author.setId(rs.getInt("author_id"));
            author.setFirstName(rs.getString("author_first_name"));
            author.setLastName(rs.getString("author_last_name"));
            Book book =new Book();
            book.setId(rs.getInt("book_id"));
            book.setName(rs.getString("book_name"));
            book.setGenre(genre);
            book.setAuthor(author);
            arrival.setBook(book);
            User user = new User();
            user.setLogin(rs.getString("modified_login"));
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role")));
            arrival.setModifiedBy(user);
        }
        rs.close();pst.close(); 
        return Optional.ofNullable(arrival);
    }

    @Override
    public List<Arrival> getAll() throws SQLException {
        List<Arrival> arrivals = new ArrayList<>();
        Connection connection = getConnection();
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(SQL_SELECT);
        while (rs.next()){
            Arrival arrival = new Arrival();
            arrival.setId(rs.getInt("id"));
            arrival.setAmount(rs.getInt("amount"));
            arrival.setArrivalDate(rs.getDate("date_add").toLocalDate());
            arrival.setPrice(rs.getInt("price"));
            arrival.setYearOf(rs.getInt("year_of"));
            arrival.setVisible(rs.getBoolean("is_visible"));
            arrival.setPages((rs.getInt("pages")));
            arrival.setBookPublisher(rs.getString("book_publisher"));
            Genre genre =new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            Author author =new Author();
            author.setId(rs.getInt("author_id"));
            author.setFirstName(rs.getString("author_first_name"));
            author.setLastName(rs.getString("author_last_name"));
            Book book =new Book();
            book.setId(rs.getInt("book_id"));
            book.setName(rs.getString("book_name"));
            book.setGenre(genre);
            book.setAuthor(author);
            arrival.setBook(book);
            User user = new User();
            user.setLogin(rs.getString("modified_login"));
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role")));
            arrival.setModifiedBy(user);
            arrivals.add(arrival);
        }
        rs.close();st.close();
        return arrivals;
    }


    public List<Arrival> getByPeriod(LocalDate begin, LocalDate end) throws SQLException {

        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_BY_PERIOD);
        pst.setDate(1,java.sql.Date.valueOf( begin));
        pst.setDate(2,java.sql.Date.valueOf( end));
        ResultSet rs = pst.executeQuery();
        List<Arrival> arrivals = new ArrayList<>();

        while (rs.next()){
            Arrival arrival=  new Arrival();
            arrival.setId(rs.getInt("id"));
            arrival.setAmount(rs.getInt("amount"));
            arrival.setArrivalDate(rs.getDate("date_add").toLocalDate());
            arrival.setPrice(rs.getInt("price"));
            arrival.setYearOf(rs.getInt("year_of"));
            arrival.setVisible(rs.getBoolean("is_visible"));
            arrival.setPages((rs.getInt("pages")));
            arrival.setBookPublisher(rs.getString("book_publisher"));
            Genre genre =new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            Author author =new Author();
            author.setId(rs.getInt("author_id"));
            author.setFirstName(rs.getString("author_first_name"));
            author.setLastName(rs.getString("author_last_name"));
            Book book =new Book();
            book.setId(rs.getInt("book_id"));
            book.setName(rs.getString("book_name"));
            book.setGenre(genre);
            book.setAuthor(author);
            arrival.setBook(book);
            User user = new User();
            user.setLogin(rs.getString("modified_login"));
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role")));
            arrival.setModifiedBy(user);
            arrivals.add(arrival);
        }
        rs.close();pst.close(); 
        return arrivals;
    }

    @Override
    public Integer save(Arrival arrival) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_INS,Statement.RETURN_GENERATED_KEYS);
        pst.setInt(1,arrival.getAmount());
        pst.setDate(2,java.sql.Date.valueOf(arrival.getArrivalDate()));
        pst.setInt(3,arrival.getPrice());
        pst.setInt(4,arrival.getBook().getId());
        pst.setString(5,arrival.getModifiedBy().getLogin());
        pst.setInt(6,arrival.getYearOf());
        pst.setBoolean(7,arrival.isVisible());
        pst.setInt(8,arrival.getPages());
        pst.setString(9,arrival.getBookPublisher());
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
    public void update(Arrival arrival) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_UPD);
        pst.setInt(1,arrival.getAmount());
        pst.setDate(2,java.sql.Date.valueOf(arrival.getArrivalDate()));
        pst.setInt(3,arrival.getPrice());
        pst.setInt(4,arrival.getBook().getId());
        pst.setString(5,arrival.getModifiedBy().getLogin());
        pst.setInt(6,arrival.getYearOf());
        pst.setBoolean(7,arrival.isVisible());
        pst.setInt(8,arrival.getPages());
        pst.setString(9,arrival.getBookPublisher());
        pst.setInt(10,arrival.getId());
        pst.executeUpdate();
        pst.close();
    }

    @Override
    public void delete(Integer id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_DEL);
        pst.setInt(1,id);
        pst.executeUpdate();
        pst.close();
    }

    /**
     * возвращает кол-во поступивших книг по годам
     * @return список год-кол-во
     * @throws SQLException
     */
    public List<YearReport> yearBookCount() throws SQLException {
        List<YearReport> list =new ArrayList<>();
        Connection connection = getConnection();
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(SQL_REPORT);
        while (rs.next()){
            YearReport report = new YearReport();
            report.setYearOf(rs.getInt(1));
            report.setBookCount(rs.getInt(2));
            list.add(report);
        }
        rs.close();st.close();
        return list;
    }
}
