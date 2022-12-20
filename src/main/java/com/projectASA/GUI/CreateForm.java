package com.projectASA.GUI;

import com.projectASA.cache.FactoryCache;
import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;
import com.projectASA.services.ValidationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateForm extends JDialog {

    private static final String ERROR_INPUT_MESSAGE_PERSON = "You entered incorrect data!\n" +
            "Age can be from 0 to 120. \n" +
            "Try again!";
    private static final String ERROR_INPUT_MESSAGE_TEXTFIELD = "You entered incorrect data!\n" +
            "All fields must be filled \n" +
            "ID and AGE - only number values \n" +
            "Try again!";

    private JTextField id = new JFormattedTextField();//сделать вывод айдишника сюда
    private JTextField firstName = new JFormattedTextField();
    private JTextField lastName = new JFormattedTextField();
    private JTextField age = new JFormattedTextField();
    private JTextField city = new JFormattedTextField();

    private JLabel labelId = new JLabel("ID");
    private JLabel labelFirstName = new JLabel("First Name");
    private JLabel labelLastName = new JLabel("Last Name");
    private JLabel labelAge = new JLabel("Age");
    private JLabel labelCity = new JLabel("City");

    private JButton apply = new JButton("Apply");
    private JButton cancel = new JButton("Cancel");

    public CreateForm(MainForm mainWindow, String selectedDB) {

        this.setSize(500,300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.setLayout(null);

        labelId.setBounds(5,5,95,95);
        labelId.setHorizontalAlignment(JLabel.CENTER);
        add(labelId);

        labelFirstName.setBounds(105,5,95,95);
        labelFirstName.setHorizontalAlignment(JLabel.CENTER);
        add(labelFirstName);

        labelLastName.setBounds(205,5,95,95);
        labelLastName.setHorizontalAlignment(JLabel.CENTER);
        add(labelLastName);

        labelAge.setBounds(305,5,95,95);
        labelAge.setHorizontalAlignment(JLabel.CENTER);
        add(labelAge);

        labelCity.setBounds(405,5,95,95);
        labelCity.setHorizontalAlignment(JLabel.CENTER);
        add(labelCity);

        id.setBounds(5,105,95,20);
        id.setEditable(false);
        add(id);

        firstName.setBounds(105,105,95,20);
        add(firstName);

        lastName.setBounds(205,105,95,20);
        add(lastName);

        age.setBounds(305,105,95,20);
        add(age);

        city.setBounds(405,105,95,20);
        add(city);

        apply.setBounds(65, 200, 130, 50);
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ValidationService.checkTextFields(firstName, lastName, age, city))
                {

                    Person newPerson  = new Person();
                    newPerson.setId(generateID(mainWindow, selectedDB));
                    newPerson.setFname(firstName.getText());
                    newPerson.setLname(lastName.getText());
                    newPerson.setAge(Integer.parseInt(age.getText()));
                    newPerson.setCity(city.getText());
                    if (ValidationService.checkPerson(newPerson))
                    {
                        newPerson = mainWindow.db.getEnvironment(selectedDB).create(newPerson);
                        mainWindow.model.addRow(new Object[]{String.valueOf(newPerson.getId()), newPerson.getFname(),
                                newPerson.getLname(), newPerson.getAge(), newPerson.getCity()});
                        mainWindow.db.getEnvironment(selectedDB).read();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null,
                                ERROR_INPUT_MESSAGE_PERSON,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            ERROR_INPUT_MESSAGE_TEXTFIELD,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(apply);

        cancel.setBounds(65+250, 200, 130, 50);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(cancel);
    }

    private int generateID(MainForm mainWindow, String selectedDB) {

        int newID = 0;
        Person[] forNewID = mainWindow.db.getEnvironment(selectedDB).read();

        for (int i =0; i<forNewID.length; i++)
        {

            if (forNewID != null && forNewID[i].getId()>newID){

                newID = forNewID[i].getId();

            }

        }

        newID++;

        return newID;

    }

    public JTextField getId() {
        return id;
    }

    public JTextField getFirstName() {
        return firstName;
    }

    public JTextField getLastName() {
        return lastName;
    }

    public JTextField getAge() {
        return age;
    }

    public JTextField getCity() {
        return city;
    }
}
