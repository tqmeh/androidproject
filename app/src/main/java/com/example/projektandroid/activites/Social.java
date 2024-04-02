package com.example.projektandroid.activites;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;

import com.example.projektandroid.R;
import com.example.projektandroid.adapters.SocialAdapter;
import com.example.projektandroid.adapters.SocialData;

import com.example.projektandroid.databinding.ActivitySocialBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Social extends AppCompatActivity {
    private ActivitySocialBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;



    String NazwaUzytkownika;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySocialBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        LadowanieUzytkownikaDanych();
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.imageDodaj.setOnClickListener(v->{
            Intent intent=new Intent(Social.this,DodajSocial.class);
            startActivity(intent);
            finish();
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewSocial.setLayoutManager(layoutManager);
        WezSocialData();


    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);
        NazwaUzytkownika=preferenceManager.getString(Constants.KEY_NAME);



    }
public void WezSocialData()
{
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    database.collection(Constants.KEY_COLLECTION_SOCIAL)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<SocialData> socialDataList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Pobierz tekst i zdjęcie z dokumentu
                        String text = document.getString(Constants.KEY_TEXT_SOCIAL);
                        String image = document.getString(Constants.KEY_IMAGE_SOCIAL);
                        String nazwa=document.getString(Constants.KEY_NAME);
                        String Zdjecie=document.getString(Constants.KEY_IMAGE);
                        String NumerPostu=document.getString(Constants.KEY_Liczba);
                        String Polubienia=document.getString(Constants.KEY_LICZBA_LIKOW);
                        String DiskLike=document.getString(Constants.KEY_LICZBA_DISLIKOW);
                        // Stworzenie obiektu SocialData i dodanie do listy
                        SocialData socialData = new SocialData(text,image,nazwa,Zdjecie,NumerPostu,Polubienia,DiskLike);
                        socialDataList.add(socialData);
                    }


                    displaySocialData(socialDataList);
                }
            });

}
    private void displaySocialData(List<SocialData> socialDataList) {
        SocialAdapter socialAdapter = new SocialAdapter(socialDataList,this,this);
        binding.recyclerViewSocial.setAdapter(socialAdapter);

        // Przesunięcie setLayoutManager poniżej przypisania adaptera
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewSocial.setLayoutManager(layoutManager);
    }

    public String getNazwaUzytkownika() {
        return NazwaUzytkownika;
    }


}