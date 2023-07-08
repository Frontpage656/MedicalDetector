package com.example.medicaldetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllList extends AppCompatActivity {

    RecyclerView listRecyleView;
    List<ItemDetails> itemList = new ArrayList();
    RecyclerView.LayoutManager linearLayout;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);

        listRecyleView = findViewById(R.id.listRecyleView);

        linearLayout = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        listRecyleView.setLayoutManager(linearLayout);



        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("item");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ItemDetails items = dataSnapshot.getValue(ItemDetails.class);
                    itemList.add(items);
                }
                itemAdapter = new ItemAdapter(getApplicationContext(), itemList);
                listRecyleView.setAdapter(itemAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllList.this, "Failed to load..", Toast.LENGTH_SHORT).show();
            }
        });

    }
}