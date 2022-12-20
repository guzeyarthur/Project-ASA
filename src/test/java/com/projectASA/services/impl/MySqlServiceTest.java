package com.projectASA.services.impl;

import com.projectASA.models.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

public class MySqlServiceTest {
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "rarara29";
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "db_persons";
    private static final String DB_URL = "jdbc:mysql://"+DB_HOST+":"+DB_PORT+"/"+DB_NAME;

    private static final String SQL_INSERT = "INSERT INTO persons (id, first_name, last_name, age, city) VALUES (?, ?, ?, ?, ?);";
    private static final String SQL_SELECT_ALL_COUNT = "SELECT count(*) FROM db_persons.persons;";
    private static final String SQL_UPDATE = "UPDATE persons SET first_name = ?, last_name = ?, age = ?, city = ? WHERE id = ?;";
    private static final String SQL_DELETE = "DELETE FROM db_persons.persons WHERE id = ?;";

    private final MySqlService cut = new MySqlService();

    private final Connection connection = Mockito.mock(Connection.class);
    private final Statement statement = Mockito.mock(Statement.class);
    private final ResultSet resultQuantity = Mockito.mock(ResultSet.class);
    private final PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
    private final ResultSet result = Mockito.mock(ResultSet.class);

    static List<Arguments> createTestArgs() {
        return List.of(
                Arguments.arguments(0, "TestFName0", "TestLName0", 19, "TestCity0"),
                Arguments.arguments(1, "TestFName1", "TestLName1", 20, "TestCity1")
        );
    }
    @ParameterizedTest
    @MethodSource("createTestArgs")
    void createTest(int id, String fname, String lname, int age, String city) {
        Person testPerson = new Person();
        testPerson.setId(id);
        testPerson.setFname(fname);
        testPerson.setLname(lname);
        testPerson.setAge(age);
        testPerson.setCity(city);
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(SQL_INSERT)).thenReturn(preparedStatement);

            cut.create(testPerson);

            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(1, testPerson.getId());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, testPerson.getFname());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(3, testPerson.getLname());
            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(4, testPerson.getAge());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(5, testPerson.getCity());
            Mockito.verify(preparedStatement, Mockito.times(1)).executeUpdate();
            Mockito.verify(preparedStatement, Mockito.times(1)).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void  createExeptionTest() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(SQL_INSERT)).thenReturn(preparedStatement);
            Person testPerson = new Person();
            testPerson.setId(0);
            testPerson.setFname("fname");
            testPerson.setLname("lname");
            testPerson.setAge(20);
            testPerson.setCity("city");
            Exception e = new SQLException();
            Mockito.doThrow(e).when(preparedStatement).setInt(4, testPerson.getAge());
            Mockito.verify(e, Mockito.times(1)).printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    void readTest() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)).thenReturn(connection);
            Person testPerson1 = new Person();
            testPerson1.setId(0);
            testPerson1.setFname("fname");
            testPerson1.setLname("lname");
            testPerson1.setAge(20);
            testPerson1.setCity("city");
            Person testPerson2 = new Person();
            testPerson1.setId(1);
            testPerson1.setFname("fname1");
            testPerson1.setLname("lname1");
            testPerson1.setAge(21);
            testPerson1.setCity("city1");
            Person[] persons = new Person[2];
            persons[0] = testPerson1;
            persons[1] = testPerson2;
            Mockito.when(connection.createStatement()).thenReturn(statement);
            Mockito.when(statement.executeQuery(SQL_SELECT_ALL_COUNT)).thenReturn(resultQuantity);
            Mockito.when(resultQuantity.next()).thenReturn(true);
            Mockito.when(result.next()).thenReturn(true, true, false);
            Mockito.when(result.getInt("id")).thenReturn(persons[0].getId(), persons[1].getId());
            Mockito.when(result.getString("first_name")).thenReturn(persons[0].getFname(), persons[1].getFname());
            Mockito.when(result.getString("last_name")).thenReturn(persons[0].getLname(), persons[1].getLname());
            Mockito.when(result.getInt("age")).thenReturn(persons[0].getAge(), persons[1].getAge());
            Mockito.when(result.getString("city")).thenReturn(persons[0].getCity(), persons[1].getCity());
            Person[] resultPersons = cut.read();
            Assertions.assertArrayEquals(resultPersons, persons);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    void updateTest() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(SQL_UPDATE)).thenReturn(preparedStatement);
            Person testPerson = new Person();
            testPerson.setId(0);
            testPerson.setFname("fname");
            testPerson.setLname("lname");
            testPerson.setAge(20);
            testPerson.setCity("city");
            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(1, testPerson.getId());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, testPerson.getFname());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(3, testPerson.getLname());
            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(4, testPerson.getAge());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(5, testPerson.getCity());
            Mockito.verify(preparedStatement, Mockito.times(1)).executeUpdate();
            Mockito.verify(preparedStatement, Mockito.times(1)).close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTest() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(SQL_DELETE)).thenReturn(preparedStatement);
            int id = 2;
            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(1, id);
            Mockito.verify(preparedStatement, Mockito.times(1)).executeUpdate();
            Mockito.verify(preparedStatement, Mockito.times(1)).close();

            boolean deleteResult = cut.delete(id);
            Assertions.assertEquals(deleteResult, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteExeptionTest() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(SQL_DELETE)).thenReturn(preparedStatement);
            int id = 2;

            Exception e = new SQLException();
            Mockito.doThrow(e).when(preparedStatement).setInt(1, id);
            Mockito.verify(e, Mockito.times(1)).printStackTrace();


            boolean deleteResult = cut.delete(id);
            Assertions.assertEquals(deleteResult, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
