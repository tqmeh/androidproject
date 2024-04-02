package com.example.projektandroid.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.CarrierConfigManager;
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
import com.example.projektandroid.databinding.ActivityDodajKsiazkaSerwisowaBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DodajKsiazkaSerwisowa extends AppCompatActivity {

    private ActivityDodajKsiazkaSerwisowaBinding binding;
    private PreferenceManager preferenceManager;
    private String NazwaUzytkownika;
    Switch Olej,Klocki,TarczeHamulcowe,Filtry,OponaPrzod,OponaTyl,Akumulator,PlynHamulcowy,
            Przeglad,Inne;
    Button buttonWybierzDate,buttonZapisz;
    Boolean Olej1=false,Klocki1=false,TarczeHamulcowe1=false,Filtry1=false,OponaPrzod1=false,OponaTyl1=false,Akumulator1=false,PlynHamulcowy1=false,
    Przegla1=false,Inne1=false;
    String Cena,Nazwa,FormatDaty,Data,Dokument;
    TextView textViewWybranaData;
    DatePicker datePicker;
    EditText WpiszNazwe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        binding=ActivityDodajKsiazkaSerwisowaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        WezzLayout();
        binding.imageBack.setOnClickListener(v-> {
            Intent ksiazka=new Intent(DodajKsiazkaSerwisowa.this,KsiazkaSerwisowa.class);
            startActivity(ksiazka);
            finish();
        });

        LadowanieUzytkownikaDanych();
        Switche();
        textViewWybranaData=findViewById(R.id.textViewWybranaData);
        buttonZapisz=findViewById(R.id.buttonZapisz);
        buttonZapisz.setVisibility(View.GONE);
        datePicker=findViewById(R.id.datePicker);
        datePicker.setVisibility(View.GONE);
        WpiszNazwe=findViewById(R.id.WpiszNazwe);

        if(intent!=null&&intent.hasExtra("WpiszDate")&&intent.hasExtra("Nazwa")&&intent.hasExtra("Olej")
        &&intent.hasExtra("Klocki")&&intent.hasExtra("Tarcze")&&intent.hasExtra("Filtry")
        &&intent.hasExtra("OponaPrzod")&&intent.hasExtra("OponaTyl")&&intent.hasExtra("Akumulator")
        &&intent.hasExtra("PlynHamulcowy")&&intent.hasExtra("Przeglad")&&intent.hasExtra("Inne")
        &&intent.hasExtra("Cena")&&intent.hasExtra("Dokument"))
        {
            String Data=intent.getStringExtra("WpiszDate");
            String Nazwa=intent.getStringExtra("Nazwa");
            String NazwaPoczatkowa=Nazwa;
             Olej1=intent.getBooleanExtra("Olej",false);
             Klocki1=intent.getBooleanExtra("Klocki",false);
             TarczeHamulcowe1=intent.getBooleanExtra("Tarcze",false);
             Filtry1=intent.getBooleanExtra("Filtry",false);
             OponaPrzod1=intent.getBooleanExtra("OponaPrzod",false);
             OponaTyl1=intent.getBooleanExtra("OponaTyl",false);
             Akumulator1=intent.getBooleanExtra("Akumulator",false);
             PlynHamulcowy1=intent.getBooleanExtra("PlynHamulcowy",false);
            Przegla1=intent.getBooleanExtra("Przeglad",false);
             Inne1=intent.getBooleanExtra("Inne",false);
            String Cena=intent.getStringExtra("Cena");
            binding.textViewWybranaData.setText(Data);
            binding.WpiszNazwe.setText(Nazwa);
            Olej.setChecked(Olej1);
            Klocki.setChecked(Klocki1);
            TarczeHamulcowe.setChecked(TarczeHamulcowe1);
            Filtry.setChecked(Filtry1);
            OponaPrzod.setChecked(OponaPrzod1);
            OponaTyl.setChecked(OponaTyl1);
            Akumulator.setChecked(Akumulator1);
            PlynHamulcowy.setChecked(PlynHamulcowy1);
            Przeglad.setChecked(Przegla1);
            Inne.setChecked(Inne1);
            binding.WpiszCene.setText(Cena);
            Dokument=intent.getStringExtra("Dokument");

            binding.imageZapisz.setOnClickListener(v->{

                String Cena1=binding.WpiszCene.getText().toString().trim();
                String Nazwa1=binding.WpiszNazwe.getText().toString().trim();
                String Data1=binding.textViewWybranaData.getText().toString().trim();
                if (Nazwa1.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Nie podałeś nazwy wpisu do książki serwisowej", Toast.LENGTH_SHORT).show();
                }
                else if(Cena1.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Nie wpisaleś ceny", Toast.LENGTH_SHORT).show();
                }
                else if (Data1.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Nie podałeś daty", Toast.LENGTH_SHORT).show();
                }

                else if(!Cena1.isEmpty()&&!Nazwa1.isEmpty()&&!Data1.isEmpty()&&NazwaPoczatkowa.equals(Nazwa1)) {
                   EdytujWpiswBazie();

                }
                else if(!Cena1.isEmpty()&&!Nazwa1.isEmpty()&&!Data1.isEmpty()&&!NazwaPoczatkowa.equals(Nazwa1))
                {
                    SprawdzCzyIstniejePoEdycji();
                }



            });
        }
        else {
            binding.imageZapisz.setOnClickListener(v->{

                Cena=binding.WpiszCene.getText().toString().trim();
                Nazwa=binding.WpiszNazwe.getText().toString().trim();
                Data=binding.textViewWybranaData.getText().toString().trim();




                if (Nazwa.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Nie podałeś nazwy wpisu do książki serwisowej", Toast.LENGTH_SHORT).show();
                }
                else if(Cena.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Nie wpisaleś ceny", Toast.LENGTH_SHORT).show();
                }
                else if (Data.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Nie podałeś daty", Toast.LENGTH_SHORT).show();
                }

                else if(!Cena.isEmpty()&&!Nazwa.isEmpty()&&!Data.isEmpty()) {
                    SprawdzCzyIstnieje();


                }



            });
        }

        PrzyciskWybierzDate();
        PrzyciskZapiszDate();

    }
    public void PrzyciskWybierzDate()
    {
        buttonWybierzDate.setOnClickListener(view -> {
           Olej.setVisibility(View.GONE);
           Klocki.setVisibility(View.GONE);
           TarczeHamulcowe.setVisibility(View.GONE);
           Filtry.setVisibility(View.GONE);
           OponaPrzod.setVisibility(View.GONE);
           OponaTyl.setVisibility(View.GONE);
           Akumulator.setVisibility(View.GONE);
           PlynHamulcowy.setVisibility(View.GONE);
           Przeglad.setVisibility(View.GONE);
           Inne.setVisibility(View.GONE);
            datePicker.setVisibility(View.VISIBLE);
            buttonZapisz.setVisibility(View.VISIBLE);


       });
    }
    public void PrzyciskZapiszDate()
    {
        buttonZapisz.setOnClickListener(view -> {
            datePicker.setVisibility(View.GONE);
            buttonZapisz.setVisibility(View.GONE);

            Olej.setVisibility(View.VISIBLE);
            Klocki.setVisibility(View.VISIBLE);
            TarczeHamulcowe.setVisibility(View.VISIBLE);
            Filtry.setVisibility(View.VISIBLE);
            OponaPrzod.setVisibility(View.VISIBLE);
            OponaTyl.setVisibility(View.VISIBLE);
            Akumulator.setVisibility(View.VISIBLE);
            PlynHamulcowy.setVisibility(View.VISIBLE);
            Przeglad.setVisibility(View.VISIBLE);
            Inne.setVisibility(View.VISIBLE);
            WybierzDate();
        });
    }
    private void WybierzDate()
    {
        int dzien=datePicker.getDayOfMonth();
        int miesiac=datePicker.getMonth();
        int rok=datePicker.getYear();

        Calendar WybierzDate=Calendar.getInstance();
        WybierzDate.set(rok,miesiac,dzien);

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        FormatDaty=dateFormat.format(WybierzDate.getTime());
        textViewWybranaData.setText(FormatDaty);
        System.out.println("Wybrana data to "+FormatDaty);
    }

    public void WezzLayout()  {
            Olej=findViewById(R.id.switchOlej);
            Klocki=findViewById(R.id.switchKlocki);
            TarczeHamulcowe=findViewById(R.id.switchTarcze);
            Filtry=findViewById(R.id.switchFiltry);
            OponaPrzod=findViewById(R.id.switchOponaPrzod);
            OponaTyl=findViewById(R.id.switchOponaTyl);
            Akumulator=findViewById(R.id.switchAkumulator);
            PlynHamulcowy=findViewById(R.id.switchPlynHamulcowy);
            Przeglad=findViewById(R.id.switchPrzeglad);
            Inne=findViewById(R.id.switchInne);
        buttonWybierzDate=findViewById(R.id.buttonWybierzDate);





    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);
        NazwaUzytkownika=preferenceManager.getString(Constants.KEY_NAME);

    }
    private void Switche()
    {
        Olej.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean zmiana) {
                if(zmiana)
                {
                    System.out.println("Przelacznik jest włączony");
                    Olej1=true;
                    System.out.println("Boolean ma stan "+Olej1);
                }
                else
                {
                    System.out.println("Przelacznik jest wylaczony");
                    Olej1=false;
                    System.out.println("Boolean ma stan "+Olej1);
                }
            }
        });
        Klocki.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean zmiana) {
                if(zmiana)
                {
                    System.out.println("Przelacznik od klockow jest wlaczony");
                    Klocki1=true;
                    System.out.println("Bolean ma stan Klocki "+Klocki1);
                }
                else
                {
                    System.out.println("Przelacznik od klockow jest wylaczony");
                    Klocki1=false;
                    System.out.println("Bolean ma stan Klocki "+Klocki1);
                }
            }
        });
        TarczeHamulcowe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean zmiana) {
                if(zmiana)
                {
                    TarczeHamulcowe1 = true;
                }
                else {
                    TarczeHamulcowe1=false;
                }

            }

        });
        Filtry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Filtry1=true;

                }
                else
                {
                    Filtry1=false;
                }
            }
        });
        OponaPrzod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    OponaPrzod1=true;
                }
                else {
                    OponaPrzod1=false;
                }
            }
        });
        OponaTyl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    OponaTyl1=true;
                }
                else
                {
                    OponaTyl1=false;

                }
            }
        });
        Akumulator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Akumulator1=true;
                }
                else
                {
                    Akumulator1=false;
                }
            }
        });
        PlynHamulcowy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    PlynHamulcowy1=true;
                }
                else
                {
                    PlynHamulcowy1=false;
                }
            }
        });
        Przeglad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Przegla1=true;
                }
                else
                {
                    Przegla1=false;
                }
            }
        });
        Inne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    Inne1=true;
                }
                else
                {
                    Inne1=false;
                }
            }
        });
    }
    public void WyslijdoBazy()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> ksiazka = new HashMap<>();
        ksiazka.put(Constants.KEY_OLEJ,Olej1);
        ksiazka.put(Constants.KEY_KLOCKI,Klocki1);
        ksiazka.put(Constants.KEY_TarczeHamulcowe,TarczeHamulcowe1);
        ksiazka.put(Constants.KEY_FILTRY,Filtry1);
        ksiazka.put(Constants.KEY_OPONA_PRZOD,OponaPrzod1);
        ksiazka.put(Constants.KEY_OPONA_TYL,OponaTyl1);
        ksiazka.put(Constants.KEY_AKUMULATOR,Akumulator1);
        ksiazka.put(Constants.KEY_PLYN_HAMULCOWY,PlynHamulcowy1);
        ksiazka.put(Constants.KEY_PRZEGLAD,Przegla1);
        ksiazka.put(Constants.KEY_INNE,Inne1);
        ksiazka.put(Constants.KEY_CENA,Cena);
        ksiazka.put(Constants.KEY_NAZWA,Nazwa);
        ksiazka.put(Constants.KEY_NAME,NazwaUzytkownika);
        ksiazka.put(Constants.KEY_DATA,Data);
        database.collection(Constants.KEY_COLLECTION_KSIAZKA)
                .add(ksiazka)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putString(Constants.KEY_OLEJ, String.valueOf(Olej1));
                    preferenceManager.putString(Constants.KEY_KLOCKI, String.valueOf(Klocki1));
                    preferenceManager.putString(Constants.KEY_TarczeHamulcowe, String.valueOf(TarczeHamulcowe1));
                    preferenceManager.putString(Constants.KEY_FILTRY, String.valueOf(Filtry1));
                    preferenceManager.putString(Constants.KEY_OPONA_PRZOD, String.valueOf(OponaPrzod1));
                    preferenceManager.putString(Constants.KEY_OPONA_TYL, String.valueOf(OponaTyl1));
                    preferenceManager.putString(Constants.KEY_AKUMULATOR, String.valueOf(Akumulator1));
                    preferenceManager.putString(Constants.KEY_PLYN_HAMULCOWY, String.valueOf(PlynHamulcowy1));
                    preferenceManager.putString(Constants.KEY_PRZEGLAD, String.valueOf(Przegla1));
                    preferenceManager.putString(Constants.KEY_INNE, String.valueOf(Inne1));
                    preferenceManager.putString(Constants.KEY_CENA,Cena);
                    preferenceManager.putString(Constants.KEY_NAZWA,Nazwa);
                    preferenceManager.putString(Constants.KEY_NAME,NazwaUzytkownika);
                    preferenceManager.putString(Constants.KEY_DATA,Data);
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Wpis do książki serwisowej został dodany", Toast.LENGTH_SHORT).show();
                    Intent ksiazkaserwisowa=new Intent(DodajKsiazkaSerwisowa.this,KsiazkaSerwisowa.class);
                    startActivity(ksiazkaserwisowa);
                    finish();

                })
                .addOnFailureListener(exception ->
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Błąd wysylania", Toast.LENGTH_SHORT).show();
                });

    }
    public void EdytujWpiswBazie()
    {
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        Map<String,Object> ksiazka=new HashMap<>();
        ksiazka.put(Constants.KEY_OLEJ,Olej1);
        ksiazka.put(Constants.KEY_KLOCKI,Klocki1);
        ksiazka.put(Constants.KEY_TarczeHamulcowe,TarczeHamulcowe1);
        ksiazka.put(Constants.KEY_FILTRY,Filtry1);
        ksiazka.put(Constants.KEY_OPONA_PRZOD,OponaPrzod1);
        ksiazka.put(Constants.KEY_OPONA_TYL,OponaTyl1);
        ksiazka.put(Constants.KEY_AKUMULATOR,Akumulator1);
        ksiazka.put(Constants.KEY_PLYN_HAMULCOWY,PlynHamulcowy1);
        ksiazka.put(Constants.KEY_PRZEGLAD,Przegla1);
        ksiazka.put(Constants.KEY_INNE,Inne1);
        ksiazka.put(Constants.KEY_CENA,binding.WpiszCene.getText().toString().trim());
        ksiazka.put(Constants.KEY_NAZWA,binding.WpiszNazwe.getText().toString().trim());
        ksiazka.put(Constants.KEY_DATA,binding.textViewWybranaData.getText().toString().trim());

        db.collection(Constants.KEY_COLLECTION_KSIAZKA)
                .document(Dokument)
                .update(ksiazka)
                .addOnSuccessListener(v->{
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Zaktualizowano wpis", Toast.LENGTH_SHORT).show();
                    Intent ksiazkaserwisowa=new Intent(DodajKsiazkaSerwisowa.this,KsiazkaSerwisowa.class);
                    startActivity(ksiazkaserwisowa);
                    finish();
                })
                .addOnFailureListener(v->{
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Błąd podczas aktualizacji", Toast.LENGTH_SHORT).show();
                });


}
    public void SprawdzCzyIstnieje() {

        String NazwaUzytkownika = preferenceManager.getString(Constants.KEY_NAME);
        System.out.println("Nazwa uzytkownika to " + NazwaUzytkownika);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference historia = db.collection(Constants.KEY_COLLECTION_KSIAZKA);
        String NazwaPostu = WpiszNazwe.getText().toString().trim();
        Query query=historia.whereEqualTo("Nazwa",NazwaPostu).whereEqualTo(Constants.KEY_NAME,NazwaUzytkownika);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                QuerySnapshot querySnapshot=task.getResult();
                if(querySnapshot!=null&&!querySnapshot.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Post o takim tytule już istnieje, zmień go", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    WyslijdoBazy();
                    Intent DodajHistoria=new Intent(DodajKsiazkaSerwisowa.this,KsiazkaSerwisowa.class);
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
        CollectionReference historia = db.collection(Constants.KEY_COLLECTION_KSIAZKA);
        String NazwaPostu = WpiszNazwe.getText().toString().trim();
        Query query=historia.whereEqualTo("Nazwa",NazwaPostu);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                QuerySnapshot querySnapshot=task.getResult();
                if(querySnapshot!=null&&!querySnapshot.isEmpty())
                {
                    Toast.makeText(DodajKsiazkaSerwisowa.this, "Post o takim tytule już istnieje, zmień go", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    EdytujWpiswBazie();
                    Intent DodajHistoria=new Intent(DodajKsiazkaSerwisowa.this,KsiazkaSerwisowa.class);
                    startActivity(DodajHistoria);
                    finish();
                    System.out.println("Brak dokumentów z polem 'nazwa' zawierającym 'Tomcio'.");
                }
            }
        });
    }
}