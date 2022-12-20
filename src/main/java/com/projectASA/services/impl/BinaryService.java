package com.projectASA.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;
import de.undercouch.bson4jackson.BsonFactory;

import java.io.*;

public class BinaryService implements IDBFactory {

    private static final String PATH = "D:\\Java DevEducation\\project-ASA\\src\\main\\resources\\Person.bin";
    private static final String MSG_EMPTY_FILE = "File is empty.";
    private ObjectMapper mapper = new ObjectMapper(new BsonFactory());

    @Override
    public Person create(Person person) {
        Person [] persons;
        Person [] tempPersons;

        if(checkSize() == 0){
            persons = new Person[1];
            persons[0] = person;
        } else {
            persons = new Person[checkSize()+1];
            tempPersons = returnPersons();
            for (int i = 0; i < persons.length - 1; i++) {
                persons[i] = tempPersons[i];
            }
            persons[persons.length - 1] = person;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baos, persons);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fos = new FileOutputStream(PATH)) {
            baos.writeTo(fos);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return person;
    }

    @Override
    public Person[] read() {
        return returnPersons();
    }

    @Override
    public Person update(Person person) {
        int id = person.getId();;
        boolean flag = true;

        try {
            FileInputStream fis = new FileInputStream(PATH);

            ByteArrayInputStream bais = new ByteArrayInputStream(fis.readAllBytes());

            try {
                Person[] persons = mapper.readValue(bais, Person[].class);
                for (Person pp : persons) {
                    if (pp.getId() == id) {
                        pp.setFname(person.getFname());
                        pp.setLname(person.getLname());
                        pp.setAge(person.getAge());
                        pp.setCity(person.getCity());
                        flag = false;
                    }
                }
                if(flag){
                    System.out.println("Element with ID " + id + " doesn't exist.");
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();


                try {
                    mapper.writeValue(baos, persons);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (FileOutputStream fos = new FileOutputStream(PATH)) {
                    baos.writeTo(fos);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            catch (Exception e){
                System.out.println(MSG_EMPTY_FILE);
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return person;
    }

    @Override
    public boolean delete(int id) {
        boolean success = false;
        try {
            FileInputStream fis = new FileInputStream(PATH);

            ByteArrayInputStream bais = new ByteArrayInputStream(fis.readAllBytes());

            try {
                Person[] persons = mapper.readValue(bais, Person[].class);
                Person[] newPersons = new Person[persons.length - 1];

                for (int i = 0, j = 0; i < persons.length; i++) {
                    if (persons[i].getId() != id) {
                        newPersons[j] = persons[i];
                        j++;
                    }
                    else
                    {
                        success = true;
                    }
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                try {
                    mapper.writeValue(baos, newPersons);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (FileOutputStream fos = new FileOutputStream(PATH)) {
                    baos.writeTo(fos);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            catch (Exception e){
                System.out.println("Element with ID " + id + " doesn't exist.");
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return success;
    }

    private int checkSize(){
        int size = 0;

        try {
            FileInputStream fis = new FileInputStream(PATH);

            ByteArrayInputStream bais = new ByteArrayInputStream(fis.readAllBytes());

            try{
                Person [] persons = mapper.readValue(bais, Person[].class);
                size = persons.length;
            } catch (Exception a){
                System.out.println();
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return size;
    }

    private Person [] returnPersons(){
        Person [] retPersons = new Person[checkSize()];
        try {
            FileInputStream fis = new FileInputStream(PATH);

            ByteArrayInputStream bais = new ByteArrayInputStream(fis.readAllBytes());

            Person [] persons = mapper.readValue(bais, Person[].class);

            retPersons = persons;

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return retPersons;
    }
}
