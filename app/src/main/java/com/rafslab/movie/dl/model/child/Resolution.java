package com.rafslab.movie.dl.model.child;

import java.io.Serializable;
import java.util.List;

/**
 * Created by: Rais AlFani Lubis
 * Date: October 18, 2020
 */

public class Resolution implements Serializable {
    private List<ResolutionValue> resolutionValues;

    public List<ResolutionValue> getResolutionValues() {
        return resolutionValues;
    }

    public void setResolutionValues(List<ResolutionValue> resolutionValues) {
        this.resolutionValues = resolutionValues;
    }
}
