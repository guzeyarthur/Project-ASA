package com.projectASA.services.impl;

import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XMLService implements IDBFactory {
    private static Document doc;
    private static Node rootNode;
    private static NodeList rootChildes;
    private static NodeList atributs;
    private static final String PATH = "D:\\Java DevEducation\\project-ASA\\src\\main\\resources\\Person.xml";

    @Override
    public Person create(Person person) {
        connectXML();
        Node root  = doc.getDocumentElement();
        Element personXML = doc.createElement("Person");

        Element personidXML = doc.createElement("ID");
        personidXML.setTextContent(String.valueOf(person.getId()));

        Element personFirstNameXML = doc.createElement("FirstName");
        personFirstNameXML.setTextContent(person.getFname());

        Element personLastNameXML = doc.createElement("LastName");
        personLastNameXML.setTextContent(person.getLname());

        Element personAgeXML = doc.createElement("Age");
        personAgeXML.setTextContent(String.valueOf(person.getAge()));

        Element personCityXML = doc.createElement("City");
        personCityXML.setTextContent(person.getCity());

        personXML.appendChild(personidXML);
        personXML.appendChild(personFirstNameXML);
        personXML.appendChild(personLastNameXML);
        personXML.appendChild(personAgeXML);
        personXML.appendChild(personCityXML);

        root.appendChild(personXML);

        writeDocument(doc);
        return person;
    }

    @Override
    public Person[] read() {
        connectXML();
        Person[] persons = new Person[rootChildes.getLength()];
        for (int i = 0; i < rootChildes.getLength(); i++)
        {
            atributs = rootChildes.item(i).getChildNodes();
            persons[i] = new Person();
            persons[i].setId(Integer.valueOf(atributs.item(0).getTextContent()));
            persons[i].setFname(atributs.item(1).getTextContent());
            persons[i].setLname(atributs.item(2).getTextContent());
            persons[i].setAge(Integer.valueOf(atributs.item(3).getTextContent()));
            persons[i].setCity(atributs.item(4).getTextContent());
        }
        return persons;
    }

    @Override
    public Person update(Person person) {
        connectXML();
        for (int i = 0; i < rootChildes.getLength(); i++)
        {
            atributs = rootChildes.item(i).getChildNodes();
            if (Integer.valueOf(atributs.item(0).getTextContent()) == person.getId())
            {
                atributs.item(1).setTextContent(person.getFname());
                atributs.item(2).setTextContent(person.getLname());
                atributs.item(3).setTextContent(String.valueOf(person.getAge()));
                atributs.item(4).setTextContent(person.getCity());
                break;
            }
        }
        writeDocument(doc);
        return person;
    }

    @Override
    public boolean delete(int id) {
        connectXML();
        for (int i = 0; i < rootChildes.getLength(); i++)
        {
            atributs = rootChildes.item(i).getChildNodes();
            if (Integer.valueOf(atributs.item(0).getTextContent()) == id)
            {
                rootNode.removeChild(rootChildes.item(i));
                break;
            }
        }
        writeDocument(doc);
        return true;
    }


    private static void connectXML(){
        File file = new File(PATH);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        {
            try {
                doc = dbf.newDocumentBuilder().parse(file);


            } catch (Exception e) {
                System.out.println("Open parsing error " + e.toString());
            }
        }
        rootNode = doc.getFirstChild();
        rootChildes = rootNode.getChildNodes();
    }

    private static void writeDocument(Document document) throws TransformerFactoryConfigurationError {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new
                    FileOutputStream(PATH);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
