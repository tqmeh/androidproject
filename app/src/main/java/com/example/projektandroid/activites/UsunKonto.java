package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.databinding.ActivityUsunKontoBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class UsunKonto extends AppCompatActivity {

    private ActivityUsunKontoBinding binding;
    private PreferenceManager preferenceManager;
    EditText WpiszImie,WpiszMaila,WpiszHaslo,WpiszPowtorzHaslo;
    String Imie,Mail,Haslo,PowtorzHaslo,NazwaUzytkownikazBazy;
    Button ButtonUsuń;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUsunKontoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        binding.imageBack.setOnClickListener(v-> {
            Intent intent=new Intent(UsunKonto.this,Edycja.class);
            startActivity(intent);
        });
        LadowanieUzytkownikaDanych();
        WpiszImie=findViewById(R.id.WpiszImie);
        WpiszMaila=findViewById(R.id.WpiszMaila);
        WpiszHaslo=findViewById(R.id.WpiszHaslo);
        WpiszPowtorzHaslo=findViewById(R.id.WpiszPowtorzHaslo);
        ButtonUsuń=findViewById(R.id.ButtonUsuń);

        ButtonUsuń.setOnClickListener(v->{
            Pobierz();
            SprawdzDane();

        });
    }


    public void Pobierz() {
        Imie = WpiszImie.getText().toString().trim();
        Mail = WpiszMaila.getText().toString().trim();
        Haslo = WpiszHaslo.getText().toString().trim();
        PowtorzHaslo = WpiszPowtorzHaslo.getText().toString().trim();

    }
    public void SprawdzDane()
    {
        if(Imie.isEmpty())
        {
            Toast.makeText(UsunKonto.this, "Nie wpisałeś nicku ", Toast.LENGTH_SHORT).show();
        }
        else if(Mail.isEmpty())
        {
            Toast.makeText(UsunKonto.this, "Nie wpisałeś maila ", Toast.LENGTH_SHORT).show();
        }
        else if(Haslo.isEmpty()||PowtorzHaslo.isEmpty())
        {
            Toast.makeText(UsunKonto.this, "nie wpisałeś hasła", Toast.LENGTH_SHORT).show();
        }
        else if(!Haslo.equals(PowtorzHaslo))
        {
            Toast.makeText(UsunKonto.this, "Hasla są różne ", Toast.LENGTH_SHORT).show();
        }


        else if(!Imie.isEmpty()&&Haslo.equals(PowtorzHaslo)&&!Mail.isEmpty())
        {
            Usun();
        }

    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);
        NazwaUzytkownikazBazy=preferenceManager.getString(Constants.KEY_NAME);

    }
    public void Usun() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference Usun = db.collection(Constants.KEY_COLLECTION_USERS);
        Query query = Usun.whereEqualTo("Imie", Imie);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Pobierz pierwszy dokument z wyników
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String documentId = document.getId();
                    String SprawdzImie=document.getString(Constants.KEY_NAME);
                    System.out.println("Imie to "+SprawdzImie);
                    if(SprawdzImie.equals(NazwaUzytkownikazBazy))
                    {
                        System.out.println("Imiona zgodne");
                        String SprawdzMaila=document.getString(Constants.KEY_EMAIL);
                        if(SprawdzMaila.equals(Mail))
                        {
                            String SprawdzHaslo=document.getString(Constants.KEY_PASSWORD);
                            if(SprawdzHaslo.equals(Haslo))
                            {
                                signOut();
                                db.collection(Constants.KEY_COLLECTION_USERS).document(documentId).delete();
                                Toast.makeText(UsunKonto.this, "Konto usunięte ", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(UsunKonto.this, "Podaleś złe hasło ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(UsunKonto.this, "Podaleś zły mail ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(UsunKonto.this, "Podaleś zły nick ", Toast.LENGTH_SHORT).show();
                    }
                    System.out.println("Numer dokumentu to " + documentId);


                }
            }
        });
    }
    private void signOut()
    {

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
                .addOnFailureListener(e->{
                    Toast.makeText(UsunKonto.this, "Nie udalo sie wylogowac ", Toast.LENGTH_SHORT).show();
                });
    }
}