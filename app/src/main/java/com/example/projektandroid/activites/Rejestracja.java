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
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.projektandroid.databinding.ActivityRejestracjaBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Rejestracja extends AppCompatActivity {

    private ActivityRejestracjaBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRejestracjaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        setListeners();
    }
    private void setListeners()
    {

        binding.Zaloguj.setOnClickListener(v -> onBackPressed());
        binding.przyciskRejestruj.setOnClickListener(v ->{
                if(SprawdzRejestracje())
                {
                SprawdzCzyIsntnieje();
                }
    });
        binding.layoutImage.setOnClickListener(v->{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
    }

    private void showToast(String wiadomosc)
    {
        Toast.makeText(getApplicationContext(),wiadomosc,Toast.LENGTH_SHORT).show();

    }
    private void Rejestruj()
    {
        ladowanie(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        HashMap<String,Object> user=new HashMap<>();
        user.put(Constants.KEY_NAME,binding.wpiszImie.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.WpiszMaila.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.WpiszHaslo.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    ladowanie(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME,binding.wpiszImie.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                })
                .addOnFailureListener(exception -> {
                ladowanie(false);
                showToast(exception.getMessage());

                });

    }
    private Boolean SprawdzRejestracje()
    {
        if(encodedImage==null)
        {
            showToast("Wybierz zdjęcie profilowe");
            return false;

        }
        else if (binding.wpiszImie.getText().toString().trim().isEmpty())
        {
            showToast("Wpisz imie");
            return  false;
        }
        else if(binding.WpiszMaila.getText().toString().trim().isEmpty())
        {
            showToast("Wpisz maila");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.WpiszMaila.getText().toString()).matches())
        {
            showToast("Email niepoprawny, wpisz ponownie");
            return false;
        }
        else if (binding.WpiszHaslo.getText().toString().trim().isEmpty())
        {
            showToast("Wpisz hasło");
            return false;
        }
        else if (binding.powtorzHaslo.getText().toString().trim().isEmpty())
        {
            showToast("Wpisz ponownie hasło");
            return false;
        }
        else if (!binding.WpiszHaslo.getText().toString().equals(binding.powtorzHaslo.getText().toString()))
        {
        showToast("Hasla nie sa takie same !");
        return false;
        }
        else
        {
            return true;
        }


    }
    public void SprawdzCzyIsntnieje()
    {
        String Imie=binding.wpiszImie.getText().toString().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference rejestracja = db.collection(Constants.KEY_COLLECTION_USERS);
        Query query=rejestracja.whereEqualTo(Constants.KEY_NAME,Imie);
        query.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot=task.getResult();
            if(querySnapshot!=null&&!querySnapshot.isEmpty())
            {
                Toast.makeText(Rejestracja.this, "Użytkownik o takim nicku juz istnieje", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Rejestruj();
            }
        });
    }
    private String getEncodedImage(Bitmap bitmap)
    {
        int previewWidth=150;
        int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte [] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
             if(result.getResultCode()==RESULT_OK)
             {
                 if(result.getData()!=null)
                 {
                     Uri imageUri=result.getData().getData();
                     try {
                         InputStream inputStream=getContentResolver().openInputStream(imageUri);
                         Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                         binding.zjdecieProfilowe.setImageBitmap(bitmap);
                         binding.textAddImage.setVisibility(View.GONE);
                         encodedImage=getEncodedImage(bitmap);


                     }
                     catch (FileNotFoundException e)
                     {
                         e.printStackTrace();
                     }
                 }
             }
            }
    );
    private void ladowanie(Boolean isLoading)
    {
        if(isLoading)
        {
            binding.przyciskRejestruj.setVisibility(View.INVISIBLE);
            binding.progrssBar.setVisibility(View.VISIBLE);

        }
        else
        {
            binding.progrssBar.setVisibility(View.INVISIBLE);
            binding.przyciskRejestruj.setVisibility(View.VISIBLE);
        }
    }
}