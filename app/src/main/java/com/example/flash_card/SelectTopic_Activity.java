package com.example.flash_card;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.flash_card.database.DBHelper;

import java.util.List;

public class SelectTopic_Activity extends AppCompatActivity {

    ListView listView;
    Button cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_topic);

        listView = findViewById(R.id.listView);
        cancel = findViewById(R.id.BackButtonID);

        cancel.setOnClickListener(
                (View view)->{
                    Intent backToMainActivity = new Intent(SelectTopic_Activity.this,MainActivity.class);
                    startActivity(backToMainActivity);
                }
        );


        try (DBHelper helper = new DBHelper(SelectTopic_Activity.this)) {
            List<String> topicList = helper.GetTopics();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectTopic_Activity.this, android.R.layout.simple_list_item_1, topicList);

            if (topicList.size() == 0) {
                Toast.makeText(SelectTopic_Activity.this, "Add topic first and choose ", Toast.LENGTH_LONG).show();
            } else {
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(
                        (AdapterView<?> adapterView, View view, int i, long l) -> {
                            String selectedTopic = adapterView.getItemAtPosition(i).toString();
                            Intent takeToView = new Intent(SelectTopic_Activity.this,FlashCard_Activity.class);
                            takeToView.putExtra("TopicChosen",selectedTopic);
                            startActivity(takeToView);
                        }
                );
            }
        } catch (Exception e) {
            Toast.makeText(SelectTopic_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
        }


    }
}