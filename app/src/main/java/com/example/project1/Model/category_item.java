package com.example.project1.Model;

import android.widget.TextView;

import java.io.Serializable;

public class category_item implements Serializable {
   String categoryName;
   String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public category_item(String categoryName, String ID) {
        this.categoryName = categoryName;
        this.ID = ID;
    }

    public category_item(String name) {
        this.categoryName=name;
    }

    public String getname() {
        return categoryName;
    }

    public void setTextView(String name) {
        this.categoryName = name;
    }
}
