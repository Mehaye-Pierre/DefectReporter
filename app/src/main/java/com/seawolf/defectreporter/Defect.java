package com.seawolf.defectreporter;

import android.os.Parcel;


import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seawolf on 28/05/2018.
 */

public class Defect implements Serializable{

    private int ID;


    private String name;
    private String photoPath;
    private String description;
    private Date date;

    /**
     * creates a defect object
     * @param ID is the unique ID for each created defect
     * @param name is the name of the defect
     * @param date is the date when the defect was created
     */
    public Defect(int ID, String name, Date date) {
        this.ID = ID;
        this.name = name;
        this.date = date;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
