package com.example.projektandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projektandroid.R;
import com.example.projektandroid.activites.DodajSocial;
import com.example.projektandroid.activites.Social;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.SocialViewHolder>{
    private final List<SocialData> socialDataList;
    private final Social social;
    private final Context context;
    String nazwadokumentu;
    public SocialAdapter(List<SocialData> socialDataList,Social social,Context context)
    {
        this.socialDataList = socialDataList;
        this.social=social;
        this.context=context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_social, parent, false);
        return new SocialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder holder, int position) {
        SocialData socialData = socialDataList.get(position);
        holder.bind(socialData);
    }

    @Override
    public int getItemCount() {
        return socialDataList.size();
    }

    public  class SocialViewHolder extends RecyclerView.ViewHolder {
        private final TextView textBaza;
        private final ImageView zdjecie;

        private final TextView textName;

        private final ImageView zjdecieProfilowe;
        private final TextView TextKciukiGora;

        private final ImageView KciukGora;
        private final TextView NumerPostu;
        private final ImageView KciukDol;
        private final TextView TextKciukiDol;
        private final ImageView Usun;
        private final ImageView Edytuj;





        public SocialViewHolder(@NonNull View itemView) {
            super(itemView);
            textBaza = itemView.findViewById(R.id.TextBaza);
            zdjecie = itemView.findViewById(R.id.zdjecie);
            textName=itemView.findViewById(R.id.textName1);
            zjdecieProfilowe=itemView.findViewById(R.id.zjdecieProfilowe);
            KciukGora=itemView.findViewById(R.id.KciukGora);
            TextKciukiGora=itemView.findViewById(R.id.TextKciukiGora);
            NumerPostu=itemView.findViewById(R.id.NumerPostu);
            KciukDol=itemView.findViewById(R.id.KciukDol);
            TextKciukiDol=itemView.findViewById(R.id.TextKciukiDol);
            Usun=itemView.findViewById(R.id.Usun);
            Edytuj=itemView.findViewById(R.id.Edytuj);


            KciukwGore();
            KciukiwDol();
            Usun();

            Edytuj();
        }


        public void KciukwGore()
        {
            KciukGora.setOnClickListener(v->{
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                CollectionReference social=db.collection("Social");
                String Szukanynumer=NumerPostu.getText().toString();
                Query query=social.whereEqualTo("Numer",Szukanynumer);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document:task.getResult())
                            if(task.isSuccessful())
                            {
                                String nazwadokumentu=document.getId();
                                System.out.println("Znalazlem numer nazwa dokumentu to "+nazwadokumentu);
                                if(document.contains("Polubienia"))
                                {
                                    String polubienia=document.getString("Polubienia");
                                    int polubieniaCalkowita=Integer.parseInt(polubienia);
                                    if (!czyUzytkownikPolubil(Szukanynumer)) {
                                        polubieniaCalkowita = polubieniaCalkowita + 1;
                                        System.out.println("Dodaje +1");
                                        ustawPolubienie(Szukanynumer);
                                    }
                                    else
                                    {
                                        polubieniaCalkowita=polubieniaCalkowita-1;
                                        System.out.println("Odejmuje -1");
                                        OdejmijPolubienie(Szukanynumer);
                                    }

                                    String aktualne=Integer.toString(polubieniaCalkowita);
                                    Map<String,Object> zaktualizowane=new HashMap<>();
                                    zaktualizowane.put("Polubienia",aktualne);

                                    social.document(nazwadokumentu).update(zaktualizowane)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    System.out.println("Aktualizacja polubień zakończona sukcesem");
                                                    social.document(nazwadokumentu).get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (documentSnapshot.exists()) {
                                                                        {
                                                                            String iloscLikow = documentSnapshot.getString("Polubienia");
                                                                            TextKciukiGora.setText(iloscLikow);
                                                                            Toast.makeText(context, "Dodales Like", Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }
                                                                }
                                                            });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    System.out.println("Błąd podczas aktualizacji polubień: ");
                                                }
                                            });

                                    System.out.println("Polubienia wynosza "+polubieniaCalkowita);

                                }
                            }
                            else
                            {
                                System.out.println("nie znalazlem numeru");
                            }
                    }
                });
            });
        }
        public void KciukiwDol()
        {
            KciukDol.setOnClickListener(v->{
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                CollectionReference social=db.collection("Social");
                String Szukanynumer=NumerPostu.getText().toString();
                Query query=social.whereEqualTo("Numer",Szukanynumer);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document:task.getResult())
                            if(task.isSuccessful())
                            {
                                String nazwadokumentu=document.getId();
                                System.out.println("Znalazlem numer nazwa dokumentu to "+nazwadokumentu);
                                if(document.contains("DisLike"))
                                {
                                    String dis=document.getString("DisLike");
                                    int disCalkowita=Integer.parseInt(dis);
                                    if (!czyUzytkownikniePolubil(Szukanynumer)) {
                                        disCalkowita=disCalkowita+1;
                                        System.out.println("Dodaje +1");
                                        ustawDislike(Szukanynumer);
                                    }
                                    else
                                    {
                                        disCalkowita=disCalkowita-1;
                                        System.out.println("Odejmuje -1");
                                        OdejmijDislike(Szukanynumer);
                                    }

                                    String aktualne=Integer.toString(disCalkowita);
                                    Map<String,Object> zaktualizowane=new HashMap<>();
                                    zaktualizowane.put("DisLike",aktualne);

                                    social.document(nazwadokumentu).update(zaktualizowane)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    System.out.println("Aktualizacja DiskLikow zakończona sukcesem");
                                                    social.document(nazwadokumentu).get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if(documentSnapshot.exists())
                                                                    {
                                                                        String iloscDisow=documentSnapshot.getString("DisLike");
                                                                        TextKciukiDol.setText(iloscDisow);
                                                                        Toast.makeText(context, "Dodales Dislika", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    System.out.println("Błąd podczas aktualizacji polubień: ");
                                                }
                                            });

                                    System.out.println("Polubienia wynosza "+disCalkowita);

                                }
                            }
                            else
                            {
                                System.out.println("nie znalazlem numeru");
                            }
                    }
                });
            });

        }
        public void Usun()
        {
            Usun.setOnClickListener(v->{
                System.out.println("usuwam");
                String NazwaUzytkownikaKtoryJestZarejestowany=social.getNazwaUzytkownika();
                System.out.println("Nazwa uzytkownika po wcisnieciu usun to "+NazwaUzytkownikaKtoryJestZarejestowany);
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                CollectionReference social=db.collection("Social");
                String Szukanynumer=NumerPostu.getText().toString();
                Query query=social.whereEqualTo("Numer",Szukanynumer);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document:task.getResult())
                        {
                            if(task.isSuccessful())
                            {
                                String nazwadokumentu=document.getId();
                                System.out.println("Znalazlem numer nazwa dokumentu to "+nazwadokumentu);
                                if(document.contains("Imie"))
                                {
                                    String ImiewBazie=document.getString("Imie");
                                    if(NazwaUzytkownikaKtoryJestZarejestowany.equals(ImiewBazie))
                                    {
                                        System.out.println("Imiona sa takie same");
                                        db.collection("Social").document(nazwadokumentu).delete()
                                                .addOnSuccessListener(v->
                                                {System.out.println("usunalem baze");
                                                    Toast.makeText(context, "usunales wpis", Toast.LENGTH_SHORT).show();
                                                    UsuniRysujJeszczeRaz(getAdapterPosition());
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Błąd usuwania", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "Nie jesteś wlascicielem tego postu nie możesz go usunąć", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }
                        }
                    }
                });


            });
        }
        public void Edytuj()
        {
            Edytuj.setOnClickListener(view -> {
                System.out.println("Zaczynamy edycje");
                String NazwaUzytkownikaKtoryJestZarejestowany=social.getNazwaUzytkownika();
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                CollectionReference social=db.collection("Social");
                String Szukanynumer=NumerPostu.getText().toString();
                Query query=social.whereEqualTo("Numer",Szukanynumer);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document:task.getResult())
                            if(task.isSuccessful())
                            {
                                if(document.contains("Imie"))
                                {
                                    String ImiewBazie = document.getString("Imie");
                                    if(NazwaUzytkownikaKtoryJestZarejestowany.equals(ImiewBazie))
                                    {
                                        Toast.makeText(context, "Możesz edytować", Toast.LENGTH_SHORT).show();
                                        nazwadokumentu=document.getId();
                                        System.out.println("Znalazlem numer nazwa dokumentu to "+nazwadokumentu);

                                        //Pobieram aktualne zdjecie
                                        byte[] zdjecieBytes = Base64.decode(socialDataList.get(getAdapterPosition()).getImages(), Base64.DEFAULT);
                                        Bitmap zdjecieBitmap = BitmapFactory.decodeByteArray(zdjecieBytes, 0, zdjecieBytes.length);

                                        //Przekazuje moje zdjecie
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        zdjecieBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();
                                        Intent intent=new Intent(context, DodajSocial.class);
                                        intent.putExtra("Text",textBaza.getText().toString());
                                        intent.putExtra("NumerDokumentu",nazwadokumentu);
                                        intent.putExtra("Zdjecie", byteArray);
                                        context.startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "nie jesteś wlaścicielem posta, nie możesz edytować", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }

                            else
                            {
                                System.out.println("nie znalazlem numeru");
                            }
                    }
                });
            });




        }
        public void bind(SocialData socialData) {
            textBaza.setText(socialData.getText());
            textName.setText(socialData.getNazwa());
            NumerPostu.setText(socialData.getNumerPostu());
            TextKciukiGora.setText(socialData.getPolubienia());
            TextKciukiDol.setText(socialData.getDisLike());
            // Dekoduj Base64 String do tablicy bajtów
            byte[] imageBytes = Base64.decode(socialData.getImages(), Base64.DEFAULT);

            // Konwertuj tablicę bajtów na Bitmap
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // Ustaw Bitmap w ImageView
            zdjecie.setImageBitmap(imageBitmap);

            byte[] zdjecie=Base64.decode(socialData.getZdjecieProfilowe(),Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(zdjecie,0,zdjecie.length);

            zjdecieProfilowe.setImageBitmap(bitmap);
        }
    }
    public void UsuniRysujJeszczeRaz(int position)
    {
        socialDataList.remove(position);
        notifyItemRemoved(position);
    }
    public String getNazwadokumentu() {
        return nazwadokumentu;
    }
    private void ustawPolubienie(String numerPostu) {
        SharedPreferences preferences = context.getSharedPreferences("Polubienia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(numerPostu, true);
        editor.apply();
    }
    private void ustawDislike(String numerPostu) {
        SharedPreferences preferences = context.getSharedPreferences("DisLike", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(numerPostu, true);
        editor.apply();
    }
    private void OdejmijDislike(String numerPostu) {
        SharedPreferences preferences = context.getSharedPreferences("DisLike", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(numerPostu, false);
        editor.apply();
    }
    private void OdejmijPolubienie(String numerPostu) {
        SharedPreferences preferences = context.getSharedPreferences("Polubienia", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(numerPostu, false);
        editor.apply();
    }
    private boolean czyUzytkownikPolubil(String numerPostu) {
        SharedPreferences preferences = context.getSharedPreferences("Polubienia", Context.MODE_PRIVATE);
        return preferences.getBoolean(numerPostu, false);
    }
    private boolean czyUzytkownikniePolubil(String numerPostu) {
        SharedPreferences preferences = context.getSharedPreferences("DisLike", Context.MODE_PRIVATE);
        return preferences.getBoolean(numerPostu, false);
    }
}
