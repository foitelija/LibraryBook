package com.app.dao;





import com.app.model.Genre;
import com.app.model.Role;
import com.app.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * реализация Genre Dao
 */
public class GenreDao extends AbstractDao implements DAO<Genre,Integer> {
    private static final String
    SQL_SELECT ="select g.id, g.name, g.is_visible, g.modified_login," +
            " u.first_name as user_first_name, u.last_name as user_last_name, u.role_name as user_role_name from genres g  join users u on g.modified_login = u.login";

    private static final String
    SQL_INS= "INSERT INTO genres ( name, is_visible, modified_login) VALUES(?, ?,?);";
    private static final String
    SQL_UPD = "UPDATE genres SET name=?, is_visible=?, modified_login=? WHERE id=?;";
    private static final String
    SQL_DEL = "DELETE FROM genres WHERE id=?;";
    private static final String
    SQL_BY_ID ="select g.id, g.name, g.is_visible, g.modified_login," +
            " u.first_name as user_first_name, u.last_name as user_last_name, u.role_name as user_role_name from genres g  join users u on g.modified_login = u.login where g.id=?;";



    public GenreDao(DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    @Override
    public Optional<Genre> get(Integer id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst =connection.prepareStatement(SQL_BY_ID);
        pst.setInt(1,id);
        ResultSet rs = pst.executeQuery();
        Genre genre=null;
        while (rs.next()){
            genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            genre.setVisible(rs.getBoolean("is_visible"));
            User user = new User();
            user.setLogin(rs.getString("modified_login"));
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role_name")));
            genre.setModifiedBy(user);
        }
        rs.close();pst.close();
        return  Optional.ofNullable(genre);

    }

    @Override
    public List<Genre> getAll() throws SQLException {
        Connection connection = getConnection();
        Statement st =connection.createStatement();
        ResultSet rs = st.executeQuery(SQL_SELECT);
        List<Genre> genres =new ArrayList<>();

        while (rs.next()){
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            genre.setVisible(rs.getBoolean("is_visible"));
            User user = new User();
            user.setLogin(rs.getString("modified_login"));
            user.setFirstName(rs.getString("user_first_name"));
            user.setLastName(rs.getString("user_last_name"));
            user.setRole(Role.valueOf(rs.getString("user_role_name")));
            genre.setModifiedBy(user);
            genres.add(genre);
        }
        rs.close();st.close();
        return genres;
    }

    @Override
    public Integer save(Genre genre) throws SQLException {

        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_INS,Statement.RETURN_GENERATED_KEYS);
        pst.setString(1,genre.getName());
        pst.setBoolean(2,genre.isVisible());
        pst.setString(3,genre.getModifiedBy().getLogin());
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
    public void update(Genre genre) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_UPD);
        pst.setString(1,genre.getName());
        pst.setBoolean(2,genre.isVisible());
        pst.setString(3,genre.getModifiedBy().getLogin());
        pst.setInt(4,genre.getId());
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
}
