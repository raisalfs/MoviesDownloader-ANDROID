package com.rafslab.movie.dl.model;

import java.io.Serializable;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class Account implements Serializable {
    private int icon;
    private String title;

    public Account() {
    }

    public Account(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
