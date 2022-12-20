package com.projectASA.services.impl;

import com.google.gson.Gson;
import com.projectASA.models.Person;
import com.projectASA.services.IDBFactory;
import com.projectASA.wrapper.WrapperFileJSON;

import java.io.*;
import java.util.ArrayList;

public class JSONService implements IDBFactory {
    private Gson gson = new Gson();
    private WrapperFileJSON wrapperFileJSON = new WrapperFileJSON();
    private File jsonFile = new File(PATH);

    private static final String PATH = "D:\\Java DevEducation\\project-ASA\\src\\main\\resources\\Person.json";
    private static final String PATH2 = "D:\\Java DevEducation\\project-ASA\\src\\main\\resources\\JSONtmp.json";
    private static final String MSG_FILE_UPDATE = "File update";

    @Override
    public Person create(Person person) {
        String json = gson.toJson(person);
        try (BufferedWriter bufferedWriter = wrapperFileJSON.getBufferWriterTrue(PATH)) {
            bufferedWriter.write(json+"\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("my error js");
            e.printStackTrace();
        }
        return person;
    }

    @Override
    public Person[] read() {
        int i = 0;
        ArrayList<Person> persons = new ArrayList<>();
        Person[] personsRead = new Person[0];
        try (BufferedReader br = new BufferedReader(new FileReader(PATH)))
        {//чтение построчно
            String s;
            while((s = br.readLine()) != null){
                Person read = gson.fromJson(s, Person.class);
                persons.add(read);
                persons.get(i).setId(read.getId());
                persons.get(i).setFname(read.getFname());
                persons.get(i).setLname(read.getLname());
                persons.get(i).setAge(read.getAge());
                persons.get(i).setCity(read.getCity());
                i++;
            }
            personsRead = new Person[i];
            for (int j=0; j<i; j++)
            {
                personsRead[j] = persons.get(j);
            }
        }
        catch(IOException e){
            System.out.println("File is empty.");
            //e.printStackTrace();
        }
        return personsRead;
    }

    @Override
    public Person update(Person person) {
        int id = person.getId();
        int findID = 0;
        File jsonFile2 = new File(PATH2); // создаю новый файл, в который буду перезаписывать
        try(BufferedReader br = new BufferedReader(new FileReader(PATH)))
        {//чтение построчно
            String s; //беру строку
            while((s=br.readLine())!=null){ //если она не пустая, продолжаю
                if(!s.contains("\"id\":"+id)){ //пока моя строка не имеет указанного ИД
                    rewrite(s, PATH2);//записываю содержимое моего файла(jsonFile) во второй файл(jsonFile2)
                }
                else {
                    findID = 1;
                    String updateJSON = gson.toJson(person);//превращаю объект в джейсон-строку
                    rewrite(updateJSON, PATH2);//добавляю джейсон строку в файл
                }
            }
            if (findID==0){
                return null;
            }
        }
        catch(IOException e){
            System.out.println("my error js");
            e.printStackTrace();
        }
        jsonFile.delete();
        boolean success = jsonFile2.renameTo(jsonFile);

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
        File jsonFile2 = new File(PATH2);
        try(BufferedReader br = new BufferedReader(new FileReader(PATH)))
        {//чтение построчно
            String s;
            while((s=br.readLine())!=null){
                if(!s.contains("\"id\":"+id)){
                    rewrite(s, PATH2);
                } else {
                    System.out.println("Person delete");
                    findID = 1;
                }
            }
            if (findID==0){
                return false;
            }
        }
        catch(IOException e){
            System.out.println("my error js");
            e.printStackTrace();
        }
        jsonFile.delete();
        boolean success = jsonFile2.renameTo(jsonFile);

        if (success){
            System.out.println(MSG_FILE_UPDATE);
        }
        else
        {
            System.out.println("Error file");
        }
        return true;
    }

    private void rewrite(String json, String pathFileToWrite){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(pathFileToWrite, true))) {//создаю буфер райтер
            //вконце обязательно true - чтобы оно дозаписывало в конец, а не удаляло уже имеющееся
            bufferedWriter.write(json+"\n");//добавляю джейсон строку и перенос строки
            bufferedWriter.flush();//сохраняю
        } catch (IOException e) {//обязательная фигня
            System.out.println("my error js");
            e.printStackTrace();
        }
    }
}
