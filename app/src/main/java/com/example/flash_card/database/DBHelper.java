package com.example.flash_card.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.flash_card.dto.CardBody;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Flashcard_test.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_TOPIC_TABLE = "CREATE TABLE Topic(TopicId TEXT PRIMARY KEY, Topic TEXT,UNIQUE(Topic));";
    public static final String CREATE_TITLE_TABLE = "CREATE TABLE Title(TitleId TEXT PRIMARY KEY, Title TEXT, Content TEXT, Sentence TEXT, TopicId TEXT NOT NULL, FOREIGN KEY (TopicId) REFERENCES Topic(TopicId) ON UPDATE CASCADE ON DELETE CASCADE,UNIQUE(Title));";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TOPIC_TABLE);
        sqLiteDatabase.execSQL(CREATE_TITLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'Title'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS 'Topic'");
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }

    public void InsertToTopic(String topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TopicId",Base64.encodeToString(topic.getBytes(),1));
        contentValues.put("Topic",topic);

        db.insertWithOnConflict("Topic",null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void InsertToTitle(String title, String content, String sentence, String topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT TopicId FROM Topic WHERE Topic = ?;",new String[]{topic});

        String topicId="";
        while(c.moveToNext()){
            topicId = c.getString(0);
        }
        c.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put("TitleId",Base64.encodeToString(title.getBytes(),1));
        contentValues.put("Title",title);
        contentValues.put("Content",content);
        contentValues.put("Sentence",sentence);
        contentValues.put("TopicId",topicId);

        db.insertWithOnConflict("Title",null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
    }

    public List<String> GetTopics(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> topicList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT DISTINCT Topic FROM Topic",null);

        while(c.moveToNext()){
            topicList.add(c.getString(0));
        }
        c.close();

        return topicList;
    }

    public List<CardBody> GetFromSelectedTopics(String topic){
        SQLiteDatabase db = this.getReadableDatabase();
        List<CardBody> listOfCardBody = new ArrayList<>();
        String topicId = "";

        CardBody.SetTopic(topic);

        Cursor c = db.rawQuery("SELECT TopicId FROM Topic WHERE Topic = ?",new String[]{topic});
        while(c.moveToNext()){
            topicId = c.getString(0);
        }
        c.close();

        c = db.rawQuery("SELECT * FROM Title WHERE TopicId = ?",new String[]{topicId});
        while(c.moveToNext()){
            CardBody cardBody = new CardBody();
            cardBody.SetTitle(c.getString(1));
            cardBody.SetContent(c.getString(2));
            cardBody.SetSentence(c.getString(3));
            listOfCardBody.add(cardBody);
        }
        c.close();
        return listOfCardBody;
    }

    public boolean isTopicPresent(String topic){
        SQLiteDatabase db = this.getReadableDatabase();
        String presence = null;
        Cursor c = db.rawQuery("SELECT TopicId FROM Topic WHERE Topic = ?",new String[]{topic});

        while(c.moveToNext()){
            presence = c.getString(0);
        }
        c.close();
        return presence != null;
    }


    public void GetStatus(){
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder record = new StringBuilder();
        StringBuilder record2 = new StringBuilder();

        Cursor c = db.rawQuery("SELECT * FROM Title",null);
        Cursor c2 = db.rawQuery("SELECT * FROM Topic",null);

        while(c.moveToNext()){
            record.append(c.getString(0)).append(" ").append(c.getString(1)).append(" ").append(c.getString(2)).append(" ").append(c.getString(3)).append(" ").append(c.getString(4)).append(" ").append("\n");
        }

        while(c2.moveToNext()){
            record2.append(c2.getString(0)).append(" ").append(c2.getString(1)).append("\n");
        }
        Log.d("DBRecord: ", "TitleId  Topic  Content  Sentence  TopicId");
        Log.d("DBRecord: ", record.toString());
        Log.d("DBRecord: ", "TopicId  Topic");
        Log.d("DBRecord: ", record2.toString());
        c.close();
        c2.close();
    }

    public void FlushDB(){

        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete("Title",null,null);
//        db.delete("Topic",null,null);
        db.execSQL("DROP TABLE IF EXISTS 'Title'");
        db.execSQL("DROP TABLE IF EXISTS 'Topic'");
    }

}
