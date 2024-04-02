package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;

import com.example.projektandroid.R;


import com.example.projektandroid.adapters.KsiazkaSerwisowaAdapter;
import com.example.projektandroid.adapters.KsiazkaSerwisowaData;
import com.example.projektandroid.databinding.ActivityKsiazkaSerwisowaBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KsiazkaSerwisowa extends AppCompatActivity {

    private ActivityKsiazkaSerwisowaBinding binding;
    private PreferenceManager preferenceManager;



    private String NazwaUzytkownika;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityKsiazkaSerwisowaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        binding.imageBack.setOnClickListener(v-> {
            Intent ksiazka=new Intent(KsiazkaSerwisowa.this,MainActivity.class);
            startActivity(ksiazka);
            finish();
        });
        binding.imageDodaj.setOnClickListener(v->{
            Intent intent=new Intent(KsiazkaSerwisowa.this,DodajKsiazkaSerwisowa.class);
            startActivity(intent);
            finish();
        });
        LadowanieUzytkownikaDanych();
        WezKsiazkaSerwisowaData();
    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);
        NazwaUzytkownika=preferenceManager.getString(Constants.KEY_NAME);

    }
    public void WezKsiazkaSerwisowaData()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_KSIAZKA)
                .whereEqualTo(Constants.KEY_NAME,NazwaUzytkownika)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null)
                    {
                        List<KsiazkaSerwisowaData> KsiazkaSerwisowaDataList=new ArrayList<>();
                        for(QueryDocumentSnapshot document:task.getResult())
                        {
                            Boolean Akumulator=document.getBoolean(Constants.KEY_AKUMULATOR);
                            String Cena=document.getString(Constants.KEY_CENA);
                            String Data=document.getString(Constants.KEY_DATA);
                            Boolean Filtry=document.getBoolean(Constants.KEY_FILTRY);
                            String Imie=document.getString(Constants.KEY_NAME);
                            Boolean Inne=document.getBoolean(Constants.KEY_INNE);
                            Boolean Klocki=document.getBoolean(Constants.KEY_KLOCKI);
                            String Nazwa=document.getString(Constants.KEY_NAZWA);
                            Boolean Olej=document.getBoolean(Constants.KEY_OLEJ);
                            Boolean OponaPrzod=document.getBoolean(Constants.KEY_OPONA_PRZOD);
                            Boolean OponaTyl=document.getBoolean(Constants.KEY_OPONA_TYL);
                            Boolean PlynHamulcowy=document.getBoolean(Constants.KEY_PLYN_HAMULCOWY);
                            Boolean Przeglad=document.getBoolean(Constants.KEY_PRZEGLAD);
                            Boolean TarczeHamulcowe=document.getBoolean(Constants.KEY_TarczeHamulcowe);

                            KsiazkaSerwisowaData ksiazkaSerwisowaData=new KsiazkaSerwisowaData(Akumulator,Cena,Data,Filtry,Imie,Inne,Klocki,Nazwa,Olej,OponaPrzod,OponaTyl,PlynHamulcowy,Przeglad,TarczeHamulcowe);
                            KsiazkaSerwisowaDataList.add(ksiazkaSerwisowaData);


                        }
                        for(KsiazkaSerwisowaData ksiazkaSerwisowaData:KsiazkaSerwisowaDataList)
                        {
                            System.out.println("Imie wlasciciela to "+ksiazkaSerwisowaData.getImie());
                        }
                        displayKsiazkaSerwisowaData(KsiazkaSerwisowaDataList);
                    }
                });

    }
    public void displayKsiazkaSerwisowaData(List<KsiazkaSerwisowaData> ksiazkaSerwisowaData)
    {
      KsiazkaSerwisowaAdapter ksiazkaSerwisowaAdapter=new KsiazkaSerwisowaAdapter(ksiazkaSerwisowaData,this,this);
      binding.recyclerViewKsiazkaSerwisowa.setAdapter(ksiazkaSerwisowaAdapter);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        binding.recyclerViewKsiazkaSerwisowa.setLayoutManager(layoutManager);
    }
    public String getNazwaUzytkownika() {
        return NazwaUzytkownika;
    }
}