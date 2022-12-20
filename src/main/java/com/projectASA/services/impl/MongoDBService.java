package com.projectASA.services.impl;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;

import org.bson.Document;

import java.util.Map;

public class MongoDBService implements IDBFactory {

    private static final String URL = "mongodb+srv://SergeiKovaliov:rarara29@cluster0.kpbfp.mongodb.net/test";

    private final String HOST = "localhost";
    private final int PORT = 27017;

//    MongoClient mongoClient;
//    MongoDatabase database;
    MongoCollection mongoCollection;

    @Override
    public Person create(Person person) {

        try (MongoClient mongoClient = getConnect()) {

            MongoDatabase database = mongoClient.getDatabase("personsDB");
            this.mongoCollection = database.getCollection("persons");

            //person.setId(generateID());

            Document document = new Document(Map.of("_id", person.getId(), "firstName", person.getFname(),
                    "lastName", person.getLname(), "age", person.getAge(), "city", person.getCity()));

            mongoCollection.insertOne(document);

        } catch (Exception ex) {

            ex.getMessage();

        }

        return person;
    }

    @Override
    public Person[] read() {

            Person [] persons = {};

        try (MongoClient mongoClient = getConnect()) {

            MongoDatabase database = mongoClient.getDatabase("personsDB");
            this.mongoCollection = database.getCollection("persons");


            int rowsQuantity = getDocumentsCount();

            persons = new Person[rowsQuantity];

            FindIterable<Document> result = mongoCollection.find();

            int i = 0;

            for (Document document : result) {

                persons[i] = new Person();
                persons[i].setId((Integer) document.get("_id"));
                persons[i].setFname((String) document.get("firstName"));
                persons[i].setLname((String) document.get("lastName"));
                persons[i].setAge((Integer) document.get("age"));
                persons[i].setCity((String) document.get("city"));
                i++;

            }

        } catch (Exception ex) {
            ex.getMessage();
        }

        return persons;
    }

    @Override
    public Person update(Person person) {

        try (MongoClient mongoClient = getConnect()) {

            MongoDatabase database = mongoClient.getDatabase("personsDB");
            this.mongoCollection = database.getCollection("persons");

            Document query = new Document().append("_id", person.getId());

            Document setData = new Document().append("firstName", person.getFname()).append("lastName", person.getLname()).append("age", person.getAge()).append("city", person.getCity());

            Document update = new Document().append("$set", setData);

            mongoCollection.updateOne(query, update);

        } catch (Exception ex) {

            ex.getMessage();

        }


        return person;
    }

    @Override
    public boolean delete(int id) {

        try (MongoClient mongoClient = getConnect()) {

            MongoDatabase database = mongoClient.getDatabase("personsDB");
            this.mongoCollection = database.getCollection("persons");


        mongoCollection.deleteOne(Filters.eq("_id", id));

        } catch (Exception ex) {

            ex.getMessage();
            return false;

        }


        return true;

    }

    public MongoClient getConnect() {

        return MongoClients.create(URL);

    }

//    public void closeConnect() {
//
//        mongoClient.close();
//
//    }

//    public int generateID() {
//
//        FindIterable<Document> result = mongoCollection.find();
//
//        int user_id = 0;
//
//        for (Document document : result) {
//
//            user_id = (Integer) document.get("_id");
//
//        }
//
//        user_id++;
//
//        return user_id;
//
//    }

    public int getDocumentsCount() {

        return (int) mongoCollection.countDocuments();

    }

}
