package com.example.projektandroid.activites;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.databinding.ActivityEdycjaBinding;
import com.example.projektandroid.databinding.ActivityMainBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Edycja extends AppCompatActivity {
    private ActivityEdycjaBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    private FirebaseFirestore firestore;
    private EditText wpiszImie;
    private EditText WpiszMaila;
    private String nowyMail;
    TextView UsunKonto;
    String noweImie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEdycjaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();
        wpiszImie=findViewById(R.id.wpiszImie);
        WpiszMaila = findViewById(R.id.WpiszMaila);
        UsunKonto=findViewById(R.id.UsunKonto);
        binding.zjdecieProfilowe.setOnClickListener(v -> openGallery());
        binding.przyciskRejestruj.setOnClickListener(v->
        {
            aktualizujNazwe();
                aktualizujEmail();

        });
        binding.zmienHaslo.setOnClickListener(v->
               startActivity(new Intent(getApplicationContext(),ZmienHaslo.class)) );
        binding.imageBack.setOnClickListener(v->{
            Intent intent=new Intent(Edycja.this,MainActivity.class);
            startActivity(intent);
        });
        UsunKonto.setOnClickListener(v->{
            Intent intent=new Intent(Edycja.this, com.example.projektandroid.activites.UsunKonto.class);
            startActivity(intent);
        });
    }
    /*private void LadowanieUzytkownikaDanych()
    {

        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);

        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        if (userId != null && !userId.isEmpty()) {
            DocumentReference userRef = firestore.collection("Użytkownicy").document(userId);

            // Pobranie danych użytkownika z Firestore
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("Imie");
                            String mail=documentSnapshot.getString("email");
                            // Ustawienie imienia w EditText
                            wpiszImie.setText(userName);

                        }
                    })
                    .addOnFailureListener(e -> {
                        // Błąd podczas pobierania danych użytkownika
                        Toast.makeText(Edycja.this, "Błąd podczas pobierania danych użytkownika", Toast.LENGTH_SHORT).show();
                    });
        }
    }


     */
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Sprawdź, czy wynik jest OK i czy istnieje dane
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                    // Pobierz URI wybranego obrazu z danych wynikowych
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        // Otwórz strumień wejściowy dla URI obrazu
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);

                        // Dekoduj strumień wejściowy na obiekt Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Ustaw Bitmapę w ImageView (zjdecieProfilowe)
                        binding.zjdecieProfilowe.setImageBitmap(bitmap);

                        // Zakoduj obraz do formatu Base64
                        encodedImage = encodeImage(bitmap);

                        // Wywołaj metodę, która zaktualizuje obraz w bazie danych Firebase
                        updateProfilePictureInFirebase(encodedImage);

                    } catch (FileNotFoundException e) {
                        // Obsłuż błąd, jeśli plik nie zostanie znaleziony
                        e.printStackTrace();
                    }
                }
            }
    );

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    private void updateProfilePictureInFirebase(String encodedImage) {
        // Pobierz ID użytkownika z preferencji
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        // Sprawdź, czy ID użytkownika jest dostępne
        if (userId != null && !userId.isEmpty()) {
            // Utwórz referencję do dokumentu użytkownika w kolekcji "Użytkownicy" w Firestore
            DocumentReference userRef = firestore.collection("Użytkownicy").document(userId);

            // Pobierz dane użytkownika z Firestore
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Sprawdź, czy dokument istnieje
                        if (documentSnapshot.exists()) {
                            // Sprawdź, czy dokument zawiera pole "zdjecie"
                            if (documentSnapshot.contains("zdjecie")) {
                                // Jeśli tak, usuń stare zdjęcie
                                userRef.update("zdjecie", null)
                                        .addOnSuccessListener(aVoid -> {
                                            // Po pomyślnym usunięciu starego zdjęcia, dodaj nowe
                                            updateNewProfilePicture(userRef, encodedImage);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Obsłuż błąd podczas usuwania starego zdjęcia
                                            Toast.makeText(Edycja.this, "Błąd podczas usuwania starego zdjęcia", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Jeśli dokument nie zawiera pola "zdjecie", dodaj nowe zdjęcie
                                updateNewProfilePicture(userRef, encodedImage);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Obsłuż błąd podczas pobierania danych użytkownika
                        Toast.makeText(Edycja.this, "Błąd podczas pobierania danych użytkownika", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void updateNewProfilePicture(DocumentReference userRef, String encodedImage) {
        // Utwórz mapę zaktualizowanych danych, aby dodać nowe zdjęcie
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("zdjecie", encodedImage);

        // Zaktualizuj dokument użytkownika w Firestore z nowym zdjęciem
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Po pomyślnej aktualizacji, wyświetl komunikat sukcesu
                    Toast.makeText(Edycja.this, "Zdjęcie profilowe zaktualizowane", Toast.LENGTH_SHORT).show();

                    // Zaktualizuj lokalne preferencje z nowym zakodowanym obrazem
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                })
                .addOnFailureListener(e -> {
                    // Obsłuż błąd podczas aktualizacji zdjęcia profilowego
                    Toast.makeText(Edycja.this, "Błąd podczas aktualizacji zdjęcia profilowego", Toast.LENGTH_SHORT).show();
                });
    }

    public void aktualizujNazwe()
    {
        String userID=preferenceManager.getString(Constants.KEY_USER_ID);
        if (userID != null && !userID.isEmpty()) {
            // Utwórz referencję do dokumentu użytkownika w kolekcji "Użytkownicy" w Firestore
            DocumentReference userRef = firestore.collection("Użytkownicy").document(userID);

            // Pobierz aktualny adres e-mail z pola WpiszMaila
            noweImie = wpiszImie.getText().toString().trim();
            if(!noweImie.isEmpty()&&noweImie!=null) {
                // Usuń stare pole "email" z dokumentu użytkownika
                userRef.update("Imie", null)
                        .addOnSuccessListener(aVoid -> {
                            // Po pomyślnym usunięciu starego adresu e-mail, dodaj nowy
                            userRef.update("Imie", noweImie)
                                    .addOnSuccessListener(aVoid1 -> {
                                        // Po pomyślnej aktualizacji, wyświetl komunikat sukcesu
                                        Toast.makeText(Edycja.this, "Nazwa konta zostala zaktualizowana", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Obsłuż błąd podczas dodawania nowego adresu e-mail
                                        Toast.makeText(Edycja.this, "Błąd podczas dodawania nowej nazwy konta", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Obsłuż błąd podczas usuwania starego adresu e-mail
                            Toast.makeText(Edycja.this, "Błąd podczas usuwania starej nazwy konta", Toast.LENGTH_SHORT).show();
                        });
            }
            else
            {
                Toast.makeText(Edycja.this, "Nie wpisales nowej nazwy konta", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void aktualizujEmail() {
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (userId != null && !userId.isEmpty()) {
            // Utwórz referencję do dokumentu użytkownika w kolekcji "Użytkownicy" w Firestore
            DocumentReference userRef = firestore.collection("Użytkownicy").document(userId);

            // Pobierz aktualny adres e-mail z pola WpiszMaila
            nowyMail = WpiszMaila.getText().toString().trim();

            if(!nowyMail.isEmpty()&& nowyMail!=null) {
                // Usuń stare pole "email" z dokumentu użytkownika
                userRef.update("email", null)
                        .addOnSuccessListener(aVoid -> {
                            // Po pomyślnym usunięciu starego adresu e-mail, dodaj nowy
                            userRef.update("email", nowyMail)
                                    .addOnSuccessListener(aVoid1 -> {
                                        // Po pomyślnej aktualizacji, wyświetl komunikat sukcesu

                                    })
                                    .addOnFailureListener(e -> {
                                        // Obsłuż błąd podczas dodawania nowego adresu e-mail
                                        Toast.makeText(Edycja.this, "Błąd podczas dodawania nowego adresu e-mail", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Obsłuż błąd podczas usuwania starego adresu e-mail
                            Toast.makeText(Edycja.this, "Błąd podczas usuwania starego adresu e-mail", Toast.LENGTH_SHORT).show();
                        });
            }
            else
            {
                Toast.makeText(Edycja.this, "Nie wpisales adresu mail", Toast.LENGTH_SHORT).show();
            }
        }

        }
}
