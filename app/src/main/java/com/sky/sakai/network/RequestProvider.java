package com.sky.sakai.network;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestProvider {

    static Request getLoginGETRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.LOGIN).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }

    static Request getLoginRequest(String username, String password, String lt){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.LOGIN).newBuilder();

        RequestBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("authenticationType", "Kerberos")
                .add("lt", lt)
                .add("execution", "e1s1")
                .add("_eventId", "submit")
                .add("submit", "LOGIN")
                .build();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(body)
                .build();

        return request;
    }

    static Request getSitesRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.SITES).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }

    static Request getAnnouncementsRequest(int d, int n){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.USER_ANNOUNCEMENTS + "?d=" + d + "&n=" + n).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }

    static Request getSiteAnnouncementsRequest(String siteId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.SITE_ANNOUNCEMENTS + siteId + ".json?d=3000&n=100").newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }

    static Request getAssignmentsRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.USER_ASSIGNMENTS).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }

    static Request getSiteAssignmentsRequest(String siteId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.SITE_ASSIGNMENTS + siteId + ".json").newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }

    static Request getSiteGradesRequest(String siteId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.SITE_GRADES + siteId + ".json").newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }

    static Request getAttachmentRequest(String attachmentURL){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(attachmentURL).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        return request;
    }
}
