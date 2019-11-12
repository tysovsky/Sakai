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
                //.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                //.addHeader("Accept-Encoding", "gzip, deflate, br")
                //.addHeader("Accept-Language", "en-US,en;q=0.5")
                //.addHeader("Cookie", "visid_incap_425582=Nq5iMLPZRNmPCGZbB7nNQ8WRJl0AAAAAQUIPAAAAAAALKLxyw7Er6zscDGgGhOCE; visid_incap_1149778=wMMBwmOhRsymPTegYJGDZ8T7KV0AAAAAQUIPAAAAAACSBksvMm1IamlB57hCMeQ2; visid_incap_425592=Xt/VO/OrT1afbOvdZDJEB9f7KV0AAAAAQUIPAAAAAADAC//kJWuAHCvh4+JsrFnt; visid_incap_708992=EdrmF11wTjqZTzFNzOPtMyNWZF0AAAAAQUIPAAAAAABoJomRVxJFR1eKrhJI7zl3; visid_incap_1006801=HYHxFhw0SZOwGS+Wm05lPvFGa10AAAAAQUIPAAAAAACNaSXPfqVxzKZ40mf/BGsc; visid_incap_425585=TncRhpECQRObUtyR6hOvFINDgl0AAAAAQUIPAAAAAABt5qaGAmNGbWXUyfFYOZtJ; EssUserTrk=516b3fea.59702fe1dc932; nlbi_1006801=vvA5FqerrRbYLnF53DfZeQAAAAArZ+3yfTZYgs0C8B0VZPoD; incap_ses_1171_1006801=gFdTBrmt2nBBkarbtjpAEGhkyF0AAAAAnhG2uu4BkwXrml3gxWTMEw==; JSESSIONID=F768F2168B283561AC9A569B71B014E1; nlbi_425582=55k4Ho1XQnVuTJX8ULT0pQAAAAD6UFXSIO/3TyLv40W0PvYc; incap_ses_702_425582=pmShO23VTUavdYjODgu+CY4syl0AAAAAw03C5Lmvc81psiXfYaEcVg==; incap_ses_703_708992=sArtAb0vL3eAXRPN8ZrBCcwXyl0AAAAA88Dh73r+DoOdieLRH0u4rA==; incap_ses_887_425582=AivReHlhbBBmAmMWzUJPDAR2yF0AAAAAs4ri7qFvLTJ4H7pWemRcFw==; incap_ses_886_425582=ihuREFzawCl/ZXXNQrVLDLgiyl0AAAAAmPP5Shuk8AR+8mk7lYuXHw==")
                //.addHeader("Content-Length", "154")
                //.addHeader("Content-Type", "application/x-www-form-urlencoded")
                //.addHeader("DNT", "1")
                //.addHeader("Host", "cas.rutgers.edu")
                //.addHeader("Origin", "https://cas.rutgers.edu")
                //.addHeader("Upgrade-Insecure-Requests", "1")
                //.addHeader("Upgrade-Insecure-Requests", "1")
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


    static Request getPortalRequest(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(SakaiURL.PORTAL).newBuilder();

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                //.addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Connection", "application/x-www-form-urlencoded")
                .addHeader("Content-Length", "154")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("DNT", "1")
                .addHeader("Host", "cas.rutgers.edu")
                .addHeader("Origin", "https://cas.rutgers.edu")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .get()
                .build();

        return request;
    }
}
