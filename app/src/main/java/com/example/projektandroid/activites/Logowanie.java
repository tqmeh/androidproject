package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.databinding.ActivityLogowanieBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Logowanie extends AppCompatActivity {

    private ActivityLogowanieBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager=new PreferenceManager(getApplicationContext());

      // pozostan zalogowanym !!!
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
        {
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }




        binding=ActivityLogowanieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }
    private void setListeners()
    {
        binding.StworzNoweKonto.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), Rejestracja.class)));
        binding.przyciskZaloguj.setOnClickListener(v->{
            if(isValidSignInDetails())
            {
                Zaloguj();
            }
        });
        //binding.przyciskZaloguj.setOnClickListener(e -> DodajdoBazy());
    }
   /* public void DodajdoBazy()
    {
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        HashMap<String, Object> data=new HashMap<>();
        data.put("imie","Tomasz");
        data.put("nazwisko","Górnik");
        database.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(),"Data INserted",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    */
    private void Zaloguj()
    {
    ladowanie(true);
    FirebaseFirestore database=FirebaseFirestore.getInstance();
    database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL,binding.WpiszMaila.getText().toString())
            .whereEqualTo(Constants.KEY_PASSWORD,binding.WpiszHaslo.getText().toString())
            .get()
            .addOnCompleteListener(task->{
              if(task.isSuccessful()&&task.getResult()!=null &&task.getResult().getDocuments().size()>0)
              {
                  DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                  preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                  preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                  preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                  preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                  Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(intent);
              }
              else
              {
                  ladowanie(false);
                  showToast("Nie można się zalogowąć email bądź hasło niepoprawne");
              }
            });
    }
    private void ladowanie(Boolean isLoading)
    {
        if(isLoading)
        {
            binding.przyciskZaloguj.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
           binding.progressBar.setVisibility(View.INVISIBLE);
           binding.przyciskZaloguj.setVisibility(View.VISIBLE);
        }
    }
   private void showToast(String wiadomosc)
   {
       Toast.makeText(getApplicationContext(),wiadomosc,Toast.LENGTH_SHORT).show();

   }
   private Boolean isValidSignInDetails()
   {
       if(binding.WpiszMaila.getText().toString().trim().isEmpty())
       {
           showToast("Wpisz maila");
           return false;
       }
       else if (!Patterns.EMAIL_ADDRESS.matcher(binding.WpiszMaila.getText().toString()).matches())
       {

               showToast("Wprowadź prawidłowy adres email");
               return false;
       }
       else if (binding.WpiszHaslo.getText().toString().trim().isEmpty())
       {
        showToast("Wpisz hasło");
        return false;
       }
       else
       {
           return true;
       }


   }

}
