package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.databinding.ActivityEdycjaBinding;
import com.example.projektandroid.databinding.ActivityZmienHasloBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ZmienHaslo extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    private ActivityZmienHasloBinding binding;
    private FirebaseFirestore firestore;
    private EditText wpiszAktualneHaslo;
    private EditText wpiszNoweHaslo;
    private EditText wpiszPonownieHaslo;
    private String noweHaslo,noweHasloponownie;
private String Aktualne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityZmienHasloBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();
        wpiszAktualneHaslo=findViewById(R.id.wpiszAktualneHaslo);
        wpiszNoweHaslo=findViewById(R.id.wpiszNoweHaslo);
        wpiszPonownieHaslo=findViewById(R.id.wpiszPonownieHaslo);
        binding.przyciskAktualizuj.setOnClickListener(e->{
                SprawdzHaslo();
                SprawdzHasla();

        });
        binding.imageBack.setOnClickListener(v-> onBackPressed());

    }

    public void SprawdzHaslo() {
        String userID = preferenceManager.getString(Constants.KEY_USER_ID);
        if (userID != null && !userID.isEmpty()) {
            // Utwórz referencję do dokumentu użytkownika w kolekcji "Użytkownicy" w Firestore
            DocumentReference userRef = firestore.collection("Użytkownicy").document(userID);

            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Aktualne = documentSnapshot.getString("hasło");
                            System.out.println("Haslo uzytkownika to "+Aktualne);
                        } else {
                            System.out.println("Dokument nie istnieje");
                        }
                    })
                    .addOnFailureListener(e -> {

                    });
            noweHaslo=wpiszAktualneHaslo.getText().toString().trim();




        }
    }
    public void SprawdzHasla()
    {
        if(!noweHaslo.equals(Aktualne))
        {
            System.out.println("Hasla sa od siebie rozne");
            Toast.makeText(ZmienHaslo.this, "Hasla nie sa takie same", Toast.LENGTH_SHORT).show();
        }
        else
        {


            noweHaslo=wpiszNoweHaslo.getText().toString().trim();
            System.out.println("Nowe haslo to "+noweHaslo);
            noweHasloponownie=wpiszPonownieHaslo.getText().toString().trim();
            System.out.println("Ponowne wpisane haslo to "+noweHasloponownie);

            if(noweHaslo.equals(noweHasloponownie))
            {
                Toast.makeText(ZmienHaslo.this, "Nowe hasla sa takie same ", Toast.LENGTH_SHORT).show();
                String userId = preferenceManager.getString(Constants.KEY_USER_ID);
                if (userId != null && !userId.isEmpty()) {
                    // Utwórz referencję do dokumentu użytkownika w kolekcji "Użytkownicy" w Firestore
                    DocumentReference userRef = firestore.collection("Użytkownicy").document(userId);
                    userRef.update("hasło", null)
                            .addOnSuccessListener(aVoid -> {
                                // Po pomyślnym usunięciu starego adresu e-mail, dodaj nowy
                                userRef.update("hasło", noweHaslo)
                                        .addOnSuccessListener(aVoid1 -> {
                                            // Po pomyślnej aktualizacji, wyświetl komunikat sukcesu
                                            Toast.makeText(ZmienHaslo.this, "Hasło zostało zmienione", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Obsłuż błąd podczas dodawania nowego adresu e-mail
                                            Toast.makeText(ZmienHaslo.this, "Błąd podczas dodawannia nowego hasła", Toast.LENGTH_SHORT).show();

                                        });
                            })
                            .addOnFailureListener(e -> {
                                // Obsłuż błąd podczas usuwania starego adresu e-mail
                                Toast.makeText(ZmienHaslo.this, "Błąd podczas usuwania nowego hasłą", Toast.LENGTH_SHORT).show();
                            });
                }
            }
            else
            {
                Toast.makeText(ZmienHaslo.this, "Nowe hasla sa rozne ", Toast.LENGTH_SHORT).show();
            }
        }
        }
    }
