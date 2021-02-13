package com.rafslab.movie.dl.model.child;

import java.io.Serializable;
import java.util.List;

public class Resolution implements Serializable {
    private List<ResolutionValue> resolutionValues;

    public List<ResolutionValue> getResolutionValues() {
        return resolutionValues;
    }

    public void setResolutionValues(List<ResolutionValue> resolutionValues) {
        this.resolutionValues = resolutionValues;
    }
}
