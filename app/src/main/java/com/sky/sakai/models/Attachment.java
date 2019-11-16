package com.sky.sakai.models;

import java.io.Serializable;

public class Attachment implements Serializable {
    public String Id;
    public String Name;
    public String Url;
    public String Type;

    public Attachment(){

    }

    public Attachment(String id, String name, String url, String type){
        Id = id;
        Name = name;
        Url = url;
        Type = type;
    }

}
