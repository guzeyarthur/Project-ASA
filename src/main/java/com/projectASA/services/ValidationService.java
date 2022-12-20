package com.projectASA.services;

import com.projectASA.models.Person;

import javax.swing.*;

public class ValidationService {
    private static final String REGEX_ID_AGE = "[0-9]+";

    public static boolean checkPerson(Person person){
        if (person.getAge() < 0 || person.getAge() > 120){
            return false;
        }
        if (person.getId() < 0){
            return false;
        }
        return true;
    }

    public static boolean checkTextFields(JTextField firstName,JTextField lastName,
                                          JTextField age, JTextField city) {
        if( firstName.getText().length() == 0 )
        {
            return false;
        }
        if( lastName.getText().length() == 0 )
        {
            return false;
        }
        if( age.getText().length() == 0 || !( age.getText().matches(REGEX_ID_AGE) ) )
        {
            return false;
        }
        if( city.getText().length() == 0 )
        {
            return false;
        }
        return true;
    }
}
