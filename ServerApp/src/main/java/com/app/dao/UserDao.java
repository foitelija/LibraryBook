package com.app.dao;

import com.app.model.Role;
import com.app.model.User;



import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * реализаця User Dao
 */
public class UserDao extends AbstractDao implements DAO<User, String> {

    private static final String SQL_INS = "INSERT INTO users (login,password,first_name,last_name,role_name) values (?,?,?,?,?)";
    private static String SQL_BY_ID = "select * from users where login=?";

    private static String SQL_ALL = "select * from users";

    private static String SQL_UPDATE = "update users set first_name=?, last_name=?, role_name=?, password=? where login=?";

    private static String SQL_DELETE = "delete from users where login=?";


    public UserDao(DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    @Override
    public Optional<User> get(String login) throws SQLException {
        User user = null;
        Connection connection = getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_BY_ID);
        pst.setString(1,login);
        ResultSet resultSet = pst.executeQuery();
        while (resultSet.next()){
            user = new User();
            user.setLogin(resultSet.getString("login"));
            user.setPassword(resultSet.getString("password"));
            user.setFirstName(resultSet.getString("first_name"));
            user.setLastName(resultSet.getString("last_name"));
            user.setRole(Role.valueOf(resultSet.getString("role_name")));
        }
        resultSet.close();
        pst.close();
        if (user!=null) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public List<User> getAll() throws SQLException {

        List<User> users = new ArrayList<>();
        Connection connection = getConnection();
        Statement pst = connection.createStatement();
        ResultSet resultSet = pst.executeQuery(SQL_ALL);
        while (resultSet.next()){
           User  user = new User();
            user.setLogin(resultSet.getString("login"));
            user.setPassword(resultSet.getString("password"));
            user.setFirstName(resultSet.getString("first_name"));
            user.setLastName(resultSet.getString("last_name"));
            user.setRole(Role.valueOf(resultSet.getString("role_name")));
            users.add(user);
        }
        resultSet.close();
        pst.close();
        return users;
    }

    @Override
    public String save(User user) throws SQLException {
        Connection connection =  getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_INS);
        pst.setString(1,user.getLogin());
        pst.setString(2,user.getPassword());
        pst.setString(3,user.getFirstName());
        pst.setString(4,user.getLastName());
        pst.setString(5,user.getRole().name());
        int ins = pst.executeUpdate();
        pst.close();
        if (ins==0) return null;
        return user.getLogin();
    }

    @Override
    public void update(User user) throws SQLException {
        Connection connection =  getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_UPDATE);
        pst.setString(1,user.getFirstName());
        pst.setString(2,user.getLastName());
        pst.setString(3,user.getRole().name());
        pst.setString(4,user.getPassword());
        pst.setString(5,user.getLogin());
        pst.executeUpdate();
        pst.close();
    }

    @Override
    public void delete(String login) throws SQLException {
        Connection connection =  getConnection();
        PreparedStatement pst = connection.prepareStatement(SQL_DELETE);
        pst.setString(1,login);
        pst.executeUpdate();
        pst.close();

    }
}
