package com.rafslab.movie.dl.model.child;

import java.io.Serializable;

public class CoverArray implements Serializable {

    private String image;

    public CoverArray(){

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
