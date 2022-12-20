package com.projectASA.services.impl;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;

public class CassandraService implements IDBFactory {

    private static final String DB_NODE = "127.0.0.1";
    private static final int DB_PORT = 9042;

    private static final String SQL_SELECT_ALL_COUNT = "SELECT count(*) FROM cassandra_keyspace.persons;";
    private static final String SQL_SELECT_ALL = "SELECT * FROM cassandra_keyspace.persons;";
    private static final String SQL_USE_KEYSPACE = "USE cassandra_keyspace;";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS cassandra_keyspace.persons("
            + "id int PRIMARY KEY, first_name text, last_name text, age int, city text);";

    private Cluster cluster;
    private Session session;

    public String query;

    @Override
    public Person create(Person person) {

        connect(DB_NODE, DB_PORT);
        createKeyspace("cassandra_keyspace", "SimpleStrategy", 1);
        createTable();
        insertPerson(person);

        close();

        return person;
    }

    @Override
    public Person[] read() {

        Person [] persons = {};

        connect(DB_NODE, DB_PORT);

        persons = new Person[getPersonsCount()];

        this.query = SQL_SELECT_ALL;

        ResultSet result = session.execute(query);

        Row row;

        for (int i = 0; i < getPersonsCount(); i++) {

            row = result.one();

            persons[i] = new Person();

            persons[i].setId(row.getInt("id"));
            persons[i].setFname(row.getString("first_name"));
            persons[i].setLname(row.getString("last_name"));
            persons[i].setAge(row.getInt("age"));
            persons[i].setCity(row.getString("city"));

        }

        close();

        return persons;
    }

    @Override
    public Person update(Person person) {

        connect(DB_NODE, DB_PORT);

        useKeyspace();

        this.query = "UPDATE persons SET first_name = '" + person.getFname()
                 + "', last_name = '" + person.getLname()
                + "', age = " + person.getAge()
                + ", city = '" + person.getCity()
                + "' WHERE id = " + person.getId() + ";";

        session.execute(query);

        close();

        return person;
    }

    @Override
    public boolean delete(int id) {

        connect(DB_NODE, DB_PORT);

        useKeyspace();

        this.query = "DELETE FROM persons WHERE id = " + id + ";";

        session.execute(query);

        close();

        return true;
    }

    public void connect(String node, Integer port) {

        Cluster.Builder b = Cluster.builder().addContactPoint(node);

        if (port != null) {

            b.withPort(port);

        }

        cluster = b.build();

        session = cluster.connect();

    }

    public void close() {

        session.close();
        cluster.close();

    }

    public void createKeyspace(String keyspaceName, String replicationStrategy, int replicationFactor) {

        this.query = "CREATE KEYSPACE IF NOT EXISTS " + keyspaceName + " WITH replication = { 'class':'"
                + replicationStrategy + "','replication_factor':" + replicationFactor + "};";

        session.execute(query);

    }

    public void createTable() {

        this.query = SQL_CREATE_TABLE;

        session.execute(query);
    }

    public void insertPerson(Person person) {

        //person.setId(getID());

        useKeyspace();

        query = "INSERT INTO persons(id, first_name, last_name, age, city) VALUES ("
               + person.getId() + ", '" + person.getFname() + "', '" + person.getLname() + "', " + person.getAge()
                + ", '" + person.getCity() + "');";

        session.execute(query);
    }

    public int getID() {

        int[] user_id = {0, 0};

        this.query = SQL_SELECT_ALL;

        ResultSet result =  session.execute(query);

        result.forEach(r -> {
                    user_id[0] = r.getInt("id");
                    if (user_id[0] > user_id[1]) {
                        user_id[1] = user_id[0];
                    }
        });

        user_id[1]++;

        return user_id[1];

    }

    public void useKeyspace() {

        this.query = SQL_USE_KEYSPACE;

        session.execute(query);

    }

    public int getPersonsCount() {

        this.query = SQL_SELECT_ALL_COUNT;

        ResultSet result = session.execute(query);

        long rowsQuantity = result.one().getLong(0);

        return (int)rowsQuantity;

    }

}
