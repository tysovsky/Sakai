package com.sky.sakai.models;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Grade implements Serializable {
    public String Grade;
    public String Points;
    public String ItemName;
    public String UserId;
    public String UserName;
    public String SiteId;


    public static ArrayList<Grade> parseJsonGradesList(String response){
        ArrayList<Grade> grades = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(response);

            JSONArray jsonAnouncements = json.getJSONArray("assignments");

            for (int i = 0; i < jsonAnouncements.length(); i++) {
                JSONObject jsonGrade = jsonAnouncements.getJSONObject(i);
                Grade grade = new Grade();

                grade.ItemName = jsonGrade.getString("itemName");
                grade.UserId = jsonGrade.getString("userId");
                grade.SiteId = json.getString("siteId");
                grade.UserName = jsonGrade.getString("userName");

                if(jsonGrade.getString("grade").equals("null"))
                    grade.Grade = "-";
                else
                    grade.Grade = jsonGrade.getString("grade");
                grade.Points = jsonGrade.getString("points");

                grades.add(grade);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return grades;
    }

}
