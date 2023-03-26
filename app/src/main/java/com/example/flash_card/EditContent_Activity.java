package com.example.flash_card;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flash_card.database.DBHelper;

public class EditContent_Activity extends AppCompatActivity {

    String selected_title, selected_content, selected_sentence;
    EditText title, content, sentence;
    Button cancel, apply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);

        title = findViewById(R.id.currentTitleeditText);
        content = findViewById(R.id.currentContenteditText);
        sentence = findViewById(R.id.currentSentenceeditText);

        cancel = findViewById(R.id.cancelButtonWithEditContent);
        apply = findViewById(R.id.applyButtonEditContent);

        Bundle bundle = getIntent().getExtras();
        selected_title = bundle.getString("selected_title");
        selected_content = bundle.getString("selected_content");
        selected_sentence = bundle.getString("selected_sentence");

        title.setText(selected_title);
        content.setText(selected_content);
        sentence.setText(selected_sentence);


        apply.setOnClickListener(
                (View view) -> {
                    String modified_title = title.getText().toString();
                    String modified_content = content.getText().toString();
                    String modified_sentence = sentence.getText().toString();

                    try (DBHelper dbHelper = new DBHelper(EditContent_Activity.this)) {
                        boolean result = dbHelper.UpdateContents(selected_title,selected_content,selected_sentence,modified_title,modified_content,modified_sentence);

                        Intent intent = new Intent(EditContent_Activity.this, MainActivity.class);

                        if (result) {
                            startActivity(intent);
                            Toast.makeText(EditContent_Activity.this, "Update Success", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(EditContent_Activity.this, "Update not success", Toast.LENGTH_LONG).show();
                    }
                }
        );


        cancel.setOnClickListener(
                (View view) -> {
                    Intent navigatetoViewContentIntent = new Intent(EditContent_Activity.this,Manage_Activity.class);
                    startActivity(navigatetoViewContentIntent);
                }
        );




    }
}