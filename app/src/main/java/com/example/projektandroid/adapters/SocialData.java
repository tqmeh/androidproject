package com.example.projektandroid.adapters;

import java.util.List;

public class SocialData {
    private String text;
    private String images;
    private String nazwa;
    private String zdjecieProfilowe;
    private String NumerPostu;
    private String Polubienia;

    private String Uzytkownik;

    private String DisLike;



    public SocialData(String text, String images,String nazwa,String zdjecieProfilowe,String NumerPostu,String Polubienia,String DisLike) {
        this.text = text;
        this.images = images;
        this.nazwa=nazwa;
        this.zdjecieProfilowe=zdjecieProfilowe;
        this.NumerPostu=NumerPostu;
        this.Polubienia=Polubienia;
        this.DisLike=DisLike;
    }

    public String getText() {
        return text;
    }

    public String getImages() {
        return images;
    }
    public String getNazwa() {
        return nazwa;
    }
    public String getZdjecieProfilowe() {
        return zdjecieProfilowe;
    }
    public String getNumerPostu() {
        return NumerPostu;
    }
    public String getPolubienia() {
        return Polubienia;
    }
    public String getDisLike() {
        return DisLike;
    }
}
