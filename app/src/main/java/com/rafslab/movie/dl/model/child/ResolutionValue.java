package com.rafslab.movie.dl.model.child;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class ResolutionValue implements Serializable {
    private String name;
    private Value values;
    private int id;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Value getValues() {
        return values;
    }

    public void setValues(Value values) {
        this.values = values;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class Value implements Serializable {
        private String episode;
        private String batch;

        public String getEpisode() {
            return episode;
        }

        public void setEpisode(String episode) {
            this.episode = episode;
        }

        public String getBatch() {
            return batch;
        }

        public void setBatch(String batch) {
            this.batch = batch;
        }
    }
}
