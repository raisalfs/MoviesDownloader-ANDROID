package com.rafslab.movie.dl.model.child;

import java.io.Serializable;

public class ValueDownload implements Serializable {
    private int name;
    private String value;

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
