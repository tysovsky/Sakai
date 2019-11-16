package com.sky.sakai.models;

import com.sky.sakai.storage.DatabaseManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Assignment implements Serializable {

    public String Id;
    public String EntityId;
    public String Title;
    public String Instructions;
    public String EntityURL;

    public Date CreatedTime;
    public Date DueTime;
    public Date ClosedTime;
    public Date OpenTime;
    public Date LastModifiedDate;

    public ArrayList<Attachment> Attachments;

    public Assignment(){
        Attachments = new ArrayList<>();
    }

    public void save(){
        DatabaseManager.getInstance().saveAssignment(this);
    }
}
