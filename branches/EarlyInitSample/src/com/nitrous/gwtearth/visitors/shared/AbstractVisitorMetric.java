package com.nitrous.gwtearth.visitors.shared;

import java.io.Serializable;
import java.util.Date;

public abstract class AbstractVisitorMetric implements Serializable {
    private static final long serialVersionUID = 765196895404058571L;
    
    private Date lastVisitDate;
    private int visitCount;
    private LatLon latLong;

    public AbstractVisitorMetric() {
    }

    public Date getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public LatLon getLatLon() {
        return latLong;
    }

    public void setLatLon(LatLon location) {
        this.latLong = location;
    }

}



