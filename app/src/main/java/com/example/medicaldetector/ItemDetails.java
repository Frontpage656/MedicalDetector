package com.example.medicaldetector;

public class ItemDetails {

    private String name;
    private String country;
    private String price;
    private String eDate;
    private String discription;

    public ItemDetails() {
    }

    public ItemDetails(String name, String country, String price, String eDate, String discription) {
        this.name = name;
        this.country = country;
        this.price = price;
        this.eDate = eDate;
        this.discription = discription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String geteDate() {
        return eDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }
}
