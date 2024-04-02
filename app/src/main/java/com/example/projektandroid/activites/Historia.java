package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.example.projektandroid.R;
import com.example.projektandroid.adapters.HistoriaAdapter;
import com.example.projektandroid.adapters.HistoriaData;
import com.example.projektandroid.databinding.ActivityHistoriaBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Historia extends AppCompatActivity {

    private ActivityHistoriaBinding binding;
    private PreferenceManager preferenceManager;



    String NazwaUzytkownika;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHistoriaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());

        binding.imageBack.setOnClickListener(v-> {
            Intent intent=new Intent(Historia.this,MainActivity.class);
            startActivity(intent);
        });
        LadowanieUzytkownikaDanych();
        binding.imageDodaj.setOnClickListener(v->
        {
            Intent intent=new Intent(Historia.this,DodajHistoria.class);
            startActivity(intent);
            finish();
        });
        WezHistoriaDate();
    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);
        NazwaUzytkownika=preferenceManager.getString(Constants.KEY_NAME);

    }
    public void WezHistoriaDate()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_HISTORIA)
                .whereEqualTo(Constants.KEY_NAME,NazwaUzytkownika)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&&task.getResult()!=null)
                    {
                        List<HistoriaData> HistoriaDataList=new ArrayList<>();
                        for(QueryDocumentSnapshot document: task.getResult())
                        {
                            String imie=document.getString(Constants.KEY_NAME);
                            String kilometry=document.getString(Constants.KEY_LICZBA_KILOMETROW);
                            String KwotaPaliwo=document.getString(Constants.KEY_ILOSC_PALIWA);
                            String Autostrada=document.getString(Constants.KEY_OPLATY_DROGOWE);
                            String Wineta=document.getString(Constants.KEY_OPLATY_WINETA);
                            String Nazwa=document.getString(Constants.KEY_NAZWA);
                            String Data=document.getString(Constants.KEY_DATA);
                            HistoriaData historiaData=new HistoriaData(imie,Autostrada,KwotaPaliwo,kilometry,Wineta,Nazwa,Data);
                            HistoriaDataList.add(historiaData);
                        }
                        for(HistoriaData historiaData:HistoriaDataList)
                        {
                            System.out.println("Imie wlasciciela to "+historiaData.getImie());
                            System.out.println("Ilosc kilometrow wpisanych to "+historiaData.getKilometry());
                        }
                        displayHistoriaData(HistoriaDataList);
                    }
                });

    }
    public void displayHistoriaData(List<HistoriaData> historiaData)
    {
        HistoriaAdapter historiaAdapter=new HistoriaAdapter(historiaData,this,this);
        binding.recyclerViewHistoria.setAdapter(historiaAdapter);

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        binding.recyclerViewHistoria.setLayoutManager(layoutManager);

    }
    public String getNazwaUzytkownika() {
        return NazwaUzytkownika;
    }
}