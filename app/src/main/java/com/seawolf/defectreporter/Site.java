package com.seawolf.defectreporter;

/**
 * Created by Seawolf on 28/05/2018.
 */

public class Site {

    private int ID;
    private String name;

    public Site(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }


    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(int ID) {

        this.ID = ID;
    }

}
