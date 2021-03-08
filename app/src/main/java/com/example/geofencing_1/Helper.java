package com.example.geofencing_1;

public class Helper {
    private String disease, medication, Imageurl;
    private int helpfull, nothelpfull;

    public Helper() {
    }

    public Helper(String disease, String medication, String imageurl) {
        this.disease = disease;
        this.medication = medication;
        Imageurl = imageurl;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }

    public int getHelpfull() {
        return helpfull;
    }

    public void setHelpfull(int helpfull) {
        this.helpfull = helpfull;
    }

    public int getNothelpfull() {
        return nothelpfull;
    }

    public void setNothelpfull(int nothelpfull) {
        this.nothelpfull = nothelpfull;
    }
}
