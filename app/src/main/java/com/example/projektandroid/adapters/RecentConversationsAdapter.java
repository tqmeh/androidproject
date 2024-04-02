package com.example.projektandroid.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projektandroid.databinding.ItemContainerRecentConversationBinding;
import com.example.projektandroid.listeners.ConversionListener;
import com.example.projektandroid.models.ChatMessage;
import com.example.projektandroid.models.Uzytkownicy;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{

private final List<ChatMessage> chatMessages;
private final ConversionListener conversionListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener=conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
    holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder
    {
        ItemContainerRecentConversationBinding binding;
       ConversionViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding)
       {
           super(itemContainerRecentConversationBinding.getRoot());
           binding=itemContainerRecentConversationBinding;
       }
       void setData(ChatMessage chatMessage)
       {
           binding.zjdecieProfilowe.setImageBitmap(getConversionImage(chatMessage.conversionImage));
           binding.textName.setText(chatMessage.conversionName);
           binding.textRecentMessage.setText(chatMessage.message);
           binding.getRoot().setOnClickListener(v->{
               Uzytkownicy uzytkownicy=new Uzytkownicy();
               uzytkownicy.id=chatMessage.conversiomId;
               uzytkownicy.imie=chatMessage.conversionName;
               uzytkownicy.zdjecie=chatMessage.conversionImage;
               conversionListener.onConversionClicked(uzytkownicy);
           });
       }
    }


    private Bitmap getConversionImage(String encodedImage)
    {
        byte[] bytes= Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
