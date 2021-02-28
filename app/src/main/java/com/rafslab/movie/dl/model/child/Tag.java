package com.rafslab.movie.dl.model.child;

import java.io.Serializable;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */
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
