package com.rafslab.movie.dl.model.user;

import java.io.Serializable;

public class Request implements Serializable {
    public String key;
    public String title;
    public String device;

    public Request(String title, String device) {
        this.title = title;
        this.device = device;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
