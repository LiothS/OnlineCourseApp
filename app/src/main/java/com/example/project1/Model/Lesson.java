package com.example.project1.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Lesson implements Serializable {
    private String ID,video, idcourse, title,order;
    private ArrayList<String> file;
    private ArrayList<MultiChoice> multiChoice;
    private ArrayList<MultiChoice> quizTest;
    private boolean isComplete;

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public Lesson(String ID, String video, String idcourse, String title, String order, ArrayList<String> file, ArrayList<MultiChoice> multiChoice) {
        this.ID = ID;
        this.video = video;
        this.idcourse = idcourse;
        this.title = title;
        this.order = order;
        this.file = file;
        this.multiChoice = multiChoice;
    }

    public ArrayList<MultiChoice> getQuizTest() {
        return quizTest;
    }

    public void setQuizTest(ArrayList<MultiChoice> quizTest) {
        this.quizTest = quizTest;
    }

    public Lesson(String ID, String video, String idcourse, String title, String order, ArrayList<String> file, ArrayList<MultiChoice> multiChoice, ArrayList<MultiChoice> quiz) {
        this.ID = ID;
        this.video = video;
        this.idcourse = idcourse;
        this.title = title;
        this.order = order;
        this.file = file;
        this.multiChoice = multiChoice;
        this.quizTest=quiz;
    }
    public Lesson(String ID, String video, String idcourse, String title, String order, ArrayList<String> file, ArrayList<MultiChoice> multiChoice, ArrayList<MultiChoice> quiz,boolean cpl) {
        this.ID = ID;
        this.video = video;
        this.idcourse = idcourse;
        this.title = title;
        this.order = order;
        this.file = file;
        this.multiChoice = multiChoice;
        this.quizTest=quiz;
        this.isComplete=cpl;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getIdcourse() {
        return idcourse;
    }

    public void setIdcourse(String idcourse) {
        this.idcourse = idcourse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public  ArrayList<String>getFile() {
        return file;
    }

    public void setFile(ArrayList<String> file) {
        this.file = file;
    }

    public ArrayList<MultiChoice> getMultiChoice() {
        return multiChoice;
    }

    public void setMultiChoice(ArrayList<MultiChoice> multiChoice) {
        this.multiChoice = multiChoice;
    }
}
