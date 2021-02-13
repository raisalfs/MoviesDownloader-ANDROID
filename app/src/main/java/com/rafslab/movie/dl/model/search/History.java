package com.rafslab.movie.dl.model.search;

import java.io.Serializable;
import java.util.List;

public class History implements Serializable {
    private int id;
    private String query;
    private List<String> arrayQuery;

    public History() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getArrayQuery() {
        return arrayQuery;
    }

    public void setArrayQuery(List<String> arrayQuery) {
        this.arrayQuery = arrayQuery;
    }
}
