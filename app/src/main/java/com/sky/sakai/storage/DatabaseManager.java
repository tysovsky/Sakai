package com.sky.sakai.storage;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sky.sakai.SakaiApplication;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;
import com.sky.sakai.models.Site;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    final private static String DATABASE_NAME = "SakaiDB";
    final private static int DATABASE_VERSION = 1;

    public enum Tables {
        ANNOUNCEMENTS("ANNOUNCEMENTS"),
        ASSIGNMENTS("ASSIGNMENTS"),
        SITES("SITES"),
        ATTACHMENTS("ATTACHMENTS")
        ;

        private final String text;

        Tables(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum Columns {
        ID("ID"),
        TITLE("TITLE"),
        DESCRIPTION("DESCRIPTION"),
        ENTITY_ID("ENTITY_ID"),
        ENTITY_URL("ENTITY_URL"),
        TERM("TERM"),
        TYPE("TYPE"),
        ANNOUNCEMENT_ID("ANNOUNCEMENT_ID"),
        SITE_ID("SITE_ID"),
        CREATED_BY("CREATED_BY"),
        BODY("BODY"),
        CREATION_DATE("CREATION_DATE"),
        INSTRUCTIONS("INSTRUCTIONS"),
        DUE_TIME("DUE_TIME"),
        CREATED_TIME("CREATED_TIME"),
        CLOSED_TIME("CLOSED_TIME"),
        OPEN_TIME("OPEN_TIME"),
        LAST_MODIFIED_DATE("LAST_MODIFIED_DATE"),
        NAME("NAME"),
        URL("URL"),
        ;

        private final String name;

        Columns(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static DatabaseManager mDatabaseManager;

    private DatabaseManager(){
        super(SakaiApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ATTACHMENTS_TABLE_QUERY = "CREATE TABLE " + Tables.ATTACHMENTS +
                "(" +
                Columns.ID + " TEXT PRIMARY KEY, " +
                Columns.NAME + " TEXT, " +
                Columns.URL + " TEXT, " +
                Columns.TYPE + " TEXT " +
                ");";
        db.execSQL(CREATE_ATTACHMENTS_TABLE_QUERY);

        String CREATE_ANNOUNCEMENTS_TABLE_QUERY = "CREATE TABLE " + Tables.ANNOUNCEMENTS +
                "(" +
                Columns.ID + " TEXT PRIMARY KEY, " +
                Columns.ANNOUNCEMENT_ID + " TEXT NOT NULL, " +
                Columns.SITE_ID + " TEXT NOT NULL, " +
                Columns.CREATED_BY + " TEXT NOT NULL, " +
                Columns.TITLE + " TEXT NOT NULL, " +
                Columns.BODY + " TEXT NOT NULL, " +
                Columns.CREATION_DATE + " DATETIME NOT NULL " +
                ");";
        db.execSQL(CREATE_ANNOUNCEMENTS_TABLE_QUERY);

        String CREATE_ASSIGNMENTS_TABLE_QUERY = "CREATE TABLE " + Tables.ASSIGNMENTS +
                "(" +
                Columns.ID + " TEXT PRIMARY KEY, " +
                Columns.ENTITY_ID + " TEXT NOT NULL, " +
                Columns.TITLE + " TEXT NOT NULL, " +
                Columns.INSTRUCTIONS + " TEXT, " +
                Columns.ENTITY_URL + " TEXT NOT NULL, " +
                Columns.CREATED_TIME + " DATETIME NOT NULL, " +
                Columns.DUE_TIME + " DATETIME NOT NULL, " +
                Columns.CLOSED_TIME + " DATETIME, " +
                Columns.OPEN_TIME + " DATETIME, " +
                Columns.LAST_MODIFIED_DATE + " DATETIME " +
                ");";
        db.execSQL(CREATE_ASSIGNMENTS_TABLE_QUERY);

        String CREATE_SITES_TABLE_QUERY = "CREATE TABLE " + Tables.SITES +
                "(" +
                Columns.ID + " TEXT PRIMARY KEY, " +
                Columns.TITLE + " TEXT NOT NULL, " +
                Columns.DESCRIPTION + " TEXT NOT NULL, " +
                Columns.ENTITY_ID + " TEXT, " +
                Columns.ENTITY_URL + " TEXT NOT NULL, " +
                Columns.TERM + " TEXT NOT NULL, " +
                Columns.TYPE + " INTEGER NOT NULL, " +
                ");";
        db.execSQL(CREATE_SITES_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public static DatabaseManager getInstance(){
        if(mDatabaseManager == null)
            mDatabaseManager = new DatabaseManager();

        return mDatabaseManager;
    }


    public ArrayList<Announcement> getAllAnnouncements(){
        return null;
    }

    public ArrayList<Assignment> getAllAssignments(){
        return null;
    }

    public ArrayList<Site> getAllSites(){
        return null;
    }

    public void saveAnnouncement(Announcement toSave){

    }

    public void saveAssignment(Assignment toSave){

    }

    public void saveSite(Site toSave){

    }
}
