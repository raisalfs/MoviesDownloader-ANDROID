package com.rafslab.movie.dl.model.child;

import java.io.Serializable;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class Categories implements Serializable {
    private String genre;
    private String name;
    private int type;
    public Categories(){

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
