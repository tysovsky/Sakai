package com.sky.sakai.models;

import com.sky.sakai.storage.DatabaseManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Announcement implements Serializable {
    public String Id;
    public String AnnouncementId;
    public String SiteId;
    public String CreatedBy;
    public String Title;
    public String Body;
    public Date CreationDate;

    public ArrayList<Attachment> Attachments;

    public Announcement(){
        Attachments = new ArrayList<>();
        CreationDate = new Date();
    }

    public void save(){
        DatabaseManager.getInstance().saveAnnouncement(this);
    }



}
