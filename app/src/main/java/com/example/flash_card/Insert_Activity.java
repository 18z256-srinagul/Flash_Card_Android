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

public class Insert_Activity extends AppCompatActivity {

    Button cancel, insert;
    EditText title, content, topic, sentence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        cancel = findViewById(R.id.cancelButton);
        insert = findViewById(R.id.insertButton);

        title = findViewById(R.id.editTextTitle);
        content = findViewById(R.id.editTextContent);
        topic = findViewById(R.id.editTextTopic);
        sentence = findViewById(R.id.editTextSentence);


        cancel.setOnClickListener(
                (View view) -> {
                    Intent backToMainActivityIntent = new Intent(Insert_Activity.this, MainActivity.class);
                    startActivity(backToMainActivityIntent);
                }
        );

        insert.setOnClickListener(
                (View view) -> {

                    String titleText = title.getText().toString();
                    String contentText = content.getText().toString();
                    String topicText = topic.getText().toString();
                    String sentenceText = sentence.getText().toString();

                    if(titleText.isBlank() || titleText.isEmpty())
                        Toast.makeText(Insert_Activity.this,"Title is blank",Toast.LENGTH_LONG).show();

                    if(contentText.isBlank() || contentText.isEmpty())
                        Toast.makeText(Insert_Activity.this,"Content is blank",Toast.LENGTH_LONG).show();

                    if(topicText.isBlank() || topicText.isEmpty())
                        Toast.makeText(Insert_Activity.this,"Topic is blank",Toast.LENGTH_LONG).show();

                    if(sentenceText.isBlank() || sentenceText.isEmpty())
                        Toast.makeText(Insert_Activity.this,"Sentence is blank",Toast.LENGTH_LONG).show();

                    if(!sentenceText.toLowerCase().contains(titleText.toLowerCase()))
                        Toast.makeText(Insert_Activity.this,"Sentence does not have title text",Toast.LENGTH_LONG).show();


                    try (DBHelper helper = new DBHelper(Insert_Activity.this)) {
                        if(!helper.IsTopicPresent(topicText)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(Insert_Activity.this);
                            builder.setTitle("New Topic");
                            builder.setMessage("This topic is not present in DB. Do you want to add this topic?");
                            builder.setPositiveButton("Yes",(DialogInterface.OnClickListener)(dialog,which)->{
                                dialog.cancel();
                                helper.InsertToTopic(topicText);
                                helper.InsertToTitle(titleText, contentText, sentenceText, topicText);
                            });
                            builder.setNegativeButton("No",(DialogInterface.OnClickListener)(dialog,which)->{
                                dialog.cancel();
                                startActivity(new Intent(Insert_Activity.this,MainActivity.class));
                            });

                            builder.create().show();
                        }
                        else {
                            helper.InsertToTopic(topicText);
                            helper.InsertToTitle(titleText, contentText, sentenceText, topicText);
                        }
                    } catch (Exception e) {
                        Toast.makeText(Insert_Activity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    finally{
                        title.setText("");
                        content.setText("");
                        topic.setText("");
                        sentence.setText("");
                    }
                }
        );
    }
}