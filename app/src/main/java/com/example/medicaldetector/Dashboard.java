package com.example.medicaldetector;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    LinearLayout myList, scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasbord);

        myList = findViewById(R.id.myList_card);
        scan = findViewById(R.id.scan_card);

        myList.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), AllList.class));
        });

        scan.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }
}