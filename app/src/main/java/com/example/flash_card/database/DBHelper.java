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
import com.example.flash_card.dto.TitleDTO;
import com.example.flash_card.dto.TopicDTO;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Flashcard_test.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_TOPIC_TABLE = "CREATE TABLE Topic(TopicId TEXT PRIMARY KEY, Topic TEXT,UNIQUE(Topic));";
    public static final String CREATE_TITLE_TABLE = "CREATE TABLE Title(TitleId TEXT PRIMARY KEY, Title TEXT, Content TEXT, Sentence TEXT, TopicId TEXT NOT NULL, FOREIGN KEY (TopicId) REFERENCES Topic(TopicId) ON UPDATE CASCADE ON DELETE CASCADE,UNIQUE(Title,TopicId));";

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
        contentValues.put("TopicId",Base64.encodeToString(topic.getBytes(),Base64.NO_WRAP));
        contentValues.put("Topic",topic);

        db.insertWithOnConflict("Topic",null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
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
        contentValues.put("TitleId",Base64.encodeToString((title+topic).getBytes(),Base64.NO_WRAP));
        contentValues.put("Title",title);
        contentValues.put("Content",content);
        contentValues.put("Sentence",sentence);
        contentValues.put("TopicId",topicId);

        db.insertWithOnConflict("Title",null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public String GetTopicFromTopicID(String topicID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT Topic FROM Topic WHERE TopicId = ?;",new String[]{topicID});

        String topic="";
        while(c.moveToNext()){
            topic = c.getString(0);
        }
        c.close();
        db.close();
        return topic;
    }

    public List<String> GetTopics(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> topicList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT DISTINCT Topic FROM Topic",null);

        while(c.moveToNext()){
            topicList.add(c.getString(0));
        }
        c.close();
        db.close();

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
        db.close();
        return listOfCardBody;
    }

    public boolean IsTopicPresent(String topic){
        SQLiteDatabase db = this.getReadableDatabase();
        String presence = null;
        Cursor c = db.rawQuery("SELECT TopicId FROM Topic WHERE Topic = ?",new String[]{topic});

        while(c.moveToNext()){
            presence = c.getString(0);
        }
        c.close();
        db.close();
        return presence != null;
    }

    public boolean UpdateTopic(String old_topic,String new_topic){
        SQLiteDatabase db = this.getReadableDatabase();
        String presence = null;

        ContentValues contentValues = new ContentValues();
        contentValues.put("Topic",new_topic);

        Cursor c = db.rawQuery("SELECT TopicId FROM Topic WHERE Topic = ?",new String[]{old_topic});
        while(c.moveToNext()){
            presence = c.getString(0);
        }
        c.close();

        if(presence == null || presence.isEmpty())
            return false;

        int updateResult = db.update("Topic",contentValues,"TopicId=?",new String[]{presence});
        db.close();

        return updateResult == 1;
    }

    public boolean UpdateContents(String old_title, String old_content, String old_sentence, String new_title, String new_content, String new_sentence){
        SQLiteDatabase db = this.getReadableDatabase();
        String presence = null;

        ContentValues contentValues = new ContentValues();
        contentValues.put("Title",new_title);
        contentValues.put("Content",new_content);
        contentValues.put("Sentence",new_sentence);

        Cursor c = db.rawQuery("SELECT TitleId FROM Title WHERE Title = ? AND Content = ? AND Sentence = ? ",new String[]{old_title,old_content,old_sentence});
        while(c.moveToNext()){
            presence = c.getString(0);
        }
        c.close();

        if(presence == null || presence.isEmpty())
            return false;

        int updateResult = db.update("Title",contentValues,"TitleId=?",new String[]{presence});
        db.close();

        return updateResult == 1;
    }


    public boolean DeleteTopic(String topic){
        SQLiteDatabase db = this.getReadableDatabase();
        int result = db.delete("Topic","Topic=?",new String[]{topic});
        db.close();
        return result == 1;
    }

    public boolean DeleteContent(String title, String content, String sentence){
        SQLiteDatabase db = this.getReadableDatabase();
        int result = db.delete("Title","Title=? AND Content=? AND Sentence=? ",new String[]{title,content,sentence});
        db.close();
        return result == 1;
    }

    public List<TopicDTO> ExportTopicDatabaseData(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<TopicDTO> topicList = new ArrayList<>();


        Cursor c = db.rawQuery("SELECT * FROM Topic",null);

        while(c.moveToNext()){
            TopicDTO topic = new TopicDTO();
            topic.setTopicID(c.getString(0));
            topic.setTopic(c.getString(1));
            topicList.add(topic);
        }

        c.close();
        db.close();

        return topicList;
    }

    public List<TitleDTO> ExportTitleDatabaseData(){
        SQLiteDatabase db = this.getReadableDatabase();
        List<TitleDTO> titleList = new ArrayList<>();


        Cursor c = db.rawQuery("SELECT * FROM Title",null);

        while(c.moveToNext()){
            TitleDTO topic = new TitleDTO();
            topic.setTitleID(c.getString(0));
            topic.setTitle(c.getString(1));
            topic.setContent(c.getString(2));
            topic.setSentence(c.getString(3));
            topic.setTopicID(c.getString(4));
            titleList.add(topic);
        }

        c.close();
        db.close();

        return titleList;
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
        Log.d("DBRecord: ", "TitleId  Title  Content  Sentence  TopicId");
        Log.d("DBRecord: ", record.toString());
        Log.d("DBRecord: ", "TopicId  Topic");
        Log.d("DBRecord: ", record2.toString());
        c.close();
        c2.close();
        db.close();
    }

    public void FlushDB(){

        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete("Title",null,null);
//        db.delete("Topic",null,null);
        db.execSQL("DROP TABLE IF EXISTS 'Title'");
        db.execSQL("DROP TABLE IF EXISTS 'Topic'");
    }

}
