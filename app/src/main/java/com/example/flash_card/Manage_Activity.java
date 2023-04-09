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

import java.util.List;

public class Manage_Activity extends AppCompatActivity {

    LinearLayout linearLayout;
    Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        linearLayout = findViewById(R.id.topicContainer);
        backButton = findViewById(R.id.manageActivityBack);

        backButton.setOnClickListener(
                (View view) -> {
                    Intent takeToMainActivity = new Intent(Manage_Activity.this, MainActivity.class);
                    startActivity(takeToMainActivity);
                }
        );

        try (DBHelper dbHelper = new DBHelper(Manage_Activity.this)) {

            List<String> topicList = dbHelper.GetTopics();

            for (String topic : topicList) {
                final View view = getLayoutInflater().inflate(R.layout.activity_topic_display, null);
                TextView topicName = view.findViewById(R.id.topicName);
                topicName.setText(topic);

                Button selectButton = view.findViewById(R.id.selectTopic);
                Button editButton = view.findViewById(R.id.editTopic);
                Button deleteButton = view.findViewById(R.id.deleteTopic);

                selectButton.setOnClickListener(
                        (View view1) -> {
                            Log.d("SelectButton", topic);
                            Intent editTopicNameIntent = new Intent(Manage_Activity.this, ViewContents_Activity.class);
                            editTopicNameIntent.putExtra("selected_topic", topic);
                            startActivity(editTopicNameIntent);
                        }
                );

                editButton.setOnClickListener(
                        (View view1) -> {
                            Intent editTopicNameIntent = new Intent(Manage_Activity.this, EditTopic_Activity.class);
                            editTopicNameIntent.putExtra("old_topic", topic);
                            startActivity(editTopicNameIntent);
                        }
                );

                deleteButton.setOnClickListener(
                        (View view1) -> {
                            Log.d("DeleteButton", topic);
                            Intent editTopicNameIntent = new Intent(Manage_Activity.this, MainActivity.class);
                            boolean result = dbHelper.DeleteTopic(topic);
                            if (result)
                                startActivity(editTopicNameIntent);

                            linearLayout.removeView(view);
                        }
                );

                linearLayout.addView(view);
                dbHelper.GetStatus();
            }

        }
    }
}