package com.rafslab.movie.dl.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class RootData implements Serializable {
    private String title;
    private List<RootChild> rootChildren;
    public RootData(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<RootChild> getRootChildren() {
        return rootChildren;
    }

    public void setRootChildren(List<RootChild> rootChildren) {
        this.rootChildren = rootChildren;
    }

    public static class RootChild implements Serializable {
        private String title;

        public RootChild() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
