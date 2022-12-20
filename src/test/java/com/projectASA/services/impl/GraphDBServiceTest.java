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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GraphDBServiceTest {

    private static final String CQL_CREATE = "CREATE (person:Person {id: ?, first_name: ?, last_name: ?, age: ?," +
            " city: ?}) RETURN person";
    private static final String CQL_UPDATE = "MATCH (person:Person) WHERE person.id=? SET person.first_name=?" +
            " SET person.last_name=? SET person.age=? SET person.city=? RETURN person";
    private static final String CQL_DELETE = "MATCH (person:Person) WHERE person.id=? DELETE person";
    private static final String CQL_PERSONS_COUNT = "MATCH (person:Person) RETURN person";

    private static final String DB_URL = "jdbc:neo4j:bolt://localhost:7687";
    private static final String DB_USER = "neo4j";
    private static final String DB_PASSWORD = "rarara29";

    private final Connection connection = Mockito.mock(Connection.class);
    private final PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
    private final Statement statement = Mockito.mock(Statement.class);
    private final ResultSet resultSet = Mockito.mock(ResultSet.class);
    private final ResultSet resultQuantity = Mockito.mock(ResultSet.class);

    PostgreSqlService cut = new PostgreSqlService();

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
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(CQL_CREATE)).thenReturn(preparedStatement);

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
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(CQL_CREATE)).thenReturn(preparedStatement);
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
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
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
            Mockito.when(statement.executeQuery(CQL_PERSONS_COUNT)).thenReturn(resultQuantity);
            Mockito.when(resultQuantity.next()).thenReturn(true);
            Mockito.when(resultSet.next()).thenReturn(true, true, false);
            Mockito.when(resultSet.getInt("id")).thenReturn(persons[0].getId(), persons[1].getId());
            Mockito.when(resultSet.getString("first_name")).thenReturn(persons[0].getFname(), persons[1].getFname());
            Mockito.when(resultSet.getString("last_name")).thenReturn(persons[0].getLname(), persons[1].getLname());
            Mockito.when(resultSet.getInt("age")).thenReturn(persons[0].getAge(), persons[1].getAge());
            Mockito.when(resultSet.getString("city")).thenReturn(persons[0].getCity(), persons[1].getCity());
            Person[] resultPersons = cut.read();
            Assertions.assertArrayEquals(resultPersons, persons);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static List<Arguments> updateTestArgs() {
        return List.of(
                Arguments.arguments(0, "TestFName0", "TestLName0", 19, "TestCity0"),
                Arguments.arguments(1, "TestFName1", "TestLName1", 20, "TestCity1")
        );
    }

    @ParameterizedTest
    @MethodSource("updateTestArgs")
    void updateTest(int id, String fname, String lname, int age, String city) {
        Person testPerson = new Person();
        testPerson.setId(id);
        testPerson.setFname(fname);
        testPerson.setLname(lname);
        testPerson.setAge(age);
        testPerson.setCity(city);
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(CQL_UPDATE)).thenReturn(preparedStatement);

            cut.update(testPerson);

            Mockito.verify(preparedStatement, Mockito.times(1)).setString(1, testPerson.getFname());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(2, testPerson.getLname());
            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(3, testPerson.getAge());
            Mockito.verify(preparedStatement, Mockito.times(1)).setString(4, testPerson.getCity());
            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(5, testPerson.getId());
            Mockito.verify(preparedStatement, Mockito.times(1)).executeUpdate();
            Mockito.verify(preparedStatement, Mockito.times(1)).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateNullPerson() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(CQL_UPDATE)).thenReturn(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertDoesNotThrow(() -> cut.update(null));
        Mockito.verifyNoInteractions(preparedStatement);
    }

    @Test
    void deleteTest() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(CQL_DELETE)).thenReturn(preparedStatement);

            Person person = new Person();

            person.setId(1);
            person.setFname("TestFName");
            person.setLname("TestLName");
            person.setAge(22);
            person.setCity("TestCity");

            cut.delete(person.getId());

            Mockito.verify(preparedStatement, Mockito.times(1)).setInt(1, person.getId());
            Mockito.verify(preparedStatement, Mockito.times(1)).executeUpdate();
            Mockito.verify(preparedStatement, Mockito.times(1)).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteNullPerson() {
        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(connection.prepareStatement(CQL_DELETE)).thenReturn(preparedStatement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertDoesNotThrow(() -> cut.delete(0));
        Mockito.verifyNoInteractions(preparedStatement);
    }

    @Test
    void getPersonsCountTest() {

        try (MockedStatic<DriverManager> mockDriveManager = Mockito.mockStatic(DriverManager.class)) {
            mockDriveManager.when(() -> DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)).thenReturn(connection);
            Mockito.when(statement.executeQuery(CQL_PERSONS_COUNT)).thenReturn(resultSet);
            Mockito.when(resultSet.next()).thenReturn(true);

            cut.getPersonsCount();

            Mockito.verify(resultSet, Mockito.times(1)).getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
