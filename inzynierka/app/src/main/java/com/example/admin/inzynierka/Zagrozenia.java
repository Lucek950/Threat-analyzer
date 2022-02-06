package com.example.admin.inzynierka;

public class Zagrozenia {

    public String zagrozeniaID;
    public String zagrozeniaTYP;
    public String zagrozeniaOpis;
    public double szerokosc;
    public double dlugosc;
    public int zagrozeniaStopien;

    public Zagrozenia(){
    }

    public Zagrozenia(String zagrozeniaID, String zagrozeniaTYP, String zagrozeniaOpis, double szerokosc, double dlugosc, int zagrozeniaStopien){
        this.zagrozeniaID = zagrozeniaID;
        this.zagrozeniaTYP = zagrozeniaTYP;
        this.zagrozeniaOpis = zagrozeniaOpis;
        this.szerokosc = szerokosc;
        this.dlugosc = dlugosc;
        this.zagrozeniaStopien = zagrozeniaStopien;
    }

    public String getZagrozeniaOpis(){
        return zagrozeniaOpis;
    }

    public String getZagrozeniaTYP(){
        return zagrozeniaTYP;
    }

    public int getZagrozeniaStopien(){
        return zagrozeniaStopien;
    }
}