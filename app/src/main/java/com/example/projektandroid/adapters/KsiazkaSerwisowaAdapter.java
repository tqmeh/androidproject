package com.example.projektandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projektandroid.R;
import com.example.projektandroid.activites.DodajKsiazkaSerwisowa;
import com.example.projektandroid.activites.KsiazkaSerwisowa;
import com.example.projektandroid.utillites.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class KsiazkaSerwisowaAdapter extends RecyclerView.Adapter<KsiazkaSerwisowaAdapter.ViewHolder> {

    private final Context context;
    private final List<KsiazkaSerwisowaData> ksiazkaSerwisowaDataList;

    private final KsiazkaSerwisowa ksiazkaSerwisowa;

    public KsiazkaSerwisowaAdapter(List<KsiazkaSerwisowaData> ksiazkaSerwisowaDataList,KsiazkaSerwisowa ksiazkaSerwisowa,Context context)
    {
        this.ksiazkaSerwisowaDataList=ksiazkaSerwisowaDataList;
        this.ksiazkaSerwisowa=ksiazkaSerwisowa;
        this.context=context;
    }


    @NonNull
    @Override
    public KsiazkaSerwisowaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ksiazka_serwisowa, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull KsiazkaSerwisowaAdapter.ViewHolder holder, int position) {
        KsiazkaSerwisowaData ksiazkaSerwisowaData=ksiazkaSerwisowaDataList.get(position);
        holder.bind(ksiazkaSerwisowaData);


    }

    @Override
    public int getItemCount() {
        return ksiazkaSerwisowaDataList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        Button PrzyciskNazwa;
        TextView textViewData,textViewCena;
        Switch switchOlej,switchKlocki,switchTarczeHamulcowe,switchFiltry,switchOponaPrzod,
                switchOponaTyl,switchAkumulator,switchPlynHamulcowy,switchPrzeglad,
        switchInne;

        ImageView imageViewEdytuj,imageViewUsun;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            PrzyciskNazwa=itemView.findViewById(R.id.PrzyciskNazwa);
            textViewData=itemView.findViewById(R.id.textViewData);
            switchOlej=itemView.findViewById(R.id.switchOlej);
            switchKlocki=itemView.findViewById(R.id.switchKlocki);
            switchTarczeHamulcowe=itemView.findViewById(R.id.switchTarczeHamulcowe);
            switchFiltry=itemView.findViewById(R.id.switchFiltry);
            switchOponaPrzod=itemView.findViewById(R.id.switchOponaPrzod);
            switchOponaTyl=itemView.findViewById(R.id.switchOponaTyl);
            switchAkumulator=itemView.findViewById(R.id.switchAkumulator);
            switchPlynHamulcowy=itemView.findViewById(R.id.switchPlynHamulcowy);
            switchPrzeglad=itemView.findViewById(R.id.switchPrzeglad);
            switchInne=itemView.findViewById(R.id.switchInne);
            textViewCena=itemView.findViewById(R.id.textViewCena);
            imageViewEdytuj=itemView.findViewById(R.id.imageViewEdytuj);
            imageViewUsun=itemView.findViewById(R.id.imageViewUsun);
            Ukryj();
            PrzyciskNazwa.setOnClickListener(view -> {
                if(textViewData.getVisibility()==View.VISIBLE)
                {
                    Ukryj();
                }
                else
                {
                    Pokaz();
                }
            });
            Edytuj();
            Usun();
        }
        public void bind(KsiazkaSerwisowaData ksiazkaSerwisowaData)
        {
            PrzyciskNazwa.setText(ksiazkaSerwisowaData.getNazwa());
            textViewData.setText("Data   "+ksiazkaSerwisowaData.getData());
            textViewCena.setText("Cena   "+ksiazkaSerwisowaData.getCena());
           Switche(ksiazkaSerwisowaData);
        }
        public void Switche(KsiazkaSerwisowaData ksiazkaSerwisowaData)
        {
            switchOlej.setText("Olej");
            switchOlej.setChecked(ksiazkaSerwisowaData.isOlej());
            switchOlej.setEnabled(false);

            switchKlocki.setText("Klocki");
            switchKlocki.setChecked(ksiazkaSerwisowaData.isKlocki());
            switchKlocki.setEnabled(false);

            switchTarczeHamulcowe.setText("Tarcze hamulcowe");
            switchTarczeHamulcowe.setChecked(ksiazkaSerwisowaData.isTarczeHamulcowe());
            switchTarczeHamulcowe.setEnabled(false);

            switchFiltry.setText("Filtry");
            switchFiltry.setChecked(ksiazkaSerwisowaData.isFiltry());
            switchFiltry.setEnabled(false);

            switchOponaPrzod.setText("Opona przód");
            switchOponaPrzod.setChecked(ksiazkaSerwisowaData.isOponaPrzod());
            switchOponaPrzod.setEnabled(false);

            switchOponaTyl.setText("Opona tył");
            switchOponaTyl.setChecked(ksiazkaSerwisowaData.isOponaTyl());
            switchOponaTyl.setEnabled(false);

            switchAkumulator.setText("Akumulator");
            switchAkumulator.setChecked(ksiazkaSerwisowaData.isAkumulator());
            switchAkumulator.setEnabled(false);

            switchPlynHamulcowy.setText("Płyn hamulcowy");
            switchPlynHamulcowy.setChecked(ksiazkaSerwisowaData.isPlynHamulcowy());
            switchPlynHamulcowy.setEnabled(false);

            switchPrzeglad.setText("Przeglad");
            switchPrzeglad.setChecked(ksiazkaSerwisowaData.isPrzeglad());
            switchPrzeglad.setEnabled(false);

            switchInne.setText("Inne");
            switchInne.setChecked(ksiazkaSerwisowaData.isInne());
            switchInne.setEnabled(false);

        }
        public void Ukryj()
        {
            textViewData.setVisibility(View.GONE);
            switchOlej.setVisibility(View.GONE);
            switchKlocki.setVisibility(View.GONE);
            switchTarczeHamulcowe.setVisibility(View.GONE);
            switchFiltry.setVisibility(View.GONE);
            switchOponaPrzod.setVisibility(View.GONE);
            switchOponaTyl.setVisibility(View.GONE);
            switchAkumulator.setVisibility(View.GONE);
            switchPlynHamulcowy.setVisibility(View.GONE);
            switchPrzeglad.setVisibility(View.GONE);
            switchInne.setVisibility(View.GONE);
            textViewCena.setVisibility(View.GONE);
            imageViewEdytuj.setVisibility(View.GONE);
            imageViewUsun.setVisibility(View.GONE);
        }
        public void Pokaz()
        {
            textViewData.setVisibility(View.VISIBLE);
            switchOlej.setVisibility(View.VISIBLE);
            switchKlocki.setVisibility(View.VISIBLE);
            switchTarczeHamulcowe.setVisibility(View.VISIBLE);
            switchFiltry.setVisibility(View.VISIBLE);
            switchOponaPrzod.setVisibility(View.VISIBLE);
            switchOponaTyl.setVisibility(View.VISIBLE);
            switchAkumulator.setVisibility(View.VISIBLE);
            switchPlynHamulcowy.setVisibility(View.VISIBLE);
            switchPrzeglad.setVisibility(View.VISIBLE);
            switchInne.setVisibility(View.VISIBLE);
            textViewCena.setVisibility(View.VISIBLE);
            imageViewEdytuj.setVisibility(View.VISIBLE);
            imageViewUsun.setVisibility(View.VISIBLE);
        }
        public void Edytuj()
        {
            imageViewEdytuj.setOnClickListener(v->{
                String nazwaUzytkownika=ksiazkaSerwisowa.getNazwaUzytkownika();
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                CollectionReference ksiazka=db.collection(Constants.KEY_COLLECTION_KSIAZKA);
                String nazwaKsiazki=PrzyciskNazwa.getText().toString().trim();
                Query query=ksiazka.whereEqualTo(Constants.KEY_NAZWA,nazwaKsiazki);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document:task.getResult())
                        {
                            if(task.isSuccessful())
                            {
                                if(document.contains(Constants.KEY_NAME))
                                {
                                    String NazwaDokumentu=document.getId();
                                    System.out.println("Nazwa dokumentu to "+NazwaDokumentu);
                                    String Data=document.getString(Constants.KEY_DATA);
                                    Boolean Olej=document.getBoolean(Constants.KEY_OLEJ);
                                    Boolean Klocki=document.getBoolean(Constants.KEY_KLOCKI);
                                    Boolean TarczeHamulcowe=document.getBoolean(Constants.KEY_TarczeHamulcowe);
                                    Boolean Filtry=document.getBoolean(Constants.KEY_FILTRY);
                                    Boolean OponaPrzod=document.getBoolean(Constants.KEY_OPONA_PRZOD);
                                    Boolean OponaTyl=document.getBoolean(Constants.KEY_OPONA_TYL);
                                    Boolean Akumulator=document.getBoolean(Constants.KEY_AKUMULATOR);
                                    Boolean PlynHamulcowy=document.getBoolean(Constants.KEY_PLYN_HAMULCOWY);
                                    Boolean Przeglad=document.getBoolean(Constants.KEY_PRZEGLAD);
                                    Boolean Inne=document.getBoolean(Constants.KEY_INNE);
                                    String Cena=document.getString(Constants.KEY_CENA);
                                    String ImieWBazie=document.getString(Constants.KEY_NAME);
                                    String nazwa=document.getString(Constants.KEY_NAZWA);
                                    if(nazwaUzytkownika.equals(ImieWBazie)&&nazwaKsiazki.equals(nazwa))
                                    {
                                        Toast.makeText(context, "Możesz edytować", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(context, DodajKsiazkaSerwisowa.class);
                                        intent.putExtra("WpiszDate",Data);
                                        intent.putExtra("Nazwa",nazwa);
                                        intent.putExtra("Olej",Olej);
                                        intent.putExtra("Klocki",Klocki);
                                        intent.putExtra("Tarcze",TarczeHamulcowe);
                                        intent.putExtra("Filtry",Filtry);
                                        intent.putExtra("OponaPrzod",OponaPrzod);
                                        intent.putExtra("OponaTyl",OponaTyl);
                                        intent.putExtra("Akumulator",Akumulator);
                                        intent.putExtra("PlynHamulcowy",PlynHamulcowy);
                                        intent.putExtra("Przeglad",Przeglad);
                                        intent.putExtra("Inne",Inne);
                                        intent.putExtra("Cena",Cena);
                                        intent.putExtra("Dokument",NazwaDokumentu);
                                        context.startActivity(intent);
                                        if(context instanceof KsiazkaSerwisowa)
                                        {
                                            ((KsiazkaSerwisowa) context).finish();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

            });
        }
        public void Usun()
        {
            imageViewUsun.setOnClickListener(v->{
                String NazwaUzytkownika=ksiazkaSerwisowa.getNazwaUzytkownika();
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                CollectionReference ksiazkaSerwisowa=db.collection(Constants.KEY_COLLECTION_KSIAZKA);
                String TytulPostu=PrzyciskNazwa.getText().toString().trim();
                Query query=ksiazkaSerwisowa.whereEqualTo(Constants.KEY_NAZWA,TytulPostu);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document: task.getResult())
                        {
                            if(task.isSuccessful())
                            {
                                String Uzytkownik=document.getString(Constants.KEY_NAME);
                                System.out.println("Nazwa uzytkownika to "+Uzytkownik);
                                if(NazwaUzytkownika.equals(Uzytkownik))
                                {
                                    String tytulPostu1=document.getString("Nazwa");
                                    if(TytulPostu.equals(tytulPostu1))
                                    {
                                        String nazwaDokumentu=document.getId();
                                        db.collection(Constants.KEY_COLLECTION_KSIAZKA).document(nazwaDokumentu).delete()
                                                .addOnSuccessListener(v->{
                                                    Toast.makeText(context, "usunales wpis", Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent(context,KsiazkaSerwisowa.class);
                                                    context.startActivity(intent);
                                                    if(context instanceof KsiazkaSerwisowa)
                                                    {
                                                        ((KsiazkaSerwisowa)context).finish();
                                                    }
                                                })
                                                .addOnFailureListener(v->{
                                                    Toast.makeText(context, "Błąd usuwania", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                }
                            }
                        }
                    }
                });
            });
        }
    }



}
