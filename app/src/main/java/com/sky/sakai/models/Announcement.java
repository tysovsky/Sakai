package com.sky.sakai.models;

import com.sky.sakai.storage.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

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

    public static ArrayList<Announcement> parseJsonAnnouncementsList(String response){
        ArrayList<Announcement> announcements = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(response);

            JSONArray jsonAnouncements = json.getJSONArray("announcement_collection");

            for (int i = 0; i < jsonAnouncements.length(); i++) {
                JSONObject jsonAnnouncement = jsonAnouncements.getJSONObject(i);
                Announcement announcement = new Announcement();
                announcement.Id = jsonAnnouncement.getString("id");
                announcement.AnnouncementId = jsonAnnouncement.getString("announcementId");
                announcement.SiteId = jsonAnnouncement.getString("siteId");
                announcement.CreatedBy = jsonAnnouncement.getString("createdByDisplayName");
                announcement.Title = jsonAnnouncement.getString("title");
                announcement.CreationDate = new Date(jsonAnnouncement.getLong("createdOn"));


                String body = jsonAnnouncement.getString("body");
                Whitelist whitelist = new Whitelist();
                whitelist.addTags("br");
                body = Jsoup.clean(body, whitelist);
                announcement.Body = body.replace("<br>", "\n").replace("&nbsp;", " ").replaceAll("[\n]{2,}", "\n");

                JSONArray jasonAttachment = jsonAnnouncement.getJSONArray("attachments");

                for (int j = 0; j < jasonAttachment.length(); j++) {
                    Attachment attachment = new Attachment();
                    attachment.Id = jasonAttachment.getJSONObject(j).getString("id");
                    attachment.Name = jasonAttachment.getJSONObject(j).getString("name");
                    attachment.Url = jasonAttachment.getJSONObject(j).getString("url");
                    attachment.Type = jasonAttachment.getJSONObject(j).getString("type");

                    announcement.Attachments.add(attachment);
                }


                announcements.add(announcement);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return announcements;
    }



}
