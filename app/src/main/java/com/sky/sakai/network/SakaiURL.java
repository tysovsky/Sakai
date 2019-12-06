package com.sky.sakai.network;

public class SakaiURL {
    public static final String  BASE_URL = "https://sakai.rutgers.edu/",
                                LOGIN = "https://cas.rutgers.edu/login?service=https%3A%2F%2Fsakai.rutgers.edu%2Fsakai-login-tool%2Fcontainer",
                                SITES = BASE_URL + "direct/site.json",
                                USER_ANNOUNCEMENTS = BASE_URL + "/direct/announcement/user.json",
                                SITE_ANNOUNCEMENTS = BASE_URL + "/direct/announcement/site/",
                                USER_ASSIGNMENTS = BASE_URL + "/direct/assignment/my.json",
                                SITE_ASSIGNMENTS = BASE_URL + "/direct/assignment/site/",
                                SITE_GRADES = BASE_URL + "direct/gradebook/site/";
}
