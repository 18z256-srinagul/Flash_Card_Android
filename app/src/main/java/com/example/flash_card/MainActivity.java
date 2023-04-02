package com.example.flash_card;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import com.example.flash_card.database.DBHelper;
import com.example.flash_card.dto.TitleDTO;
import com.example.flash_card.dto.TopicDTO;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button insert, start, manage, export, importData;
    ActivityResultLauncher<Intent> fileActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insert = findViewById(R.id.insert);
        start = findViewById(R.id.start);
        manage = findViewById(R.id.manage);
        export = findViewById(R.id.exportData);
        importData = findViewById(R.id.importData);

        insert.setOnClickListener(
                view -> {
                    Intent startInsertActivityIntent = new Intent(MainActivity.this, Insert_Activity.class);
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
                    Intent startManageActivityIntent = new Intent(MainActivity.this, Manage_Activity.class);
                    startActivity(startManageActivityIntent);
                }
        );

        export.setOnClickListener(
                view -> ExportData()
        );

        importData.setOnClickListener(
                view -> ImportData()
        );

        fileActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK) {

                        try (DBHelper dbHelper = new DBHelper(MainActivity.this)) {

                            Intent data = result.getData();
                            if (data.getData() == null) {
                                Toast.makeText(MainActivity.this, "Intent null pointer exception", Toast.LENGTH_LONG).show();
                            }
                            //FilePath: /storage/emulated/0/Download/FlashCardExport_.....csv
                            String filePathData = data.getData().getPath();
                            String filePath = "";
                            if (filePathData.contains("raw")) {
                                String[] filePathArr = filePathData.split("raw:/");
                                filePath = filePathArr[1];
                            } else if (filePathData.contains("primary:")) {
                                String[] filePathArr = filePathData.split("primary:");
                                filePath = filePathArr[1];
                                filePath = "/storage/emulated/0/" + filePath;
                            }

                            File importedFile = new File(filePath);

                            try {
                                FileInputStream fis = new FileInputStream(importedFile);
                                DataInputStream in = new DataInputStream(fis);
                                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                                String strLine;
                                while ((strLine = br.readLine()) != null) {
                                    String tableName;
                                    String[] splitData = strLine.split(",");
                                    tableName = splitData[0];

                                    switch (tableName) {
                                        case "Topic": {
                                            String topicName;
                                            topicName = splitData[2];
                                            dbHelper.InsertToTopic(topicName);
                                            break;
                                        }
                                        case "Title": {
                                            String titleName, content, sentence, topic;
                                            titleName = splitData[2];
                                            content = splitData[3];
                                            sentence = splitData[4];
                                            topic = dbHelper.GetTopicFromTopicID(splitData[5]);
                                            dbHelper.InsertToTitle(titleName, content, sentence, topic);
                                            break;
                                        }
                                    }
                                    dbHelper.GetStatus();
                                }
                                br.close();
                                in.close();
                                fis.close();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    }

                });
    }


    private void WriteDataToFile() {
        try (DBHelper dbHelper = new DBHelper(MainActivity.this)) {
            List<TopicDTO> topicDTOList = dbHelper.ExportTopicDatabaseData();
            List<TitleDTO> titleDTOList = dbHelper.ExportTitleDatabaseData();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String fileName = String.format("FlashCardExport_%s.csv", timeStamp);

            File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadDirectory, fileName);
            try (FileWriter fileWriter = new FileWriter(file)) {
                String line;
                for (TopicDTO topic : topicDTOList) {
                    line = String.format("Topic,%s,%s\n", topic.getTopicID(), topic.getTopic());
                    fileWriter.append(line);
                }

                for (TitleDTO title : titleDTOList) {
                    line = String.format("Title,%s,%s,%s,%s,%s\n", title.getTitleID(), title.getTitle(), title.getContent(), title.getSentence(), title.getTopicID());
                    fileWriter.append(line);
                }
                Toast.makeText(MainActivity.this, "Your data has been exported", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }


    private void ReadDataFromFile() {

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("text/comma-separated-values");
        //startActivityForResult(chooseFile,1);
        fileActivityResultLauncher.launch(chooseFile);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            try (DBHelper dbHelper = new DBHelper(MainActivity.this)) {
                if (data.getData() == null) {
                    Toast.makeText(MainActivity.this, "Intent null pointer exception", Toast.LENGTH_LONG).show();
                }
                String filePath = data.getData().getPath().split("raw:/")[1];
                File importedFile = new File(filePath);

                try {
                    FileInputStream fis = new FileInputStream(importedFile);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String strLine;
                    while ((strLine = br.readLine()) != null) {
                        String tableName;
                        String[] splitData = strLine.split(",");
                        tableName = splitData[0];

                        switch (tableName) {
                            case "Topic": {
                                String topicName;
                                topicName = splitData[2];
                                dbHelper.InsertToTopic(topicName);
                                break;
                            }
                            case "Title": {
                                String titleName, content, sentence, topic;
                                titleName = splitData[2];
                                content = splitData[3];
                                sentence = splitData[4];
                                topic = dbHelper.GetTopicFromTopicID(splitData[5]);
                                dbHelper.InsertToTitle(titleName, content, sentence, topic);
                                break;
                            }
                        }
                        dbHelper.GetStatus();
                    }
                    br.close();
                    in.close();
                    fis.close();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void ExportData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(MainActivity.this, "External Media not mounted", Toast.LENGTH_LONG).show();
            return;
        }

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            WriteDataToFile();
        }
    }

    private void ImportData() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(MainActivity.this, "External Media not mounted", Toast.LENGTH_LONG).show();
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            ReadDataFromFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            WriteDataToFile();
        } else if (requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ReadDataFromFile();
        }

    }
}