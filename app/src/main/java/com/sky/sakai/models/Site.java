package com.sky.sakai.models;

import com.sky.sakai.storage.DatabaseManager;

import java.io.Serializable;
import java.util.ArrayList;

public class Site implements Serializable {

    public enum SiteType {
        COURSE
    }

    public String Id;
    public String Title;
    public String Description;
    public String EntityId;
    public String EntityUrl;
    public String Term;
    public SiteType Type;

    ArrayList<Announcement> announcements;
    ArrayList<Assignment> assignments;

    public Site(){
        announcements = new ArrayList<>();
        assignments = new ArrayList<>();
    }

    public void save(){
        DatabaseManager.getInstance().saveSite(this);
    }


}
