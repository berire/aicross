package com.company;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Created by user on 30.4.2017.
 */
public class data {
    ArrayList<String> clues=new ArrayList<>();
    ArrayList<String> answers=new ArrayList<>();
    int linecount=0;
    int count=0;

    public void getdata()
    {
        try (Stream<String> lines = Files.lines(Paths.get("C:\\Users\\user\\Desktop\\clues.txt"), Charset.defaultCharset())) {
                lines.forEach(line -> {

                    String[] retval = new String[4];

                    retval = line.split("\\t");

                    /*String[] retval = new String[2];

                    retval = line.split("123");

                    String partition=retval[1];

                    String[] rtvl = new String[2];

                    rtvl=partition.split("#"+"#",2);

                    String aclue=rtvl[0];
                    String anAnswer=rtvl[1];*/

                    String aclue=retval[0];
                    String anAnswer=retval[1];

                    clues.add(aclue);
                    answers.add(anAnswer);
                    linecount++;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }





    }


    public void printClues()
    {
        getdata();
        for(int i=0;i<clues.size();i++)
        {
            System.out.println(" " + answers.get(i));
        }
    }

    public ArrayList<String> getclues()
    {
        return clues;
    }

    public ArrayList<String> getAnswers()
    {
        return answers;
    }
}
