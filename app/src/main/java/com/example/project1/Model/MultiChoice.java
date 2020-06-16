package com.example.project1.Model;

import java.io.File;
import java.io.Serializable;

public class MultiChoice implements Serializable {
    private String id,A,B,C,D,answer,question, image, timeShow;
    private File file;
    public String getImage() {
        return image;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimeShow() {
        return timeShow;
    }

    public void setTimeShow(String timeShow) {
        this.timeShow = timeShow;
    }

    public MultiChoice(String id, String a, String b, String c, String d, String answer, String question, String image, String timeShow) {
        this.id = id;
        A = a;
        B = b;
        C = c;
        D = d;
        this.answer = answer;
        this.question = question;
        this.image = image;
        this.timeShow = timeShow;
    }

    public MultiChoice(String id, String a, String b, String c, String d, String answer, String question) {
        this.id = id;
        A = a;
        B = b;
        C = c;
        D = d;
        this.answer = answer;
        this.question = question;
    }
    public MultiChoice(String id, String a, String b, String c, String d, String answer, String question, String image, String timeShow, File file) {
        this.id = id;
        A = a;
        B = b;
        C = c;
        D = d;
        this.answer = answer;
        this.question = question;
        this.timeShow=timeShow;
        this.image=image;
        this.file=file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        D = d;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
