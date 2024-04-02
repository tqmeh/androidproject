package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.projektandroid.adapters.UsersAdapter;
import com.example.projektandroid.databinding.ActivityUserBinding;
import com.example.projektandroid.listeners.UserListener;
import com.example.projektandroid.models.Uzytkownicy;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements UserListener {

    private ActivityUserBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        setListeners();
        getUser();
    }
    private void setListeners()
    {
        binding.imageBack.setOnClickListener(v->onBackPressed());
    }
    private void getUser()
    {
       Ladowanie(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    Ladowanie(false);
                    String currentUserId=preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful()&&task.getResult()!=null)
                    {
                        List<Uzytkownicy> users=new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult())
                        {
                            if(currentUserId.equals(queryDocumentSnapshot.getId()))
                            {
                                continue;
                            }
                            Uzytkownicy user=new Uzytkownicy();
                            user.imie=queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email=queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.zdjecie=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id=queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size()>0)
                        {
                            UsersAdapter usersAdapter=new UsersAdapter(users,this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Wyswietlblad();
                        }

                    }
                    else
                    {
                        Wyswietlblad();
                    }
                });
    }
    private void Wyswietlblad()
    {
        binding.textErrorMessage.setText(String.format("%s","Brak aktywanego użytkownika"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void Ladowanie(Boolean isLoading)
    {
        if(isLoading)
        {
        binding.progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(Uzytkownicy uzytkownicy) {
        Intent intent=new Intent(getApplicationContext(),Czat.class);
        intent.putExtra(Constants.KEY_USER, uzytkownicy);
        startActivity(intent);
        finish();
    }
}