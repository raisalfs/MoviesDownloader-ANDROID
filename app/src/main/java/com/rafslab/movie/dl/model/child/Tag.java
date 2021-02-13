package com.rafslab.movie.dl.model.child;

import java.io.Serializable;

public class Tag implements Serializable {
    private String tag;

    public Tag() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
