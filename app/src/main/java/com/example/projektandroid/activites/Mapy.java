package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.databinding.ActivityEdycjaBinding;
import com.example.projektandroid.databinding.ActivityMapyBinding;

public class Mapy extends AppCompatActivity {

    private EditText wpiszPunktPoczatkowy;
    private EditText wpiszPunktZakonczenia;
    private String cel,zakonczenie;
    private ActivityMapyBinding binding;
    Intent intent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    wpiszPunktPoczatkowy=findViewById(R.id.wpiszPunktPoczatkowy);
    wpiszPunktZakonczenia=findViewById(R.id.wpiszPunktZakonczenia);
        binding.przyciskNawiguj.setOnClickListener(view ->wpisz());
        binding.imageBack.setOnClickListener(v-> onBackPressed());
    }

    public void wpisz()
    {
        cel=wpiszPunktPoczatkowy.getText().toString().trim();
        zakonczenie=wpiszPunktZakonczenia.getText().toString().trim();

        if(cel.isEmpty()&&cel!=null)
        {
            Toast.makeText(Mapy.this, "Nie wpisałes punktu startowego", Toast.LENGTH_SHORT).show();
        }
        else if(zakonczenie.isEmpty()&&zakonczenie!=null)
        {
            Toast.makeText(Mapy.this, "Nie wpisałeś punktu zakończenia", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(Mapy.this, "Wpisałeś wszystkie punkty", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + cel + "&destination=" + zakonczenie);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
            finish();

        }
    }

}