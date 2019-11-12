package com.sky.sakai.models;

public class User {
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
