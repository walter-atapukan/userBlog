package com.example.myblog;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {
    private DatabaseReference dbSetting;
    TextView textDescription, tVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("Tentang Aplikasi");

        dbSetting = FirebaseDatabase.getInstance().getReference().child("Pengaturan");
        dbSetting.keepSynced(true);

        textDescription = (TextView)findViewById(R.id.textDescription);
        tVersion = (TextView)findViewById(R.id.tVersion);

        dbSetting.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String detail = snapshot.child("deskripsi").getValue().toString();
                String versi = snapshot.child("versi").getValue().toString();
                textDescription.setText(detail);
                tVersion.setText(versi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}