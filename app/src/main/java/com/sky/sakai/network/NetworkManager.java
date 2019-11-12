package com.sky.sakai.network;

import com.sky.sakai.models.Class;
import com.sky.sakai.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;

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
        void onClassesReceived(ArrayList<Class> classes);
    }

    private static NetworkManager instance;
    private final String TAG = NetworkManager.class.getSimpleName();

    private OkHttpClient httpClient;
    private PersistentCookieJar cookieJar;


    private NetworkManager(){

        cookieJar = new PersistentCookieJar();
        httpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
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

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                ArrayList<Class> classes = new ArrayList<>();

                try {
                    JSONObject jResponse = new JSONObject(response.body().string());
                    JSONArray sites = jResponse.getJSONArray("site_collection");

                    for (int i = 0; i < sites.length(); i++){
                        Class c = new Class();

                        c.EntityId = sites.getJSONObject(i).getString("entityId");
                        c.Title = sites.getJSONObject(i).getString("title");

                        classes.add(c);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

                listener.onClassesReceived(classes);



            }
        });
    }

}
