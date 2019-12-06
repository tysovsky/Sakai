package com.sky.sakai.network;

import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;
import com.sky.sakai.models.Attachment;
import com.sky.sakai.models.Grade;
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

    public interface OnGradesRequestListener{
        void onGradesReceived(ArrayList<Grade> grades);
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
                listener.onClassesReceived(Site.parseSitesListFromJson(response.body().string()));
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
                listener.onAnnouncementsReceived(Announcement.parseJsonAnnouncementsList(response.body().string()));
            }
        });
    }

    public void getAnnouncementsForSite(String siteId, final OnAnnouncementsRequestListener listener){
        httpClient.newCall(RequestProvider.getSiteAnnouncementsRequest(siteId)).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                listener.onAnnouncementsReceived(Announcement.parseJsonAnnouncementsList(response.body().string()));
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
                listener.onAssignmentsReceived(Assignment.parseJsonAssignmentsList(response.body().string()));
            }
        });
    }

    public void getAssignmentsForSite(String siteId, final OnAssignmentsRequestListener listener){
        httpClient.newCall(RequestProvider.getSiteAssignmentsRequest(siteId)).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                listener.onAssignmentsReceived(Assignment.parseJsonAssignmentsList(response.body().string()));
            }
        });
    }

    public void getGradesForSite(String siteId, final OnGradesRequestListener listener){
        httpClient.newCall(RequestProvider.getSiteGradesRequest(siteId)).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                listener.onGradesReceived(Grade.parseJsonGradesList(response.body().string()));
            }
        });
    }

}
