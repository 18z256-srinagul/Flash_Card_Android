package com.example.flash_card;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button insert,start,manage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insert = findViewById(R.id.insert);
        start = findViewById(R.id.start);
        manage = findViewById(R.id.manage);

        insert.setOnClickListener(
                view -> {
                    Intent startInsertActivityIntent = new Intent(MainActivity.this,Insert_Activity.class);
                    startActivity(startInsertActivityIntent);
                }
        );

        start.setOnClickListener(
                view -> {
                    Intent startInsertActivityIntent = new Intent(MainActivity.this, SelectTopic_Activity.class);
                    startActivity(startInsertActivityIntent);
                }
        );

        manage.setOnClickListener(
                view -> {
                    Intent startManageActivityIntent = new Intent(MainActivity.this,Manage_Activity.class);
                    startActivity(startManageActivityIntent);
                }
        );

    }
}