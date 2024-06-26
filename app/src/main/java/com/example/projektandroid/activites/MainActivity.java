package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;


import com.example.projektandroid.databinding.ActivityMainBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
    LadowanieUzytkownikaDanych();
    getToken();
    setListeners();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }
    private void setListeners()
    {
        binding.imageSignOut.setOnClickListener(v->signOut());
        binding.imageChat.setOnClickListener(v -> {
            // Obsługa kliknięcia na imageChat
            Intent intent = new Intent(MainActivity.this, chat.class);
            startActivity(intent);
        });
        binding.imageEdit.setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this,Edycja.class);
            startActivity(intent);
        });

        binding.imageMaps.setOnClickListener(view ->{
            Intent intent=new Intent(MainActivity.this,Mapy.class);
            startActivity(intent);
        });

        binding.imageSocial.setOnClickListener(view->{
            Intent intent=new Intent(MainActivity.this,Social.class);
            startActivity(intent);
        });
        binding.imageHistoria.setOnClickListener(view->
        {
            Intent intent=new Intent(MainActivity.this,Historia.class);
            startActivity(intent);
        });
        binding.imageSerwis.setOnClickListener(view->
        {
            Intent intent=new Intent(MainActivity.this,KsiazkaSerwisowa.class);
            startActivity(intent);
        });
        binding.imageSMS.setOnClickListener(view ->
        {
            Intent intent=new Intent(MainActivity.this,WyslijSMS.class);
            startActivity(intent);
        });

    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);

    }
    private void showToast(String wiadomosc)
    {
        Toast.makeText(getApplicationContext(),wiadomosc,Toast.LENGTH_SHORT).show();

    }
    private void getToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token)
    {
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        DocumentReference documentReference=
        database.collection(Constants.KEY_COLLECTION_USERS).document(
          preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused->showToast("Token updated successfuly"))
                .addOnFailureListener(e-> showToast("Unable to update token"));

    }
    private void signOut()
    {
        showToast("Wylogowywanie");
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        DocumentReference documentReference=
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String,Object> updates=new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(),Logowanie.class));
                    finish();
                })
                .addOnFailureListener(e->showToast("Unable to sign out"));
    }

}