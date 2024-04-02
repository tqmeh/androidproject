package com.example.projektandroid.activites;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.adapters.SocialAdapter;
import com.example.projektandroid.databinding.ActivityDodajSocialBinding;
import com.example.projektandroid.databinding.ActivitySocialBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class DodajSocial extends AppCompatActivity {
    private ActivityDodajSocialBinding binding;
    private PreferenceManager preferenceManager;
    String encodedImage,zdekodowane;
    Bitmap wybraneZdjecie;
    Long numer;
    String tekstSocial;
    String numerDokumentu;
    boolean Sprawdz;

    private CountDownLatch latch = new CountDownLatch(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDodajSocialBinding.inflate(getLayoutInflater());
        preferenceManager=new PreferenceManager(getApplicationContext());
        LadowanieUzytkownikaDanych();
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Text")&&intent.hasExtra("NumerDokumentu")&&intent.hasExtra("Zdjecie"))
        {

             tekstSocial = intent.getStringExtra("Text");

             numerDokumentu=intent.getStringExtra("NumerDokumentu");
            byte[] zdjecieByteArray = intent.getByteArrayExtra("Zdjecie");
            wybraneZdjecie = BitmapFactory.decodeByteArray(zdjecieByteArray, 0, zdjecieByteArray.length);

            binding.DodajZdjecie.setImageBitmap(wybraneZdjecie);
            System.out.println("Pobrany tekst to :" +tekstSocial);
            binding.inputMessage.setText(tekstSocial);
            binding.DodajZdjecie.setOnClickListener(v -> openGallery());
            binding.wyslij.setOnClickListener(v->{

                EdytujWiadomosc();


            });

        }
        else {

            Listener();
            PobierzNumer();
        }
    }

    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();
        zdekodowane = Base64.encodeToString(imageData, Base64.DEFAULT);

    }
    private void Listener()
    {
        binding.DodajZdjecie.setOnClickListener(v -> openGallery());
        binding.wyslij.setOnClickListener(v->{

                DodajWiadomosc();


        });


    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
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

                         wybraneZdjecie= BitmapFactory.decodeStream(inputStream);

                        ImageView zdjecie=findViewById(R.id.DodajZdjecie);
                        zdjecie.setImageBitmap(wybraneZdjecie);



                    } catch (FileNotFoundException e) {
                        // Obsłuż błąd, jeśli plik nie zostanie znaleziony
                        e.printStackTrace();
                    }
                }
            }
    );
    public void EdytujWiadomosc()
    {

            ZapiszZdjecie(wybraneZdjecie);
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            Map<String,Object> update=new HashMap<>();
            update.put(Constants.KEY_TEXT_SOCIAL,binding.inputMessage.getText().toString());
            update.put(Constants.KEY_IMAGE_SOCIAL,encodedImage);
            db.collection("Social")
                    .document(numerDokumentu)
                    .update(update)
                    .addOnSuccessListener(v->{
                        Toast.makeText(DodajSocial.this, "Zaktualizowano wpis", Toast.LENGTH_SHORT).show();
                        Intent DodajSocial=new Intent(com.example.projektandroid.activites.DodajSocial.this,Social.class);
                        startActivity(DodajSocial);
                        finish();
                    })
                    .addOnFailureListener(v->{
                        Toast.makeText(DodajSocial.this, "Błąd podczas aktualizacji", Toast.LENGTH_SHORT).show();
                    });
            System.out.println("Powinno to zadzialac");
            System.out.println("numer dokumentu w klasie DodajSocial to "+numerDokumentu);
            Sprawdz=false;

    }
    public void DodajWiadomosc()
    {


            ZapiszZdjecie(wybraneZdjecie);

            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String, Object> user = new HashMap<>();
            user.put(Constants.KEY_TEXT_SOCIAL, binding.inputMessage.getText().toString());
            user.put(Constants.KEY_IMAGE_SOCIAL, encodedImage);
            user.put(Constants.KEY_NAME, binding.textName.getText().toString());
            user.put(Constants.KEY_IMAGE, zdekodowane);
            user.put(Constants.KEY_Liczba, String.valueOf(numer));
            user.put(Constants.KEY_LICZBA_LIKOW, "0");
            user.put(Constants.KEY_LICZBA_DISLIKOW, "0");
            database.collection(Constants.KEY_COLLECTION_SOCIAL)
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        preferenceManager.putString(Constants.KEY_TEXT_SOCIAL, binding.inputMessage.getText().toString());
                        preferenceManager.putString(Constants.KEY_IMAGE_SOCIAL, encodedImage);
                        preferenceManager.putString(Constants.KEY_NAME, binding.textName.getText().toString());
                        preferenceManager.putString(Constants.KEY_IMAGE, zdekodowane);
                        preferenceManager.putString(Constants.KEY_Liczba, String.valueOf(numer));
                        preferenceManager.putString(Constants.KEY_LICZBA_LIKOW, "0");
                        preferenceManager.putString(Constants.KEY_LICZBA_DISLIKOW, "0");
                        Toast.makeText(DodajSocial.this, "Post zostal dodany", Toast.LENGTH_SHORT).show();
                        Intent DodajSocial=new Intent(com.example.projektandroid.activites.DodajSocial.this,Social.class);
                        startActivity(DodajSocial);
                        finish();

                    })
                    .addOnFailureListener(exception -> {
                        Toast.makeText(DodajSocial.this, "Błąd wysyłania", Toast.LENGTH_SHORT).show();
                    });
        }



    public void ZapiszZdjecie(Bitmap bitmap) {
        // Konwertuj bitmapę na tablicę bajtów bez kompresji
        ByteArrayOutputStream originalBaos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, originalBaos);
        byte[] originalImageData = originalBaos.toByteArray();

        // Sprawdź rozmiar oryginalnej tablicy bajtów
        if (originalImageData.length > 1020) {
            // Obraz jest za duży, więc dostosuj rozmiar

            // Maksymalna jakość kompresji (100)
            int quality = 100;

            // Iteracyjnie zmniejszaj jakość obrazu, aż osiągnie odpowiedni rozmiar
            while (originalImageData.length > 1020 && quality > 0) {
                // Utwórz nową bitmapę z aktualną jakością
                ByteArrayOutputStream resizedBaos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, resizedBaos);
                byte[] resizedImageData = resizedBaos.toByteArray();

                // Zapisz zakodowany obraz
                encodedImage = Base64.encodeToString(resizedImageData, Base64.DEFAULT);

                // Aktualizuj jakość dla następnej iteracji
                quality -= 10;
            }
        } else {
            // Oryginalny obraz mieści się w limicie, więc zapisz go bez zmian
            encodedImage = Base64.encodeToString(originalImageData, Base64.DEFAULT);
        }
    }
public void PobierzNumer()
{
    FirebaseFirestore database=FirebaseFirestore.getInstance();
    database.collection("Numer").document("Pozycja")
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Pobierz wartość pola "Numer" z dokumentu
                        numer = documentSnapshot.getLong("Numer");

                        if (numer != null) {
                            System.out.println("Wartość pola 'Numer': " + numer);

                            DodajNumer();

                        } else {
                            System.out.println("Pole 'Numer' nie istnieje w dokumencie.");

                        }
                    } else {
                        System.out.println("Dokument 'Pozycja' nie istnieje w kolekcji 'numer'.");

                    }
                }
            });

}
public void DodajNumer()
{
    try {
        Thread.sleep(2000);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Podaj ścieżkę do dokumentu "Pozycja" w kolekcji "Numer"
        String kolekcja = "Numer";
        String dokument = "Pozycja";

        Long sprawdz=numer;

        // Utwórz mapę, aby zaktualizować pole "numer" na wartość 2
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("Numer", sprawdz+1);

        // Zaktualizuj dokument w bazie danych
        database.collection(kolekcja).document(dokument)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Zaktualizowano pomyślnie
                    System.out.println("Zaktualizowano pomyślnie");
                })
                .addOnFailureListener(e -> {
                    // Błąd podczas aktualizacji
                    System.out.println("Błąd podczas aktualizacji: " + e.getMessage());
                });
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }

}

}