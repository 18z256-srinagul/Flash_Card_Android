package com.example.flash_card.dto;


public class CardBody {
    private String title;
    private String content;
    private String sentence;
    private static String topic;

    public void SetTitle(String title){
        this.title = title;
    }
    public void SetContent(String content){
        this.content = content;
    }
    public void SetSentence(String sentence){
        this.sentence = sentence;
    }
    public static void SetTopic(String inpTopic){
        topic = inpTopic;
    }

    public String GetTitle(){
        return this.title;
    }

    public String GetContent(){
        return this.content;
    }

    public String GetSentence(){
        return this.sentence;
    }

    public static String GetTopic(){
        return topic;
    }

}
