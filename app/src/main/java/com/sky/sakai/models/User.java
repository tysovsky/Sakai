package com.sky.sakai.models;

import java.io.Serializable;

public class User implements Serializable {
    public String Id;
    public String NetId;
    public String FullName;
    public String UserType;

    public User(String id, String netId, String fullName, String userType){
        Id = id;
        NetId = netId;
        FullName = fullName;
        UserType = userType;

    }
}
