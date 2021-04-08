package com.rafslab.movie.dl.model.user;

import java.io.Serializable;

public class Report implements Serializable {
    public String key;
    public String title;
    public String episode;
    public String season;
    public String resolution;
    public String reason;

    public Report(String title, String episode, String season, String resolution, String reason) {
        this.title = title;
        this.episode = episode;
        this.season = season;
        this.resolution = resolution;
        this.reason = reason;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpisode() {
        return episode;
    }

    public void setEpisode(String episode) {
        this.episode = episode;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
