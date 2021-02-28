package com.rafslab.movie.dl.model.child;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class DownloadedDatabase implements Serializable {
    String title;
    String size;
    int id;
    private static int idCounter = 0;

    public DownloadedDatabase() {
        this.id = idCounter++;
    }
    List<String> downloaded;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<String> getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(List<String> downloaded) {
        this.downloaded = downloaded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
