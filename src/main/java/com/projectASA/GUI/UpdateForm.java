package com.projectASA.GUI;

import com.projectASA.cache.FactoryCache;
import com.projectASA.models.Person;
import com.projectASA.services.ValidationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateForm extends JDialog {

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

    public UpdateForm(MainForm mainWindow, String selectedDB, JTable table) {

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
        id.setText(String.valueOf(table.getValueAt(table.getSelectedRow(), 0)));
        add(id);

        firstName.setBounds(105,105,95,20);
        firstName.setText(String.valueOf(table.getValueAt(table.getSelectedRow(), 1)));
        add(firstName);

        lastName.setBounds(205,105,95,20);
        lastName.setText(String.valueOf(table.getValueAt(table.getSelectedRow(), 2)));
        add(lastName);

        age.setBounds(305,105,95,20);
        age.setText(String.valueOf(table.getValueAt(table.getSelectedRow(), 3)));
        add(age);

        city.setBounds(405,105,95,20);
        city.setText(String.valueOf(table.getValueAt(table.getSelectedRow(), 4)));
        add(city);

        apply.setBounds(65, 200, 130, 50);
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to update the person?",
                        "Update",
                        JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {

                    if(ValidationService.checkTextFields(firstName, lastName, age, city)) {

                        Person person = new Person();
                        person.setId(Integer.parseInt(id.getText()));
                        person.setFname(firstName.getText());
                        person.setLname(lastName.getText());
                        person.setAge(Integer.parseInt(age.getText()));
                        person.setCity(city.getText());

                        if (ValidationService.checkPerson(person)) {

                            mainWindow.db.getEnvironment(selectedDB).update(person);
                            //mainWindow.model.setValueAt(person.getId(), table.getSelectedRow(), 0);
                            mainWindow.model.setValueAt(person.getFname(), table.getSelectedRow(), 1);
                            mainWindow.model.setValueAt(person.getLname(), table.getSelectedRow(), 2);
                            mainWindow.model.setValueAt(person.getAge(), table.getSelectedRow(), 3);
                            mainWindow.model.setValueAt(person.getCity(), table.getSelectedRow(),4);

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
