package com.example.project1.Model;

public class courseItem {
    private  String url,title,fee,author;
    private float rating,price,discount,totalVote;

    public courseItem(String url, String title, String fee, float rating) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.rating=rating;
    }

    public courseItem(String url, String title, String fee, String author, float rating, float price, float discount, float totalVote) {
        this.url = url;
        this.title = title;
        this.fee = fee;
        this.author = author;
        this.rating = rating;
        this.price = price;
        this.discount = discount;
        this.totalVote = totalVote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getTotalVote() {
        return totalVote;
    }

    public void setTotalVote(float totalVote) {
        this.totalVote = totalVote;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
