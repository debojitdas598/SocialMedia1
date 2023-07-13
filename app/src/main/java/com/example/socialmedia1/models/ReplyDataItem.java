package com.example.socialmedia1.models;

public class ReplyDataItem {

    private String text1;
    private String text2;
    private long text3;
    private String text4;

    public ReplyDataItem(String text1, String text2,long text3, String text4) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;

    }

    public String getText1() {
        return text1;
    }

    public String getText2() { return text2; }

    public long getText3() { return text3; }

    public String getText4() { return text4 ;}
}
