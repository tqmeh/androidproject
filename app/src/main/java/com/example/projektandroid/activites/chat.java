package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.adapters.RecentConversationsAdapter;
import com.example.projektandroid.databinding.ActivityChatBinding;
import com.example.projektandroid.databinding.ActivityMainBinding;
import com.example.projektandroid.listeners.ConversionListener;
import com.example.projektandroid.models.ChatMessage;
import com.example.projektandroid.models.Uzytkownicy;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class chat extends AppCompatActivity implements ConversionListener {
    private ActivityChatBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager=new PreferenceManager(getApplicationContext());
        init();
        LadowanieUzytkownikaDanych();
        getToken();
        setListeners();
        listenConversations();
        AppCompatImageView imageBack = (AppCompatImageView) findViewById(R.id.imageBack);

        // Dodaj obsługę kliknięcia
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Wywołaj funkcję cofająca (na przykład finish() dla Activity)
                finish();
            }
        });
    }
    private void init()
    {
        conversations=new ArrayList<>();
        conversationsAdapter=new RecentConversationsAdapter(conversations,this);
        binding.conversationRecyclerView.setAdapter(conversationsAdapter);
        database=FirebaseFirestore.getInstance();
    }

    private void setListeners()
    {
        binding.imageSignOut.setOnClickListener(v->signOut());
        binding.fabNewChat.setOnClickListener(v->startActivity(new Intent(getApplicationContext(), UserActivity.class)));
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

    private void listenConversations()
    {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener=(value,error )->
    {
        if(error!=null)
        {
            return;
        }
        if(value!=null)
        {
            for(DocumentChange documentChange:value.getDocumentChanges())
            {
                if(documentChange.getType()==DocumentChange.Type.ADDED)
                {
                    String senderId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.senderId=senderId;
                    chatMessage.receiverId=receiverId;
                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId))
                    {
                        chatMessage.conversionImage=documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName=documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversiomId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }
                    else
                    {
                        chatMessage.conversionImage=documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName=documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversiomId=documentChange.getDocument().getString((Constants.KEY_SENDER_ID));
                    }
                    chatMessage.message=documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject=documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }
                else if (documentChange.getType()==DocumentChange.Type.MODIFIED)
                {
                    for(int i=0;i<conversations.size();i++)
                    {
                        String senderId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversations.get(i).senderId.equals(senderId)&&conversations.get(i).receiverId.equals(receiverId))
                        {
                            conversations.get(i).message=documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject=documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations,(obj1,obj2)->obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationRecyclerView.smoothScrollToPosition(0);
            binding.conversationRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

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

    @Override
    public void onConversionClicked(Uzytkownicy uzytkownicy) {
        Intent intent=new Intent(getApplicationContext(),Czat.class);
        intent.putExtra(Constants.KEY_USER,uzytkownicy);
        startActivity(intent);
    }
}