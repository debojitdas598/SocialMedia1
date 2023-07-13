package com.example.socialmedia1.models;

public class ReplyDataItem {

    private String text1;
    private String text2;
    private String text3;
    private long text4;

    public ReplyDataItem(String text1, String text2,String text3, long text4) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;

    }

    public String getText1() {
        return text1;
    }

    public String getText2() { return text2; }

    public String getText3() { return text3; }

    public long getText4() { return text4 ;}
}
