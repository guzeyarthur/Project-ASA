package com.projectASA.services.impl;

import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class MySqlService implements IDBFactory {
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "rarara29";
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "db_persons";

    private static final String SQL_SELECT_ALL = "SELECT * FROM db_persons.persons;";
    private static final String SQL_SELECT_ALL_COUNT = "SELECT count(*) FROM db_persons.persons;";
    private static final String SQL_UPDATE = "UPDATE persons SET first_name = ?, last_name = ?, age = ?, city = ? WHERE id = ?;";
    private static final String SQL_DELETE = "DELETE FROM db_persons.persons WHERE id = ?;";
    private static final String SQL_INSERT = "INSERT INTO persons (id, first_name, last_name, age, city) VALUES (?, ?, ?, ?, ?);";

    private Connection dbCon = null;

    String query;

    @Override
    public Person create(Person person) {

        try {

            this.query = SQL_INSERT;

            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);

            preparedStatement.setInt(1, person.getId());
            preparedStatement.setString(2, person.getFname());
            preparedStatement.setString(3, person.getLname());
            preparedStatement.setInt(4, person.getAge());
            preparedStatement.setString(5, person.getCity());

            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return person;

    }

    @Override
    public Person[] read() {

        Person [] persons = {};
        int rowsQuantity = 0;
        ResultSet resultQuantity = null, result = null;

        try (Statement statement = getDBConnection().createStatement()) {

            resultQuantity = statement.executeQuery(SQL_SELECT_ALL_COUNT);
            resultQuantity.next();
            rowsQuantity = resultQuantity.getInt(1);

            persons = new Person[rowsQuantity];
            result = statement.executeQuery(SQL_SELECT_ALL);

            int i = 0;

            while (result.next()) {

                persons[i] = new Person();
                persons[i].setId(result.getInt("id"));
                persons[i].setFname(result.getString("first_name"));
                persons[i].setLname(result.getString("last_name"));
                persons[i].setAge(result.getInt("age"));
                persons[i].setCity(result.getString("city"));

                i++;

            }

            resultQuantity.close();
            result.close();

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return persons;

    }

    @Override
    public Person update(Person person) {

        try {

            this.query = SQL_UPDATE;
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, person.getFname());
            preparedStatement.setString(2, person.getLname());
            preparedStatement.setInt(3, person.getAge());
            preparedStatement.setString(4, person.getCity());
            preparedStatement.setInt(5, person.getId());

            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return person;

    }

    @Override
    public boolean delete(int id) {
        try {

            this.query = SQL_DELETE;
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }

        return true;

    }

    private Connection getDBConnection() {

        String connection = "jdbc:mysql://"+DB_HOST+":"+DB_PORT+"/"+DB_NAME;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            dbCon = DriverManager.getConnection(connection, DB_USERNAME, DB_PASSWORD);

        } catch (ClassNotFoundException e) {

            e.printStackTrace();

        } catch (SQLException throwables) {

            throwables.printStackTrace();

        }

        return dbCon;

    }
}
