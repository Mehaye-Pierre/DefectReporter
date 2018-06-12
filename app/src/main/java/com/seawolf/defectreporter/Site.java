package com.seawolf.defectreporter;

import java.io.Serializable;

/**
 * Created by Seawolf on 28/05/2018.
 */

public class Site implements Serializable{

    private int ID;
    private String name;

    /**
     * constructor for a Site
     * is used to create a new Site object
     * @param ID is given to the Site to identify it
     * @param name is the name of the Site
     */
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
