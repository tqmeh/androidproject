package com.example.projektandroid.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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

    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "Spojrz tam";
    private static final String CHANNEL_DESCRIPTION = "Cos tam";
   private static final int notificationId = 1;
    private ActivityWyslijSmsBinding binding;
    private PreferenceManager preferenceManager;
    String numertelefonu,trescsms;
    String NazwaUzytkownika;
    private static final String PERMISSION_SMS = android.Manifest.permission.SEND_SMS;
    private static final String Permission_NOTIFICATION_POLICY= Manifest.permission.ACCESS_NOTIFICATION_POLICY;
    private static final int REQUEST_Notification_policy = 1;
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
                showNotification(this,this,"Wiadomość wysłana","Wiadomość wysłana na numer "+numertelefonu);

               // Intent intent=new Intent(WyslijSMS.this,MainActivity.class);

                //startActivity(intent);
            }

        }
    }
    public static void createNotificationChannel(Context context) {
        // Sprawdź, czy urządzenie używa wersji Androida Oreo (API 26) lub nowszej
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Utwórz kanał powiadomień
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            // Zarejestruj kanał powiadomień z menedżerem powiadomień
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static void showNotification(Activity activity, Context context, String title, String message) {
       createNotificationChannel(context); // Utwórz kanał powiadomień

        if (ContextCompat.checkSelfPermission(context, Permission_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Permission_NOTIFICATION_POLICY}, REQUEST_Notification_policy);
            Toast.makeText(context, "Brak uprawnień do powiadomień", Toast.LENGTH_SHORT).show();
        } else {
            // Utwórz obiekt powiadomienia
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Wyślij powiadomienie
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());

        }

    }
}