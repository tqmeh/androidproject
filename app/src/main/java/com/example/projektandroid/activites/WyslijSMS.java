package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import android.telephony.SmsManager;



import com.example.projektandroid.R;
import com.example.projektandroid.databinding.ActivityHistoriaBinding;
import com.example.projektandroid.databinding.ActivityWyslijSmsBinding;
import com.example.projektandroid.utillites.Constants;
import com.example.projektandroid.utillites.PreferenceManager;

public class WyslijSMS extends AppCompatActivity {

    private static final int REQUEST_SEND_SMS = 1;
    private ActivityWyslijSmsBinding binding;
    private PreferenceManager preferenceManager;
    String numertelefonu,trescsms;
    String NazwaUzytkownika;
    private static final String PERMISSION_SMS = android.Manifest.permission.SEND_SMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityWyslijSmsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        binding.imageBack.setOnClickListener(e->
        {
            Intent intent=new Intent(WyslijSMS.this,MainActivity.class);
            startActivity(intent);
        });
        binding.editTextWpisznumerTelefonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editTextWpisznumerTelefonu.setText("");
            }
        });
        //setContentView(R.layout.activity_wyslij_sms);
        LadowanieUzytkownikaDanych();
        binding.buttonWYslij.setOnClickListener(view -> {
            PobierzDane();
        });


    }
    private void LadowanieUzytkownikaDanych()
    {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.zjdecieProfilowe.setImageBitmap(bitmap);
        NazwaUzytkownika=preferenceManager.getString(Constants.KEY_NAME);

    }
    private void PobierzDane() {
        numertelefonu = binding.editTextWpisznumerTelefonu.getText().toString().trim();
        trescsms = binding.editTextWpiszTrescSMS.getText().toString().trim();
        int dlugoscnumerutelefonu = numertelefonu.length();

        if (numertelefonu.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Nie wpisałeś numeru telefonu", Toast.LENGTH_SHORT).show();
        } else if (dlugoscnumerutelefonu < 9) {
            Toast.makeText(getApplicationContext(), "Numer telefonu za krótki", Toast.LENGTH_SHORT).show();
        } else {
            if (ContextCompat.checkSelfPermission(this, PERMISSION_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Jeśli nie ma, poproś użytkownika o uprawnienie
                ActivityCompat.requestPermissions(this,
                        new String[]{PERMISSION_SMS},
                        REQUEST_SEND_SMS);
            }
            else
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(numertelefonu, null, trescsms, null, null);
                Toast.makeText(getApplicationContext(), "Wysłano SMS do " + numertelefonu, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(WyslijSMS.this,MainActivity.class);
                startActivity(intent);
            }

        }
    }

}