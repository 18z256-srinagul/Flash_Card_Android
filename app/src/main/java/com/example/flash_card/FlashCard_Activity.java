package com.example.flash_card;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flash_card.database.DBHelper;
import com.example.flash_card.dto.CardBody;

import java.util.Collections;
import java.util.List;

public class FlashCard_Activity extends AppCompatActivity {

    Button next, finish;
    TextView title, content, sentence;
    String chosenTopic = "";
    List<CardBody> contents;
    int contentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(FlashCard_Activity.this, "Null topic", Toast.LENGTH_LONG).show();
        } else {
            contentIndex = 0;
            chosenTopic = bundle.getString("TopicChosen");
            try (DBHelper helper = new DBHelper(FlashCard_Activity.this)) {
                contents = helper.GetFromSelectedTopics(chosenTopic);
                Collections.shuffle(contents);
            } catch (Exception e) {
                Toast.makeText(FlashCard_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
            }

            next = findViewById(R.id.nextID);
            finish = findViewById(R.id.finishID);

            title = findViewById(R.id.textForTitle);
            content = findViewById(R.id.textForContent);
            sentence = findViewById(R.id.textForSentence);

            finish.setOnClickListener(
                    (View view) -> {
                        Intent moveToMainActivity = new Intent(FlashCard_Activity.this, MainActivity.class);
                        startActivity(moveToMainActivity);
                    }
            );

            if (contentIndex < contents.size()) {
                CardBody cardBody = contents.get(contentIndex++);
                title.setText(cardBody.GetTitle());
                content.setOnClickListener(
                        (View view)->{
                            content.setText(cardBody.GetContent());
                        }
                );
                sentence.setOnClickListener(
                        (View view2)->{
                            sentence.setText(cardBody.GetSentence());
                        }
                );
            } else if (contents.size() == 0) {
                Toast.makeText(FlashCard_Activity.this, "No questions added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(FlashCard_Activity.this, "End of Q's. Starting from first", Toast.LENGTH_LONG).show();
                contentIndex = contentIndex % contents.size();
            }

            next.setOnClickListener(
                    (View view) -> {
                        if (contentIndex < contents.size()) {
                            CardBody cardBody = contents.get(contentIndex++);
                            title.setText(cardBody.GetTitle());
                            content.setText(R.string.check_the_content);
                            content.setOnClickListener(
                                    (View view2)->{
                                        content.setText(cardBody.GetContent());
                                    }
                            );
                            sentence.setText(R.string.check_the_sentence);
                            sentence.setOnClickListener(
                                    (View view2)->{
                                        sentence.setText(cardBody.GetSentence());
                                    }
                            );
                        } else if (contents.size() == 0) {
                            Toast.makeText(FlashCard_Activity.this, "No questions added", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(FlashCard_Activity.this, "End of Q's. Starting from first", Toast.LENGTH_LONG).show();
                            contentIndex = contentIndex % contents.size();
                            Collections.shuffle(contents);
                        }
                    }
            );
        }
    }
}