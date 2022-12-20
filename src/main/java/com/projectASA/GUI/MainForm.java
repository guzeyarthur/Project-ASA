package com.projectASA.GUI;

import com.projectASA.cache.FactoryCache;
import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;
import com.projectASA.services.impl.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm extends JPanel {

    private static final String NOT_SELECTED_ROW_DELETE =
            "To delete a person, \n" +
                    "select the row in the table that you want to delete";
    private static final String NOT_SELECTED_ROW_UPDATE =
            "To update a person, \n" +
                    "select the row in the table that you want to update";

    IDBFactory[] dataBases = new IDBFactory[]{new BinaryService(),
            new CSVService(), new JSONService(), new XMLService(), new YAMLService(),
            new MySqlService(), new PostgreSqlService(), new MongoDBService(),
            new CassandraService(), new H2Service(), new RedisService(), new GraphDBService()};
    FactoryCache db = new FactoryCache(dataBases);

    private static final String[] columnNames = {
            "ID",
            "First Name",
            "Last Name",
            "Age",
            "City"
    };
    private static final String[] typesOfDB = {
            "binary",
            "csv",
            "json",
            "xml",
            "yaml",
            "mySql",
            "postgreSql",
            "mongoDB",
            "cassandra",
            "h2",
            "redis",
            "graphDB"
    };

    TableModel tableModel = new DefaultTableModel(columnNames, 0);

    JTable table = new JTable(tableModel) {

        @Override
        public boolean isCellEditable(int i, int i1) {
            return false;
        }

    };

    JScrollPane scrollPanel = new JScrollPane(table);
    DefaultTableModel model = (DefaultTableModel) table.getModel();

    JComboBox databaseSelect = new JComboBox(typesOfDB);
    JButton create = new JButton("Create");
    JButton read = new JButton("Read");
    JButton update = new JButton("Update");
    JButton delete = new JButton("Delete");

    Person data;

    public MainForm(){
        setLayout(null);

        create.setBounds(605,150,390,100);
        create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedBD = (String) databaseSelect.getSelectedItem();
                createModalWindow(selectedBD);
            }
        });
        add(create);

        read.setBounds(605,150+100+5,390,100);
        read.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedBD = (String) databaseSelect.getSelectedItem();
                Person[] persons = db.getEnvironment(selectedBD).read();

                model.setRowCount(0);

                for (int i = 0; i < persons.length; i++)
                {
                        model.addRow(new Object[]{String.valueOf(persons[i].getId()), persons[i].getFname(),
                                persons[i].getLname(), persons[i].getAge(), persons[i].getCity()});
                }
            }
        });
        add(read);

        update.setBounds(605,150+100+5+100+5,390,100);
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1){
                    String selectedBD = (String) databaseSelect.getSelectedItem();
                    updateModalWindow(selectedBD);
                }
                else
                {
                    JOptionPane.showMessageDialog(null,
                            NOT_SELECTED_ROW_UPDATE,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(update);

        delete.setBounds(605,150+100+5+100+5+100+5,390,100);
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int n = JOptionPane.showConfirmDialog(
                        null,
                        "Would you like to delete the person?",
                        "Delete",
                        JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {

                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1)

                    {

                        String selectedBD = (String) databaseSelect.getSelectedItem();

                        int selectedID = Integer.valueOf(table.getValueAt(selectedRow,0).toString());
                        boolean successDelete = db.getEnvironment(selectedBD).delete(selectedID);

                        if (successDelete)
                        {

                            model.removeRow(table.getSelectedRow());

                        }

                    } else {

                        JOptionPane.showMessageDialog(null,
                                NOT_SELECTED_ROW_DELETE,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);

                    }
                }
            }
        });
        add(delete);

        scrollPanel.setBounds(5,5, 590,560);
        add(scrollPanel);

        databaseSelect.setBounds(605,5, 390,30);
        databaseSelect.setSelectedIndex(0);
        add(databaseSelect);

    }

    public void createModalWindow(String selectedDB) {

        CreateForm modalWindow = new CreateForm(this, selectedDB);
    }

    public void updateModalWindow(String selectedDB) {

        UpdateForm modalWindow = new UpdateForm(this, selectedDB, table);
    }

    public void setData(Person data) {
        this.data = data;
    }
}