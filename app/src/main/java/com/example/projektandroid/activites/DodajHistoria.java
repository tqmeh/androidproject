package com.example.projektandroid.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projektandroid.R;
import com.example.projektandroid.databinding.ActivityDodajHistoriaBinding;
import com.example.projektandroid.databinding.ActivityHistoriaBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class DodajHistoria extends AppCompatActivity {

    private ActivityDodajHistoriaBinding binding;
    private PreferenceManager preferenceManager;
    String NazwaUzytkownika;
    String IloscKilometrow,IlośćPaliwa,OplatyDrogowe,OplatyWineta,Nazwa;
    Switch SOplatyDrogowe;
    TextView Autostrasdy,Wineta,textWpiszNazwe,textIloscKM,KwotaZapaliwa,textWpisanaData;
    String dodajnazwe,kilometry,paliwo,autostrada,wineta, dokument,FormatDaty,Data,Data1;
    EditText WpiszOplatyDrogowe,WpiszOplatyWineta,WpiszNazwe,WpiszIlośćKM,WpiszIloscPaliwa;
    DatePicker picker;
    Button WybierzDate,PrzyciskZapisz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityDodajHistoriaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        LadowanieUzytkownikaDanych();
        Wineta=findViewById(R.id.textWineta);
        Wineta.setVisibility(View.GONE);
       picker=findViewById(R.id.datePicker);
        picker.setVisibility(View.GONE);
        PrzyciskZapisz=findViewById(R.id.buttonZapisz);
        PrzyciskZapisz.setVisibility(View.GONE);
        WybierzDate=findViewById(R.id.ButtonWybierzDate);
        WpiszOplatyDrogowe = findViewById(R.id.WpiszOplatyDrogowe);
        WpiszOplatyDrogowe.setVisibility(View.GONE);
        WpiszOplatyWineta=findViewById(R.id.WpiszOplatyWineta);
        WpiszOplatyWineta.setVisibility(View.GONE);
        Autostrasdy=findViewById(R.id.textAutostrady);
        Autostrasdy.setVisibility(View.GONE);
        SOplatyDrogowe=findViewById(R.id.switchOplatyDrogowe);
        textWpiszNazwe=findViewById(R.id.textWpiszNazwe);
        WpiszNazwe=findViewById(R.id.WpiszNazwe);
        textIloscKM=findViewById(R.id.textIloscKM);
        WpiszIlośćKM=findViewById(R.id.WpiszIlośćKM);
        KwotaZapaliwa=findViewById(R.id.KwotaZapaliwa);
        WpiszIloscPaliwa=findViewById(R.id.WpiszIloscPaliwa);
        textWpisanaData=findViewById(R.id.textWpisanaData);

        Intent intent=getIntent();
        SOplatyDrogowe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Autostrasdy.setVisibility(View.VISIBLE);
                    Wineta.setVisibility(View.VISIBLE);
                    WpiszOplatyDrogowe.setVisibility(View.VISIBLE);
                    WpiszOplatyWineta.setVisibility(View.VISIBLE);

                }
                else
                {
                    Autostrasdy.setVisibility(View.GONE);
                    Wineta.setVisibility(View.GONE);
                    WpiszOplatyDrogowe.setVisibility(View.GONE);
                    WpiszOplatyWineta.setVisibility(View.GONE);

                }
            }
        });
        if(intent!=null&&intent.hasExtra("WpiszNazwe")&&intent.hasExtra("Kilometry")&&
        intent.hasExtra("Paliwo")&&intent.hasExtra("Autostrada")&&intent.hasExtra("Wineta")
        &&intent.hasExtra("NumerDokumentu")&&intent.hasExtra("Data"))
        {
            dodajnazwe=intent.getStringExtra("WpiszNazwe");
            String nazwaPoczatkowa=dodajnazwe;
             kilometry=intent.getStringExtra("Kilometry");
             paliwo=intent.getStringExtra("Paliwo");
             autostrada=intent.getStringExtra("Autostrada");
             wineta=intent.getStringExtra("Wineta");
             dokument=intent.getStringExtra("NumerDokumentu");
             Data1=intent.getStringExtra("Data");
            int wartosc=Integer.parseInt(autostrada);
            int wartosc1=Integer.parseInt(wineta);
            binding.WpiszNazwe.setText(dodajnazwe);
            binding.WpiszIloKM.setText(kilometry);
            binding.WpiszIloscPaliwa.setText(paliwo);
            binding.WpiszOplatyDrogowe.setText(autostrada);
            binding.WpiszOplatyWineta.setText(wineta);
            binding.textWpisanaData.setText(Data1);
            if(wartosc>0||wartosc1>0)
            {
                binding.switchOplatyDrogowe.setChecked(true);
            }

            binding.imageZapisz.setOnClickListener(view -> {
                String wezNazwe=binding.WpiszNazwe.getText().toString().toString();
                if(nazwaPoczatkowa.equals(wezNazwe)) {
                    EdytujWpisDoBazy();
                    Intent DodajHistoria = new Intent(com.example.projektandroid.activites.DodajHistoria.this, Historia.class);
                    startActivity(DodajHistoria);
                    finish();
                }
                else
                {
                   SprawdzCzyIstniejePoEdycji();
                }
            });
        }
        else {
            binding.imageZapisz.setOnClickListener(v ->
            {


                Pobierz();

                if (!IloscKilometrow.isEmpty() && !Nazwa.isEmpty()&&!Data.isEmpty()) {

                    SprawdzCzyIstnieje();




                }
            });
        }


        WybierzDate.setOnClickListener(view -> {
            picker.setVisibility(View.VISIBLE);
            PrzyciskZapisz.setVisibility(View.VISIBLE);
            textWpiszNazwe.setVisibility(View.GONE);
            WpiszNazwe.setVisibility(View.GONE);
            textIloscKM.setVisibility(View.GONE);
            WpiszIlośćKM.setVisibility(View.GONE);
            KwotaZapaliwa.setVisibility(View.GONE);
            WpiszIloscPaliwa.setVisibility(View.GONE);
            textWpisanaData.setVisibility(View.GONE);
            WybierzDate.setVisibility(View.GONE);
        });
        PrzyciskZapisz.setOnClickListener(view ->{
            picker.setVisibility(View.GONE);
            PrzyciskZapisz.setVisibility(View.GONE);
            textWpiszNazwe.setVisibility(View.VISIBLE);
            WpiszNazwe.setVisibility(View.VISIBLE);
            textIloscKM.setVisibility(View.VISIBLE);
            WpiszIlośćKM.setVisibility(View.VISIBLE);
            KwotaZapaliwa.setVisibility(View.VISIBLE);
            WpiszIloscPaliwa.setVisibility(View.VISIBLE);
            textWpisanaData.setVisibility(View.VISIBLE);
            WybierzDate.setVisibility(View.VISIBLE);
            WybierzDate();
        });

    }
    private void WybierzDate()
    {
        int dzien=picker.getDayOfMonth();
        int miesiac=picker.getMonth();
        int rok=picker.getYear();
        miesiac=miesiac;
        Calendar WybierzDate=Calendar.getInstance();
        WybierzDate.set(rok,miesiac,dzien);

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        FormatDaty=dateFormat.format(WybierzDate.getTime());
        textWpisanaData.setText(FormatDaty);
        System.out.println("Wybrana data to "+FormatDaty);
    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);
        NazwaUzytkownika=preferenceManager.getString(Constants.KEY_NAME);

    }
    private void Pobierz()
    {
        IloscKilometrow=binding.WpiszIloKM.getText().toString().trim();
        IlośćPaliwa=binding.WpiszIloscPaliwa.getText().toString().trim();
        OplatyDrogowe=binding.WpiszOplatyDrogowe.getText().toString().trim();
        OplatyWineta=binding.WpiszOplatyWineta.getText().toString().trim();
        Nazwa=binding.WpiszNazwe.getText().toString().trim();
        Data=binding.textWpisanaData.getText().toString().trim();
        Sprawdz();

    }
    private void Sprawdz()
    {
        System.out.println("Ilosc kilometrow to "+IloscKilometrow);
        if(IloscKilometrow.isEmpty())
        {
            Toast.makeText(DodajHistoria.this, "Nie podales ilsoci kilometrów", Toast.LENGTH_SHORT).show();
        }
        if(Nazwa.isEmpty())
        {
            Toast.makeText(DodajHistoria.this, "Nie podales nazwy wpisu", Toast.LENGTH_SHORT).show();
        }
        if(Data.isEmpty())
        {
            Toast.makeText(DodajHistoria.this, "Nie podales daty wpisu", Toast.LENGTH_SHORT).show();
        }
         if (IlośćPaliwa.isEmpty()) {
            IlośćPaliwa = "0";
         }
        if (OplatyDrogowe.isEmpty()) {
            OplatyDrogowe = "0";
        }
        if (OplatyWineta.isEmpty())
        {
            OplatyWineta="0";
        }



    }

    public void EdytujWpisDoBazy()
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        Map<String,Object> update=new HashMap<>();
        update.put(Constants.KEY_NAZWA,binding.WpiszNazwe.getText().toString().trim());
        update.put(Constants.KEY_LICZBA_KILOMETROW,binding.WpiszIloKM.getText().toString().trim());
        String paliwo=binding.WpiszIloscPaliwa.getText().toString().trim();
        if(paliwo.isEmpty())
        {
            paliwo="0";
        }
        update.put(Constants.KEY_ILOSC_PALIWA,paliwo);
        String autostrady=binding.WpiszOplatyDrogowe.getText().toString().trim();
        if(autostrady.isEmpty())
        {
            autostrady="0";
        }
        update.put(Constants.KEY_OPLATY_DROGOWE,autostrady);
        String wineta=binding.WpiszOplatyWineta.getText().toString().trim();
        update.put(Constants.KEY_OPLATY_WINETA,wineta);
        String Data2=binding.textWpisanaData.getText().toString().trim();
        update.put(Constants.KEY_DATA,Data2);

        db.collection(Constants.KEY_COLLECTION_HISTORIA)
                .document(dokument)
                .update(update)
                .addOnSuccessListener(v->{
                    Toast.makeText(DodajHistoria.this, "Zaktualizowano wpis", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(v->{
                    Toast.makeText(DodajHistoria.this, "Błąd podczas aktualizacji", Toast.LENGTH_SHORT).show();
                });
    }
    private void DodajWpisDoBazy() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String, Object> Historia = new HashMap<>();
            Historia.put(Constants.KEY_LICZBA_KILOMETROW, IloscKilometrow);
            Historia.put(Constants.KEY_ILOSC_PALIWA, IlośćPaliwa);
            Historia.put(Constants.KEY_OPLATY_DROGOWE, OplatyDrogowe);
            Historia.put(Constants.KEY_NAME, binding.textName.getText().toString().trim());
            Historia.put(Constants.KEY_OPLATY_WINETA,OplatyWineta);
            Historia.put(Constants.KEY_NAZWA,Nazwa);
            Historia.put(Constants.KEY_DATA,FormatDaty);
            database.collection(Constants.KEY_COLLECTION_HISTORIA)
                    .add(Historia)
                    .addOnSuccessListener(documentReference -> {
                        preferenceManager.putString(Constants.KEY_LICZBA_KILOMETROW, IloscKilometrow);
                        preferenceManager.putString(Constants.KEY_ILOSC_PALIWA, IlośćPaliwa);
                        preferenceManager.putString(Constants.KEY_OPLATY_DROGOWE, OplatyDrogowe);
                        preferenceManager.putString(Constants.KEY_NAME, binding.textName.getText().toString().trim());
                        preferenceManager.putString(Constants.KEY_OPLATY_WINETA,OplatyWineta);
                        preferenceManager.putString(Constants.KEY_NAZWA,Nazwa);
                        preferenceManager.putString(Constants.KEY_DATA,FormatDaty);

                        Toast.makeText(DodajHistoria.this, "Dodane do historii wpisów", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DodajHistoria.this, "Błąd wysyłania", Toast.LENGTH_SHORT).show();
                    });


    }
    public void SprawdzCzyIstnieje() {

        String NazwaUzytkownika = preferenceManager.getString(Constants.KEY_NAME);
        System.out.println("Nazwa uzytkownika to " + NazwaUzytkownika);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference historia = db.collection(Constants.KEY_COLLECTION_HISTORIA);
        String NazwaPostu = WpiszNazwe.getText().toString().trim();
        Query query=historia.whereEqualTo("Nazwa",NazwaPostu).whereEqualTo(Constants.KEY_NAME,NazwaUzytkownika);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                QuerySnapshot querySnapshot=task.getResult();
                if(querySnapshot!=null&&!querySnapshot.isEmpty())
                {
                    Toast.makeText(DodajHistoria.this, "Post o takim tytule już istnieje, zmień go", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    DodajWpisDoBazy();
                    Intent DodajHistoria=new Intent(com.example.projektandroid.activites.DodajHistoria.this,Historia.class);
                    startActivity(DodajHistoria);
                    finish();
                    System.out.println("Brak dokumentów z polem 'nazwa' zawierającym 'Tomcio'.");
                }
            }
        });
    }
    public void SprawdzCzyIstniejePoEdycji() {

        String NazwaUzytkownika = preferenceManager.getString(Constants.KEY_NAME);
        System.out.println("Nazwa uzytkownika to " + NazwaUzytkownika);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference historia = db.collection(Constants.KEY_COLLECTION_HISTORIA);
        String NazwaPostu = WpiszNazwe.getText().toString().trim();
        Query query=historia.whereEqualTo("Nazwa",NazwaPostu);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                QuerySnapshot querySnapshot=task.getResult();
                if(querySnapshot!=null&&!querySnapshot.isEmpty())
                {
                    Toast.makeText(DodajHistoria.this, "Post o takim tytule już istnieje, zmień go", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    EdytujWpisDoBazy();
                    Intent DodajHistoria=new Intent(com.example.projektandroid.activites.DodajHistoria.this,Historia.class);
                    startActivity(DodajHistoria);
                    finish();
                    System.out.println("Brak dokumentów z polem 'nazwa' zawierającym 'Tomcio'.");
                }
            }
        });
    }
}