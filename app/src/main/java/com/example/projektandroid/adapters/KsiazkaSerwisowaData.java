package com.example.projektandroid.adapters;

public class KsiazkaSerwisowaData {



    private boolean Akumulator;
    private String Cena;
    private String Data;
    private boolean Filtry;
    private String Imie;
    private boolean Inne;
    private boolean Klocki;
    private String Nazwa;
    private boolean Olej;
    private boolean OponaPrzod;
    private boolean OponaTyl;
    private boolean PlynHamulcowy;
    private boolean Przeglad;
    private boolean TarczeHamulcowe;

    public KsiazkaSerwisowaData(boolean akumulator, String cena, String data, boolean filtry, String imie, boolean inne, boolean klocki, String nazwa, boolean olej, boolean oponaPrzod, boolean oponaTyl, boolean plynHamulcowy, boolean przeglad, boolean tarczeHamulcowe) {
        Akumulator = akumulator;
        Cena = cena;
        Data = data;
        Filtry = filtry;
        Imie = imie;
        Inne = inne;
        Klocki = klocki;
        Nazwa = nazwa;
        Olej = olej;
        OponaPrzod = oponaPrzod;
        OponaTyl = oponaTyl;
        PlynHamulcowy = plynHamulcowy;
        Przeglad = przeglad;
        TarczeHamulcowe = tarczeHamulcowe;
    }

    public boolean isAkumulator() {
        return Akumulator;
    }

    public String getCena() {
        return Cena;
    }

    public String getData() {
        return Data;
    }

    public boolean isFiltry() {
        return Filtry;
    }

    public String getImie() {
        return Imie;
    }

    public boolean isInne() {
        return Inne;
    }

    public boolean isKlocki() {
        return Klocki;
    }

    public String getNazwa() {
        return Nazwa;
    }

    public boolean isOlej() {
        return Olej;
    }

    public boolean isOponaPrzod() {
        return OponaPrzod;
    }

    public boolean isOponaTyl() {
        return OponaTyl;
    }

    public boolean isPlynHamulcowy() {
        return PlynHamulcowy;
    }

    public boolean isPrzeglad() {
        return Przeglad;
    }

    public boolean isTarczeHamulcowe() {
        return TarczeHamulcowe;
    }
}
