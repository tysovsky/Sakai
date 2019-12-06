package com.sky.sakai.models;

import com.sky.sakai.storage.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Assignment implements Serializable {

    public String Id;
    public String SiteId;
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

    public static ArrayList<Assignment> parseJsonAssignmentsList(String response){
        ArrayList<Assignment> assignments = new ArrayList<>();
        try {

            JSONObject json = new JSONObject(response);

            JSONArray assignmentsJson = json.getJSONArray("assignment_collection");

            for (int i = 0; i < assignmentsJson.length(); i++){
                JSONObject assignmentJson = assignmentsJson.getJSONObject(i);

                Assignment assignment = new Assignment();

                assignment.Id = assignmentJson.getString("id");
                assignment.EntityId = assignmentJson.getString("entityId");
                assignment.Title = Jsoup.clean(assignmentJson.getString("title"), new Whitelist());
                assignment.CreatedTime = new Date(assignmentJson.getJSONObject("timeCreated").getLong("time"));
                assignment.DueTime = new Date(assignmentJson.getJSONObject("dueTime").getLong("time"));
                assignment.ClosedTime = new Date(assignmentJson.getJSONObject("closeTime").getLong("time"));
                assignment.OpenTime = new Date(assignmentJson.getJSONObject("openTime").getLong("time"));
                assignment.LastModifiedDate = new Date(assignmentJson.getJSONObject("timeLastModified").getLong("time"));
                assignment.EntityURL = assignmentJson.getString("entityURL");
                assignment.SiteId = assignmentJson.getString("context");

                String instructions = assignmentJson.getString("instructions");
                Whitelist whitelist = new Whitelist();
                whitelist.addTags("br");
                instructions = Jsoup.clean(instructions, whitelist);
                assignment.Instructions = instructions.replace("<br>", "\n").replace("&nbsp;", " ").replaceAll("[\n]{2,}", "\n");;

                JSONArray jasonAttachment = assignmentJson.getJSONArray("attachments");

                for (int j = 0; j < jasonAttachment.length(); j++){
                    Attachment attachment = new Attachment();
                    attachment.Name = jasonAttachment.getJSONObject(j).getString("name");
                    attachment.Url = jasonAttachment.getJSONObject(j).getString("url");
                    attachment.Id = assignment.Id;

                    assignment.Attachments.add(attachment);
                }

                assignments.add(assignment);

            }

            Collections.sort(assignments, new Comparator<Assignment>() {
                @Override
                public int compare(Assignment a, Assignment b) {
                    return a.DueTime.compareTo(b.DueTime);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

        return assignments;
    }
}
