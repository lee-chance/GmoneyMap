package com.chance.gmoneymap.Models;

import com.google.firebase.firestore.FieldValue;

public class NoticeModel {
    String title, content;
    String[] noticeArray;

    public NoticeModel() {
    }

    public NoticeModel(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public NoticeModel(String title, String[] noticeArray) {
        this.title = title;
        this.noticeArray = noticeArray;
    }

    public NoticeModel(String title, String content, String[] noticeArray) {
        this.title = title;
        this.content = content;
        this.noticeArray = noticeArray;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String[] getNoticeArray() {
        return noticeArray;
    }
}
