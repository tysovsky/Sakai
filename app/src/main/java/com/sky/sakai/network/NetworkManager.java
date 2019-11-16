package com.sky.sakai.network;

import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;
import com.sky.sakai.models.Attachment;
import com.sky.sakai.models.Site;
import com.sky.sakai.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class NetworkManager {


    public interface OnLoginListener{
        void onLoginSuccess(User loggedInUser);
        void onLoginFailure();
    }

    public interface OnClassesRequestListener{
        void onClassesReceived(ArrayList<Site> sites);
    }

    public interface OnAnnouncementsRequestListener{
        void onAnnouncementsReceived(ArrayList<Announcement> announcements);
    }

    public interface OnAssignmentsRequestListener{
        void onAssignmentsReceived(ArrayList<Assignment> assignments);
    }

    private static NetworkManager instance;
    private final String TAG = NetworkManager.class.getSimpleName();

    private OkHttpClient httpClient;
    private PersistentCookieJar cookieJar;


    private NetworkManager(){

        cookieJar = new PersistentCookieJar();
        httpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(30, TimeUnit.MINUTES)
                .callTimeout(30, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .build();
    }

    public static NetworkManager getInstance(){
        if(instance == null){
            instance = new NetworkManager();
        }

        return instance;
    }


    public void login(final String username, final String password, final OnLoginListener listener){

        cookieJar.clear();
        httpClient.newCall(RequestProvider.getLoginGETRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onLoginFailure();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String lt = Jsoup.parse(response.body().string()).select("[name=lt]").val();


                httpClient.newCall(RequestProvider.getLoginRequest(username, password, lt)).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        listener.onLoginFailure();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String res = response.body().string();

                        if(res.contains("Central  Authentication  Service")){
                            listener.onLoginFailure();
                        }
                        else{
                            Document doc = Jsoup.parse(res);

                            Element e = doc.selectFirst("script");
                            String jsonStr = e.data();
                            jsonStr = jsonStr.substring(jsonStr.indexOf("var portal = ") + 13, jsonStr.lastIndexOf("}")+1);

                            try {
                                JSONObject jsonObject = new JSONObject(jsonStr);
                                JSONObject user = jsonObject.getJSONObject("user");

                                String Id = user.getString("id");
                                String NetID = user.getString("eid");
                                String FullName = doc.select("div.Mrphs-userNav__submenuitem--fullname").text();
                                String UserType = user.getString("userType");

                                User loggedInUser = new User(Id, NetID, FullName, UserType);

                                listener.onLoginSuccess(loggedInUser);

                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }

                        }
                    }
                });

            }
        });

    }

    public void getSites(final OnClassesRequestListener listener){
        httpClient.newCall(RequestProvider.getSitesRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ArrayList<Site> sites = new ArrayList<>();

                try {
                    JSONObject responseJson = new JSONObject(response.body().string());
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


                            site.save();
                            sites.add(site);
                        }


                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.onClassesReceived(sites);
            }
        });
    }

    public void getAllAnnouncements(int n, int d, final OnAnnouncementsRequestListener listener){
        httpClient.newCall(RequestProvider.getAnnouncementsRequest(300, 100)).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ArrayList<Announcement> announcements = new ArrayList<>();
                    JSONObject json = new JSONObject(response.body().string());

                    JSONArray jsonAnouncements = json.getJSONArray("announcement_collection");

                    for (int i = 0; i < jsonAnouncements.length(); i++){
                        JSONObject jsonAnnouncement = jsonAnouncements.getJSONObject(i);
                        Announcement announcement = new Announcement();
                        announcement.Id = jsonAnnouncement.getString("id");
                        announcement.AnnouncementId = jsonAnnouncement.getString("announcementId");
                        announcement.SiteId = jsonAnnouncement.getString("siteId");
                        announcement.CreatedBy = jsonAnnouncement.getString("createdByDisplayName");
                        announcement.Title = jsonAnnouncement.getString("title");
                        announcement.CreationDate = new Date(jsonAnnouncement.getLong("createdOn"));
                        //announcement.Body = Jsoup.parse(jsonAnnouncement.getString("body").replace("<br />", "\n")).text();


                        String body = jsonAnnouncement.getString("body");
                        Whitelist whitelist = new Whitelist();
                        whitelist.addTags("br");
                        body = Jsoup.clean(body, whitelist);
                        announcement.Body = body.replace("<br>", "\n").replace("&nbsp;", " ");

                        JSONArray jasonAttachment = jsonAnnouncement.getJSONArray("attachments");

                        for (int j = 0; j < jasonAttachment.length(); j++){
                            Attachment attachment = new Attachment();
                            attachment.Id = jasonAttachment.getJSONObject(j).getString("id");
                            attachment.Name = jasonAttachment.getJSONObject(j).getString("name");
                            attachment.Url = jasonAttachment.getJSONObject(j).getString("url");
                            attachment.Type = jasonAttachment.getJSONObject(j).getString("type");

                            announcement.Attachments.add(attachment);
                        }


                        announcement.save();
                        announcements.add(announcement);
                    }

                    listener.onAnnouncementsReceived(announcements);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getAllAssignments(final OnAssignmentsRequestListener listener){
        httpClient.newCall(RequestProvider.getAssignmentsRequest()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    ArrayList<Assignment> assignments = new ArrayList<>();
                    JSONObject json = new JSONObject(response.body().string());

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

                        String instructions = assignmentJson.getString("instructions");
                        Whitelist whitelist = new Whitelist();
                        whitelist.addTags("br");
                        instructions = Jsoup.clean(instructions, whitelist);
                        assignment.Instructions = instructions.replace("<br>", "\n").replace("&nbsp;", " ");

                        JSONArray jasonAttachment = assignmentJson.getJSONArray("attachments");

                        for (int j = 0; j < jasonAttachment.length(); j++){
                            Attachment attachment = new Attachment();
                            attachment.Name = jasonAttachment.getJSONObject(j).getString("name");
                            attachment.Url = jasonAttachment.getJSONObject(j).getString("url");

                            assignment.Attachments.add(attachment);
                        }

                        assignment.save();
                        assignments.add(assignment);

                    }

                    Collections.sort(assignments, new Comparator<Assignment>() {
                        @Override
                        public int compare(Assignment a, Assignment b) {
                            return a.DueTime.compareTo(b.DueTime);
                        }
                    });


                    listener.onAssignmentsReceived(assignments);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
