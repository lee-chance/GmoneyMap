package com.chance.gmoneymap.Models;

import android.graphics.drawable.Drawable;

public class MenuModel {
    private String content;
    private Drawable icon;

    public MenuModel() {
    }

    public MenuModel(String content, Drawable icon) {
        this.content = content;
        this.icon = icon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
