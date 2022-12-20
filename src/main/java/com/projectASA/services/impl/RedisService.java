package com.projectASA.services.impl;

import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.ArrayList;
import java.util.Set;

public class RedisService implements IDBFactory {

    @Override
    public Person create(Person person) {

        try (Jedis jedis = connectRedis().getResource()) {

            String key = String.valueOf(person.getId());
            String value = person.getFname() + " " + person.getLname() + " " + person.getAge() + " " + person.getCity();
            jedis.set(key, value);

        }

        return person;

    }

    @Override
    public Person[] read() {

        ArrayList<Person> persons = new ArrayList<>();

        try (Jedis jedis = connectRedis().getResource()) {

            Set<String> keys = jedis.keys("*");

            for (String key : keys) {

                String value = jedis.get(key);
                String[] personStr = value.split(" ");
                Person person = new Person();
                person.setId(Integer.parseInt(key));
                person.setFname(personStr[0]);
                person.setLname(personStr[1]);
                person.setAge(Integer.parseInt(personStr[2]));
                person.setCity(personStr[3]);
                persons.add(person);

            }

        }

        Person[] personsArray = new Person[persons.size()];

        for (int i = 0; i < persons.size(); i++) {

            personsArray[i] = persons.get(i);

        }

        return personsArray;

    }

    @Override
    public Person update(Person person) {

        try (Jedis jedis = connectRedis().getResource()) {

            String key = String.valueOf(person.getId());
            String value = person.getFname() + " " + person.getLname() + " " + person.getAge() + " " + person.getCity();
            jedis.getSet(key, value);

        }

        return null;

    }

    @Override
    public boolean delete(int id) {

        try (Jedis jedis = connectRedis().getResource()) {

            jedis.del(String.valueOf(id));

        }

        return true;

    }

    private JedisPool connectRedis() {

        return new JedisPool("localhost", 6379);

    }
}
