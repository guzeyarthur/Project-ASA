package com.projectASA.services;

import com.projectASA.models.Person;

public interface IDBFactory {
    Person create(Person person);

    Person[] read();

    Person update(Person person);

    boolean delete(int id);
}
