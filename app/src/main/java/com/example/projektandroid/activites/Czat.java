package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.projektandroid.adapters.ChatAdapter;
import com.example.projektandroid.databinding.ActivityCzatBinding;
import com.example.projektandroid.models.ChatMessage;
import com.example.projektandroid.models.Uzytkownicy;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Czat extends AppCompatActivity {

    private ActivityCzatBinding binding;
    private Uzytkownicy reciverUser;
    private List<ChatMessage> chatMessage;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCzatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init()
    {
        preferenceManager=new PreferenceManager(getApplicationContext());
        chatMessage=new ArrayList<>();
        chatAdapter=new ChatAdapter(
                chatMessage,
                getBitmapFromEncodedString(reciverUser.zdjecie),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database=FirebaseFirestore.getInstance();
    }
    public void sendMessage()
    {
        HashMap<String,Object> message=new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,reciverUser.id);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionId!=null)
        {
            updateConversion(binding.inputMessage.getText().toString());
        }
        else
        {
            HashMap<String,Object> conversion=new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME,preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE,preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID,reciverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME,reciverUser.imie);
            conversion.put(Constants.KEY_RECEIVER_IMAGE,reciverUser.zdjecie);
            conversion.put(Constants.KEY_LAST_MESSAGE,binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP,new Date());
            addConversion(conversion);
        }
        binding.inputMessage.setText(null);
    }

    private void listenMessages()
    {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,reciverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,reciverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot>eventListener=(value,error) ->
    {
        if(error!=null)
        {
            return;
        }
        if(value!=null)
        {
            int count=chatMessage.size();
            for(DocumentChange documentChange:value.getDocumentChanges())
            {
                if(documentChange.getType()==DocumentChange.Type.ADDED)
                {
                    ChatMessage chatMessage1=new ChatMessage();
                    chatMessage1.senderId=documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage1.receiverId=documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage1.message=documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage1.dateTime=getReadableDataTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage1.dateObject=documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessage.add(chatMessage1);
                }
            }
            Collections.sort(chatMessage,(obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
            if(count==0)
            {
                chatAdapter.notifyDataSetChanged();
            }
            else
            {
                chatAdapter.notifyItemRangeInserted(chatMessage.size(),chatMessage.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessage.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);

        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversionId==null)
        {
            checkForConversion();
        }
    };


    private Bitmap getBitmapFromEncodedString(String encodedImage)
    {
        byte[] bytes= Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
    public void loadReceiverDetails()
    {
        reciverUser=(Uzytkownicy) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(reciverUser.imie);
    }
    public void setListeners()
    {
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.LayputSend.setOnClickListener(v->sendMessage());
    }
    private String getReadableDataTime(Date date)
    {
       return new SimpleDateFormat("MMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String,Object> conversion)
    {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId=documentReference.getId());
    }

    private void updateConversion(String message)
    {
        DocumentReference documentReference=
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE,message,
                Constants.KEY_TIMESTAMP,new Date()
        );
    }


    private void checkForConversion()
    {
        if(chatMessage.size()!=0)
        {
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    reciverUser.id
            );
            checkForConversionRemotely(
                    reciverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId,String receiverId)
    {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener=task ->
    {
        if(task.isSuccessful()&&task.getResult()!=null&&task.getResult().getDocuments().size()>0)
        {
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            conversionId= documentSnapshot.getId();
        }
    };
}