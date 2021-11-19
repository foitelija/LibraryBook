package com.app.dao;



import com.app.model.Author;
import com.app.model.Role;
import com.app.model.User;


import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * реализация AuthorDao
 */
public class AuthorDao extends AbstractDao implements DAO<Author,Integer> {
    private static final String SQL_BY_ID = "select a.id,a.first_name,a.last_name ,a.is_visible,a.modified_login , u.first_name  as user_first_name, u.last_name  as user_last_name, u.role_name as user_role from authors a join users u on a.modified_login  =u.login where a.id=?;";
    private static final String SQL_SELECT = "select a.id,a.first_name,a.last_name ,a.is_visible,a.modified_login , u.first_name  as user_first_name, u.last_name  as user_last_name, u.role_name as user_role from authors a join users u on a.modified_login  =u.login";
    private static final String SQL_INS = "INSERT INTO authors (first_name, last_name,is_visible,modified_login) VALUES(?, ?,?,?);";
    private static final String SQL_UPD = "UPDATE authors SET first_name=?, last_name=?,is_visible=?,modified_login=? WHERE id=?;";
    private static final String SQL_DELETE="DELETE FROM authors WHERE id=?;";


    public AuthorDao(DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    @Override
    public Optional<Author> get(Integer id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst =connection.prepareStatement(SQL_BY_ID);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        Author author=null;
        while (rs.next()){
            author = new Author();
            author.setId(rs.getInt("id"));
            author.setFirstName(rs.getString("first_name"));
            author.setLastName(rs.getString("last_name"));
            author.setVisible(rs.getBoolean("is_visible"));
            User user = new User();
            user.setLogin("modified_login");
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role")));
            author.setModifiedBy(user);
        }
        rs.close();pst.close();
        return  Optional.ofNullable(author);

    }

    @Override
    public List<Author> getAll() throws SQLException {
        Connection connection = getConnection();
        Statement st =connection.createStatement();
        ResultSet rs = st.executeQuery(SQL_SELECT);
        List<Author> authors =new ArrayList<>();

        while (rs.next()){
            Author author = new Author();
            author.setId(rs.getInt("id"));
            author.setFirstName(rs.getString("first_name"));
            author.setLastName(rs.getString("last_name"));
            author.setVisible(rs.getBoolean("is_visible"));
            User user = new User();
            user.setLogin("modified_login");
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role")));
            author.setModifiedBy(user);
            authors.add(author);
        }
        rs.close();st.close();
        return authors;
    }

    @Override
    public Integer save(Author author) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_INS,Statement.RETURN_GENERATED_KEYS);
        pst.setString(1,author.getFirstName());
        pst.setString(2,author.getLastName());
        pst.setBoolean(3,author.isVisible());
        pst.setString(4,author.getModifiedBy().getLogin());
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
    public void update(Author author) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_UPD);
        pst.setString(1,author.getFirstName());
        pst.setString(2,author.getLastName());
        pst.setBoolean(3,author.isVisible());
        pst.setString(4,author.getModifiedBy().getLogin());
        pst.setInt(5,author.getId());
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
}
