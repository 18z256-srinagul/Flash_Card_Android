package com.example.flash_card;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flash_card.database.DBHelper;
import com.example.flash_card.dto.CardBody;

import java.util.List;

public class ViewContents_Activity extends AppCompatActivity {

    LinearLayout linearLayout;
    Button backButton;

    String selected_topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        backButton = findViewById(R.id.manageActivityBack);
        linearLayout = findViewById(R.id.topicContainer);

        backButton.setOnClickListener(
                (View view) -> {
                    Intent takeToMainActivity = new Intent(ViewContents_Activity.this,MainActivity.class);
                    startActivity(takeToMainActivity);
                }
        );

        try(DBHelper dbHelper = new DBHelper(ViewContents_Activity.this)) {


            Bundle bundle = getIntent().getExtras();
            selected_topic = bundle.getString("selected_topic");
            List<CardBody> topicList = dbHelper.GetFromSelectedTopics(selected_topic);


            for (CardBody cardbody : topicList) {

                View view = getLayoutInflater().inflate(R.layout.activity_content_display, null);
                TextView titleName = view.findViewById(R.id.titleName);
                TextView contentName = view.findViewById(R.id.contextName);
                TextView sentenceName = view.findViewById(R.id.sentenceName);


                titleName.setText(cardbody.GetTitle());
                contentName.setText(cardbody.GetContent());
                sentenceName.setText(cardbody.GetSentence());

                Button editButton = view.findViewById(R.id.editTopic);
                Button deleteButton = view.findViewById(R.id.deleteTopic);

                editButton.setOnClickListener(
                        (View view1) -> {
                            Intent editContentsIntent = new Intent(ViewContents_Activity.this, EditContent_Activity.class);
                            editContentsIntent.putExtra("selected_title", cardbody.GetTitle());
                            editContentsIntent.putExtra("selected_content", cardbody.GetContent());
                            editContentsIntent.putExtra("selected_sentence", cardbody.GetSentence());

                            startActivity(editContentsIntent);
                        }
                );

                deleteButton.setOnClickListener(
                        (View view1) -> {
                            Log.d("DeleteButton", cardbody.GetTitle());
                            Intent editTopicNameIntent = new Intent(ViewContents_Activity.this, MainActivity.class);
                            boolean result = dbHelper.DeleteContent(cardbody.GetTitle(), cardbody.GetContent(), cardbody.GetSentence());
                            if (result)
                                startActivity(editTopicNameIntent);

                            linearLayout.removeView(view);
                        }
                );

                dbHelper.GetStatus();
//            TextView testte = view.findViewById(R.id.titleName);
//            Log.d("ENDTEST",testte.getText().toString());
                linearLayout.addView(view);
            }
        }

    }
}