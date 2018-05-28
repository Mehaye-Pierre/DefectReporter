package com.seawolf.defectreporter;

import java.util.Date;

/**
 * Created by Seawolf on 28/05/2018.
 */

public class Defect {

    private int ID;



    private String name;
    private String photoPath;
    private String description;
    private Date date;

    public Defect(int ID, String name, String photoPath, String description, Date date) {
        this.ID = ID;
        this.name = name;
        this.photoPath = photoPath;
        this.description = description;
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
