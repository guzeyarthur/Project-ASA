package com.projectASA.services.impl;

import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;

import java.io.*;
import java.util.ArrayList;

public class CSVService implements IDBFactory {
    private static final String PATH = "D:\\Java DevEducation\\project-ASA\\src\\main\\resources\\csvFile.csv";
    private static final String PATH2 = "D:\\Java DevEducation\\project-ASA\\src\\main\\resources\\csvFile2.csv";
    private static final String HEAD_CSV = "id,fname,lname,age,city";
    private static final String MSG_PERSON_NOT_FOUND = "Person not found";
    private static final String MSG_PERSON_UPDATE = "Person update";
    private static final String MSG_FILE_UPDATE = "File update";
    private File csvFile = new File(PATH);

    @Override
    public Person create(Person person) {
        String csvString = parseToStringCsv(person);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(PATH, true))) {
            checkHeadLine(PATH);
            bufferedWriter.write(csvString + "\n");
            System.out.println("Person save");
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("my error js");
            e.printStackTrace();
        }
        return person;
    }

    @Override
    public Person[] read() {
        ArrayList<Person> persons = new ArrayList<>();
        Person[] personsRead = new Person[0];
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {//чтение построчно
            String s;
            String[] csvArr;
            s = br.readLine();
            while ((s = br.readLine()) != null) {
                Person person = new Person();

                csvArr = s.split(",");
                person.setId(Integer.valueOf(csvArr[0]));
                person.setFname(csvArr[1]);
                person.setLname(csvArr[2]);
                person.setAge(Integer.valueOf(csvArr[3]));
                person.setCity(csvArr[4]);
                persons.add(person);
            }
            personsRead = new Person[persons.size()];
            for (int j=0; j< persons.size(); j++)
            {
                personsRead[j] = persons.get(j);
            }
        } catch (IOException e) {
            System.out.println("File is empty.");
        }
        return personsRead;
    }

    @Override
    public Person update(Person person) {
        String[] csvArr;
        int id = person.getId();
        int findID = 0;
        File csvFile22 = new File(PATH2);
        try(BufferedReader br = new BufferedReader(new FileReader(PATH)))
        {
            String s;
            s = br.readLine();
            while((s=br.readLine())!=null){
                csvArr = s.split(",");
                if(Integer.parseInt(csvArr[0]) != id ){
                    rewrite(s, PATH2);
                }
                else {
                    findID = 1;
                    String updateCsv = parseToStringCsv(person);
                    rewrite(updateCsv, PATH2);
                }
            }
            if (findID==0){
                System.out.println(MSG_PERSON_NOT_FOUND);
            }
            else System.out.println(MSG_PERSON_UPDATE);
        }
        catch(IOException e){
            System.out.println("my error js");
            e.printStackTrace();
        }
        csvFile.delete();
        boolean success = csvFile22.renameTo(csvFile);

        if (success){
            System.out.println(MSG_FILE_UPDATE);
        }
        else
        {
            System.out.println("Error file");
        }
        return person;
    }

    @Override
    public boolean delete(int id) {
        int findID = 0;
        File csvFile2 = new File(PATH2);
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String s;
            String[] csvArr;
            s = br.readLine();
            while((s=br.readLine())!=null){
                csvArr = s.split(",");
                if(Integer.parseInt(csvArr[0]) != id ){
                    rewrite(s, PATH2);

                }else {
                    findID = 1;
                }
            }
            if (findID==0){
                return false;
            }
            else System.out.println(MSG_PERSON_UPDATE);

        } catch (IOException e) {
            System.out.println("my error js");
            e.printStackTrace();
        }

        csvFile.delete();
        boolean success = csvFile2.renameTo(csvFile);

        if (success) {
            System.out.println("Person delete");
        } else {
            System.out.println("Error file");
        }
        return true;
    }

    private void rewrite(String csvStr, String pathFileToWrite) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFileToWrite, true))) {
            checkHeadLine(PATH2);
            bufferedWriter.write(csvStr + "\n");
            bufferedWriter.flush();//сохраняю
        } catch (IOException e) {//обязательная фигня
            System.out.println("my error js");
            e.printStackTrace();
        }
    }

    private void checkHeadLine(String nameFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(nameFile))) {//чтение построчно
            String firstLine;
            firstLine = br.readLine();
            if (firstLine == null ) {
                createHeadLine(nameFile);
            }
        } catch (IOException e) {
            System.out.println("my error js read");
            e.printStackTrace();
        }
    }

    private void createHeadLine(String nameFile) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(nameFile, true))) {
            bufferedWriter.write(HEAD_CSV + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("my error js");
            e.printStackTrace();
        }
    }
    private String parseToStringCsv(Person person) {
        return person.getId() +","+ person.getFname() + "," + person.getLname()  + "," + person.getAge() + "," + person.getCity() ;
    }

}
