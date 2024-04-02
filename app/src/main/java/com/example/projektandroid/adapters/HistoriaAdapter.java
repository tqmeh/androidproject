package com.example.projektandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projektandroid.R;
import com.example.projektandroid.activites.DodajHistoria;
import com.example.projektandroid.activites.Historia;
import com.example.projektandroid.adapters.HistoriaData;
import com.example.projektandroid.utillites.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class HistoriaAdapter extends RecyclerView.Adapter<HistoriaAdapter.ViewHolder> {

    private final Context context;
    private final List<HistoriaData> historiaDataList;
    private final Historia historia;

    public HistoriaAdapter(List<HistoriaData> historiaDataList, Historia historia,Context context) {
        this.historiaDataList = historiaDataList;
        this.historia=historia;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_historia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       HistoriaData historiaData=historiaDataList.get(position);
       holder.bind(historiaData);


    }

    @Override
    public int getItemCount() {
        return historiaDataList.size();
    }

   public class ViewHolder extends RecyclerView.ViewHolder {

        Button Przycisk;
        TextView kilometryTextView, Paliwo, Autostrady, Winety,Data;

        ImageView Edytuj, Usun;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Data=itemView.findViewById(R.id.textViewData);
            Przycisk = itemView.findViewById(R.id.PrzyciskNazwa);
            kilometryTextView = itemView.findViewById(R.id.textViewKilometry);
            Paliwo = itemView.findViewById(R.id.textViewPaliwo);
            Autostrady = itemView.findViewById(R.id.textViewAutostrada);
            Winety = itemView.findViewById(R.id.textViewWineta);
            Edytuj = itemView.findViewById(R.id.imageEdytuj);
            Usun = itemView.findViewById(R.id.imageUsun);
            Ukryj();
            Przycisk.setOnClickListener(v -> {
                if (kilometryTextView.getVisibility() == View.VISIBLE) {
                    Ukryj();
                } else {
                    Pokaz();
                }
            });

            Edytuj(this);
            Usun(this);
        }
            public void bind (HistoriaData historiaData){
                Przycisk.setText(historiaData.getNazwa());
                kilometryTextView.setText("Przejechane km   " + historiaData.getKilometry());
                Paliwo.setText("Kwota Paliwa   " + historiaData.getKwotaPaliwo());
                Autostrady.setText("Koszt Autostrad   " + historiaData.getAutostrada());
                Winety.setText("Koszt Winet   " + historiaData.getWineta());
                Data.setText("Data   "+historiaData.getData());


            }

            public void Ukryj ()
            {
                kilometryTextView.setVisibility(View.GONE);
                Paliwo.setVisibility(View.GONE);
                Autostrady.setVisibility(View.GONE);
                Winety.setVisibility(View.GONE);
                Edytuj.setVisibility(View.GONE);
                Usun.setVisibility(View.GONE);
                Data.setVisibility(View.GONE);
            }

        public void Pokaz() {
            kilometryTextView.setVisibility(View.VISIBLE);
            Paliwo.setVisibility(View.VISIBLE);
            Autostrady.setVisibility(View.VISIBLE);
            Winety.setVisibility(View.VISIBLE);
            Edytuj.setVisibility(View.VISIBLE);
            Usun.setVisibility(View.VISIBLE);
            Data.setVisibility(View.VISIBLE);
        }

    }
    public void Edytuj(ViewHolder viewHolder) {

        viewHolder.Edytuj.setOnClickListener(v -> {
            System.out.println("Zaczynam edytować wpisy w historii");
            String nazwaUzytkownika = historia.getNazwaUzytkownika();
            System.out.println("Nazwa uzytkownika to "+nazwaUzytkownika);
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            CollectionReference historia=db.collection(Constants.KEY_COLLECTION_HISTORIA);
            String nazwaHistorii=viewHolder.Przycisk.getText().toString().trim();
            System.out.println("Nazwa elementu naszego to "+nazwaHistorii);
            Query query=historia.whereEqualTo(Constants.KEY_NAZWA,nazwaHistorii);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot document:task.getResult())
                    {
                        if(task.isSuccessful())
                        {
                            if(document.contains(Constants.KEY_NAME))
                            {
                                String nazwadokumentu=document.getId();
                                System.out.println("Numer dokumentu to "+nazwadokumentu);
                                String imiewBazie=document.getString(Constants.KEY_NAME);
                                String nazwa=document.getString(Constants.KEY_NAZWA);
                                String kilometry=document.getString(Constants.KEY_LICZBA_KILOMETROW);
                                String paliwo=document.getString(Constants.KEY_ILOSC_PALIWA);
                                String autostrada=document.getString(Constants.KEY_OPLATY_DROGOWE);
                                String wineta=document.getString(Constants.KEY_OPLATY_WINETA);
                                String data=document.getString(Constants.KEY_DATA);

                                if(nazwaUzytkownika.equals(imiewBazie)&&nazwaHistorii.equals(nazwa))
                                {
                                    Toast.makeText(context, "Możesz edytować", Toast.LENGTH_SHORT).show();

                                    Intent intent=new Intent(context, DodajHistoria.class);
                                    intent.putExtra("WpiszNazwe",nazwaHistorii);
                                    intent.putExtra("Kilometry",kilometry);
                                    intent.putExtra("Paliwo",paliwo);
                                    intent.putExtra("Autostrada",autostrada);
                                    intent.putExtra("Wineta",wineta);
                                    intent.putExtra("NumerDokumentu",nazwadokumentu);
                                    intent.putExtra("Data",data);
                                    context.startActivity(intent);
                                    if (context instanceof Historia) {
                                        ((Historia) context).finish();
                                    }
                                }
                            }
                        }
                    }
                }
            });

        });

    }

    public void Usun(ViewHolder viewHolder)
    {
        viewHolder.Usun.setOnClickListener(view -> {


        String NazwaUzytkownika=historia.getNazwaUzytkownika();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference historia=db.collection(Constants.KEY_COLLECTION_HISTORIA);
        String TytulPostu=viewHolder.Przycisk.getText().toString().trim();
        Query query=historia.whereEqualTo(Constants.KEY_NAZWA,TytulPostu);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot document:task.getResult())
                {
                    if(task.isSuccessful())
                    {
                        String Uzytkownik=document.getString(Constants.KEY_NAME);
                        if(NazwaUzytkownika.equals(Uzytkownik))
                        {
                            String tytulPostu1=document.getString(Constants.KEY_NAZWA);
                            if(TytulPostu.equals(tytulPostu1))
                            {
                                String nazwaDokumentu=document.getId();
                                db.collection(Constants.KEY_COLLECTION_HISTORIA).document(nazwaDokumentu).delete()
                                        .addOnSuccessListener(v->{
                                            Toast.makeText(context, "usunales wpis", Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(context,Historia.class);
                                            context.startActivity(intent);
                                            if(context instanceof Historia)
                                            {
                                                ((Historia)context).finish();
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
