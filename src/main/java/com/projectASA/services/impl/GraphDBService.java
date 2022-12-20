package com.projectASA.services.impl;

import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;

import java.sql.*;

public class GraphDBService implements IDBFactory {

    private static final String CQL_CREATE = "CREATE (person:Person {id: ?, first_name: ?, last_name: ?, age: ?," +
            " city: ?}) RETURN person";
    private static final String CQL_READ_ALL = "MATCH (person:Person) RETURN person.id, person.first_name," +
            " person.last_name, person.age, person.city";
    private static final String CQL_UPDATE = "MATCH (person:Person) WHERE person.id=? SET person.first_name=?" +
            " SET person.last_name=? SET person.age=? SET person.city=? RETURN person";
    private static final String CQL_DELETE = "MATCH (person:Person) WHERE person.id=? DELETE person";
    private static final String CQL_PERSONS_COUNT = "MATCH (person:Person) RETURN person";
    private static final String CQL_PERSONS_ID = "MATCH (person:Person) RETURN person.id";

    private static final String DB_URL = "jdbc:neo4j:bolt://localhost:7687";
    private static final String DB_USER = "neo4j";
    private static final String DB_PASSWORD = "rarara29";

    String query;

    @Override
    public Person create(Person person) {

        try (Connection connection = getConnect()) {

            this.query = CQL_CREATE;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, person.getId());
            preparedStatement.setString(2, person.getFname());
            preparedStatement.setString(3, person.getLname());
            preparedStatement.setInt(4, person.getAge());
            preparedStatement.setString(5, person.getCity());

            preparedStatement.executeUpdate();

            preparedStatement.close();


        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return person;

    }

    @Override
    public Person[] read() {

        Person [] persons = {};
        int i = 0;

        try (Connection connection = getConnect(); Statement statement = connection.createStatement();) {

            persons = new Person[getPersonsCount()];

            this.query = CQL_READ_ALL;

            ResultSet result = statement.executeQuery(query);

            while(result.next()) {

                persons[i] = new Person();
                persons[i].setId(result.getInt("person.id"));
                persons[i].setFname(result.getString("person.first_name"));
                persons[i].setLname(result.getString("person.last_name"));
                persons[i].setAge(result.getInt("person.age"));
                persons[i].setCity(result.getString("person.city"));

                i++;

            }

        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return persons;
    }

    @Override
    public Person update(Person person) {

        try (Connection connection = getConnect()) {

            this.query = CQL_UPDATE;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, person.getId());
            preparedStatement.setString(2, person.getFname());
            preparedStatement.setString(3, person.getLname());
            preparedStatement.setInt(4, person.getAge());
            preparedStatement.setString(5, person.getCity());

            preparedStatement.executeUpdate();

            preparedStatement.close();


        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return person;
    }

    @Override
    public boolean delete(int id) {

        try (Connection connection = getConnect()) {

            this.query = CQL_DELETE;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

            preparedStatement.close();


        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return true;

    }

    public Connection getConnect() {

        Connection connection = null;

        try {

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return connection;

    }

    public int generateID() {

        int user_id = 0, user_temp_id = 0;

        try (Connection connection = getConnect()) {

            this.query = CQL_PERSONS_ID;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {
                user_temp_id = result.getInt("person.id");

                if (user_id < user_temp_id) {
                    user_id = user_temp_id;
                }
            }

            user_id++;


        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return user_id;
    }

    public int getPersonsCount() {

        int count = 0;

        try (Connection connection = getConnect()) {

            this.query = CQL_PERSONS_COUNT;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet result = preparedStatement.executeQuery();

            while(result.next()) {

                count++;

            }

        } catch (SQLException ex) {

            ex.printStackTrace();

        }

        return count;

    }

}
