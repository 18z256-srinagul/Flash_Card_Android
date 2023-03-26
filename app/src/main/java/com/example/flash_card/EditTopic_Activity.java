package com.example.flash_card;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flash_card.database.DBHelper;

public class EditTopic_Activity extends AppCompatActivity {

    EditText old_topic, new_topic;
    Button apply;
    String old_topic_name, new_topic_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topic);

        old_topic = findViewById(R.id.currentTopiceditText);
        new_topic = findViewById(R.id.newTopiceditText);
        apply = findViewById(R.id.applyButtonEditTopic);

        Bundle bundle = getIntent().getExtras();
        old_topic_name = bundle.getString("old_topic");

        old_topic.setText(old_topic_name);

        apply.setOnClickListener(
                (View view) -> {
                    new_topic_name = new_topic.getText().toString();

                    try (DBHelper dbHelper = new DBHelper(EditTopic_Activity.this)) {
                        boolean result = dbHelper.UpdateTopic(old_topic_name, new_topic_name);

                        Intent intent = new Intent(EditTopic_Activity.this, MainActivity.class);

                        if (result) {
                            startActivity(intent);
                            Toast.makeText(EditTopic_Activity.this, "Update Success", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(EditTopic_Activity.this, "Update not success", Toast.LENGTH_LONG).show();
                    }

                }
        );


    }
}