package com.example.projektandroid.adapters;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.projektandroid.R;

import java.nio.file.WatchEvent;

public class HistoriaData  {



    private String Imie;
private String Autostrada;
private String KwotaPaliwo;
private String Kilometry;
private String Wineta;


    private String Data;


    private String Nazwa;

public HistoriaData(String Imie,String Autostrada,String KwotaPaliwo,String Kilometry,String Wineta,String Nazwa,String Data)
{
    this.Imie=Imie;
    this.Autostrada=Autostrada;
    this.KwotaPaliwo=KwotaPaliwo;
    this.Kilometry= Kilometry;
    this.Wineta=Wineta;
    this.Nazwa=Nazwa;
    this.Data=Data;
}
    public String getImie() {
        return Imie;
    }

    public String getAutostrada() {
        return Autostrada;
    }

    public String getKwotaPaliwo() {
        return KwotaPaliwo;
    }

    public String getKilometry() {
        return Kilometry;
    }

    public String getWineta() {
        return Wineta;
    }
    public String getNazwa() {
        return Nazwa;
    }
    public String getData() {
        return Data;
    }

}