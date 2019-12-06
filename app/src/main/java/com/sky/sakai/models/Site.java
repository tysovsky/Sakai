package com.sky.sakai.models;

import com.sky.sakai.storage.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Site implements Serializable {

    public enum SiteType {
        COURSE(0);

        private final int value;
        private SiteType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
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

    public ArrayList<Announcement> getAnnouncements(){
        return DatabaseManager.getInstance().getAnnouncementsForSite(Id);
    }

    public ArrayList<Assignment> getAssignments(){
        return DatabaseManager.getInstance().getAssignmentsForSite(Id);
    }

    public ArrayList<Grade> getGrades(){
        return DatabaseManager.getInstance().getGradesForSite(Id);
    }

    public static ArrayList<Site> parseSitesListFromJson(String response){
        ArrayList<Site> sites = new ArrayList<>();

        try {
            JSONObject responseJson = new JSONObject(response);
            JSONArray sitesJson = responseJson.getJSONArray("site_collection");

            for (int i = 0; i < sitesJson.length(); i++){
                Site site = new Site();
                JSONObject siteJson = sitesJson.getJSONObject(i);

                String type = siteJson.getString("type");

                if (type.equals("course")){
                    site.Type = Site.SiteType.COURSE;

                    site.Id = siteJson.getString("id");
                    site.Title = siteJson.getString("title");
                    site.Description = siteJson.getString("description");
                    site.EntityUrl = siteJson.getString("entityURL");
                    site.EntityId = siteJson.getString("entityId");
                    site.Term = siteJson.getJSONObject("props").getString("term");

                    sites.add(site);
                }


            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sites;
    }

}
