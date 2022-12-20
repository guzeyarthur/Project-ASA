package com.projectASA.services.impl;

import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;
import java.sql.*;

public class PostgreSqlService implements IDBFactory {

    private static final String DB_USERNAME = "serjkovaliov";
    private static final String DB_PASSWORD = "rarara29";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/persons";

    private static final String SQL_SELECT_ALL = "SELECT * FROM persons;";
    private static final String SQL_SELECT_ALL_COUNT = "SELECT count(*) FROM persons;";
    private static final String SQL_SELECT_ALL_BY_ID = "SELECT * FROM persons ORDER BY id;";
    private static final String SQL_INSERT = "INSERT INTO persons (id, first_name, last_name, age, city) VALUES (?, ?, ?, ?, ?);";
    private static final String SQL_UPDATE = "UPDATE persons SET first_name = ?, last_name = ?, age = ?, city = ? WHERE id = ?;";
    private static final String SQL_DELETE = "DELETE FROM persons WHERE id = ?;";

    String query;

    @Override
    public Person create(Person person) {

        try (Connection connection = getConnect()) {

            this.query = SQL_INSERT;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, person.getId());
            preparedStatement.setString(2, person.getFname());
            preparedStatement.setString(3, person.getLname());
            preparedStatement.setInt(4, person.getAge());
            preparedStatement.setString(5, person.getCity());

            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

        return person;
    }

    @Override
    public Person[] read() {

        Person [] persons = {};

        try (Connection connection = getConnect(); Statement statement = connection.createStatement()) {

            persons = new Person[getPersonsCount()];

            ResultSet result = statement.executeQuery(SQL_SELECT_ALL);

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

                result.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

        return persons;
    }

    @Override
    public Person update(Person person) {

        try (Connection connection = getConnect()) {

            this.query = SQL_UPDATE;

             PreparedStatement preparedStatement = connection.prepareStatement(query);

             preparedStatement.setString(1, person.getFname());
             preparedStatement.setString(2, person.getLname());
             preparedStatement.setInt(3, person.getAge());
             preparedStatement.setString(4, person.getCity());
             preparedStatement.setInt(5, person.getId());

             preparedStatement.executeUpdate();

             preparedStatement.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

        return person;
    }

    @Override
    public boolean delete(int id) {

        try (Connection connection = getConnect()){

            this.query = SQL_DELETE;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            preparedStatement.close();

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

        return true;

    }

    private Connection getConnect() {

        try {

            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

        } catch (SQLException e) {

            e.printStackTrace();
            return null;

        }

    }

    public int generateID() {

        this.query = SQL_SELECT_ALL_BY_ID;

        int user_id = 0;

        try (Connection connection = getConnect(); Statement statement = connection.createStatement()) {

            ResultSet result = statement.executeQuery(query);

            while(result.next()) {

                user_id = result.getInt("id");

            }

            user_id++;

        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return user_id;

    }

    public int getPersonsCount() {

        int rowsQuantity = 0;

        try (Connection connection = getConnect(); Statement statement = connection.createStatement()) {

            ResultSet resultQuantity = statement.executeQuery(SQL_SELECT_ALL_COUNT);
            resultQuantity.next();
            rowsQuantity = resultQuantity.getInt(1);

        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return rowsQuantity;

    }

}
