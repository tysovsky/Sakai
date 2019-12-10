package com.sky.sakai.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sky.sakai.SakaiApplication;
import com.sky.sakai.models.Announcement;
import com.sky.sakai.models.Assignment;
import com.sky.sakai.models.Attachment;
import com.sky.sakai.models.Grade;
import com.sky.sakai.models.Site;

import org.apache.commons.text.StringSubstitutor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager extends SQLiteOpenHelper {

    final private static String DATABASE_NAME = "SakaiDB";
    final private static int DATABASE_VERSION = 2;



    public enum Tables {
        ANNOUNCEMENTS("ANNOUNCEMENTS"),
        ASSIGNMENTS("ASSIGNMENTS"),
        SITES("SITES"),
        ATTACHMENTS("ATTACHMENTS"),
        ATTACHMENTS_LOOKUP("ATTACHMENTS_LOOKUP"),
        GRADES("GRADES")
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
        ATTACHMENT_ID("ATTACHMENT_ID"),
        GRADE("GRADE"),
        POINTS("POINTS"),
        ITEM_NAME("ITEM_NAME"),
        USER_ID("USER_ID"),
        USER_NAME("USER_NAME"),
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
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private DatabaseManager(){
        super(SakaiApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ATTACHMENTS_TABLE_QUERY = "CREATE TABLE " + Tables.ATTACHMENTS +
                "(" +
                Columns.ID + " TEXT, " +
                Columns.NAME + " TEXT, " +
                Columns.URL + " TEXT, " +
                Columns.TYPE + " TEXT " +
                ");";
        db.execSQL(CREATE_ATTACHMENTS_TABLE_QUERY);

        String CREATE_ATTACHMENTS_LOOKUP_TABLE_QUERY = "CREATE TABLE " + Tables.ATTACHMENTS_LOOKUP +
                "(" +
                Columns.ID + " TEXT, " +
                Columns.ATTACHMENT_ID + " TEXT " +
                ");";
        db.execSQL(CREATE_ATTACHMENTS_LOOKUP_TABLE_QUERY);

        String CREATE_ANNOUNCEMENTS_TABLE_QUERY = "CREATE TABLE " + Tables.ANNOUNCEMENTS +
                "(" +
                Columns.ID + " TEXT PRIMARY KEY, " +
                Columns.ANNOUNCEMENT_ID + " TEXT NOT NULL, " +
                Columns.SITE_ID + " TEXT NOT NULL, " +
                Columns.CREATED_BY + " TEXT NOT NULL, " +
                Columns.TITLE + " TEXT NOT NULL, " +
                Columns.BODY + " TEXT NOT NULL, " +
                Columns.CREATION_DATE + " TEXT NOT NULL " +
                ");";
        db.execSQL(CREATE_ANNOUNCEMENTS_TABLE_QUERY);

        String CREATE_ASSIGNMENTS_TABLE_QUERY = "CREATE TABLE " + Tables.ASSIGNMENTS +
                "(" +
                Columns.ID + " TEXT PRIMARY KEY, " +
                Columns.SITE_ID + " TEXT NOT NULL, " +
                Columns.ENTITY_ID + " TEXT NOT NULL, " +
                Columns.TITLE + " TEXT NOT NULL, " +
                Columns.INSTRUCTIONS + " TEXT, " +
                Columns.ENTITY_URL + " TEXT NOT NULL, " +
                Columns.CREATED_TIME + " TEXT NOT NULL, " +
                Columns.DUE_TIME + " TEXT NOT NULL, " +
                Columns.CLOSED_TIME + " TEXT, " +
                Columns.OPEN_TIME + " TEXT, " +
                Columns.LAST_MODIFIED_DATE + " TEXT " +
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
                Columns.TYPE + " INTEGER NOT NULL " +
                ");";
        db.execSQL(CREATE_SITES_TABLE_QUERY);

        String CREATE_GRADES_TABLE_QUERY = "CREATE TABLE " + Tables.GRADES +
                "(" +
                Columns.ITEM_NAME + " TEXT NOT NULL, " +
                Columns.GRADE + " TEXT NOT NULL, " +
                Columns.POINTS + " TEXT NOT NULL, " +
                Columns.USER_ID + " TEXT NOT NULL, " +
                Columns.USER_NAME + " TEXT NOT NULL, " +
                Columns.SITE_ID + " TEXT NOT NULL " +
                ");";
        db.execSQL(CREATE_GRADES_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ANNOUNCEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SITES);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ASSIGNMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ATTACHMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ATTACHMENTS_LOOKUP);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.GRADES);

        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static DatabaseManager getInstance(){
        if(mDatabaseManager == null)
            mDatabaseManager = new DatabaseManager();

        return mDatabaseManager;
    }


    /*----ANNOUNCEMENTS-----*/
    public ArrayList<Announcement> getAllAnnouncements(){
        String QUERY_TEMPLATE =
                "SELECT * FROM ${ANNOUNCEMENTS} ORDER BY ${CREATION_DATE} DESC LIMIT 100";
        Map valuesMap = new HashMap();
        valuesMap.put("ANNOUNCEMENTS", Tables.ANNOUNCEMENTS.toString());
        valuesMap.put("CREATION_DATE", Columns.CREATION_DATE.toString());

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Announcement> announcements = new ArrayList<>();

        try{
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                Announcement announcement = new Announcement();
                announcement.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                announcement.AnnouncementId = cursor.getString(cursor.getColumnIndex(Columns.ANNOUNCEMENT_ID.toString()));
                announcement.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                announcement.Body = cursor.getString(cursor.getColumnIndex(Columns.BODY.toString()));
                announcement.CreatedBy = cursor.getString(cursor.getColumnIndex(Columns.CREATED_BY.toString()));
                announcement.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));

                announcement.CreationDate = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CREATION_DATE.toString())));


                announcement.Attachments = getAttachmentsForId(announcement.AnnouncementId);

                announcements.add(announcement);

                cursor.moveToNext();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return announcements;
    }

    public ArrayList<Announcement> getAnnouncementsForSite(String siteId){
        String QUERY_TEMPLATE =
                "SELECT * FROM ${ANNOUNCEMENTS} WHERE ${SITE_ID} = '${TARGET}' ORDER BY ${CREATION_DATE} DESC";
        Map valuesMap = new HashMap();
        valuesMap.put("ANNOUNCEMENTS", Tables.ANNOUNCEMENTS.toString());
        valuesMap.put("CREATION_DATE", Columns.CREATION_DATE.toString());
        valuesMap.put("SITE_ID", Columns.SITE_ID.toString());
        valuesMap.put("TARGET", siteId);

        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Announcement> announcements = new ArrayList<>();

        try{
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                Announcement announcement = new Announcement();
                announcement.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                announcement.AnnouncementId = cursor.getString(cursor.getColumnIndex(Columns.ANNOUNCEMENT_ID.toString()));
                announcement.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                announcement.Body = cursor.getString(cursor.getColumnIndex(Columns.BODY.toString()));
                announcement.CreatedBy = cursor.getString(cursor.getColumnIndex(Columns.CREATED_BY.toString()));
                announcement.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));

                announcement.CreationDate = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CREATION_DATE.toString())));


                announcement.Attachments = getAttachmentsForId(announcement.AnnouncementId);

                announcements.add(announcement);

                cursor.moveToNext();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return announcements;
    }

    public Announcement getAnnouncement(String id){
        String QUERY_TEMPLATE =
                "SELECT *" +
                        "FROM ${ANNOUNCEMENTS} " +
                        "WHERE ${ANNOUNCEMENT_ID} = '${A_ID}';";

        Map valuesMap = new HashMap();
        valuesMap.put("ANNOUNCEMENTS", Tables.ANNOUNCEMENTS.toString());
        valuesMap.put("ANNOUNCEMENT_ID", Columns.ANNOUNCEMENT_ID.toString());
        valuesMap.put("A_ID", id);


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Announcement announcement = new Announcement();
                announcement.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                announcement.AnnouncementId = cursor.getString(cursor.getColumnIndex(Columns.ANNOUNCEMENT_ID.toString()));
                announcement.Body = cursor.getString(cursor.getColumnIndex(Columns.BODY.toString()));
                announcement.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                announcement.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));
                announcement.CreatedBy = cursor.getString(cursor.getColumnIndex(Columns.CREATED_BY.toString()));


                announcement.CreationDate = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CREATION_DATE.toString())));

                announcement.Attachments = getAttachmentsForId(announcement.AnnouncementId);


                return announcement;
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }


    public boolean saveAnnouncement(Announcement toSave){

        if (getAnnouncement(toSave.AnnouncementId) != null)
            return false;

        ContentValues values = new ContentValues();
        values.put(Columns.ANNOUNCEMENT_ID.toString(), toSave.AnnouncementId);
        values.put(Columns.ID.toString(), toSave.Id);
        values.put(Columns.BODY.toString(), toSave.Body);
        values.put(Columns.SITE_ID.toString(), toSave.SiteId);
        values.put(Columns.TITLE.toString(), toSave.Title);
        values.put(Columns.CREATED_BY.toString(), toSave.CreatedBy);
        values.put(Columns.CREATION_DATE.toString(), dateFormatter.format(toSave.CreationDate));

        SQLiteDatabase db = getWritableDatabase();
        db.insert(Tables.ANNOUNCEMENTS.toString(), null, values);

        for (Attachment attachment: toSave.Attachments){
            saveAttachment(attachment);

            ContentValues attachment_lookup_values = new ContentValues();
            attachment_lookup_values.put(Columns.ID.toString(), toSave.AnnouncementId);
            attachment_lookup_values.put(Columns.ATTACHMENT_ID.toString(), attachment.Id);
            db.insert(Tables.ATTACHMENTS_LOOKUP.toString(), null, attachment_lookup_values);
        }

        return true;
    }


    /*----ASSIGNMENTS-----*/
    public ArrayList<Assignment> getAllAssignments(){
        String QUERY_TEMPLATE =
                "SELECT * FROM ${ASSIGNMENTS} ORDER BY ${DUE_DATE} DESC LIMIT 100";
        Map valuesMap = new HashMap();
        valuesMap.put("ASSIGNMENTS", Tables.ASSIGNMENTS.toString());
        valuesMap.put("DUE_DATE", Columns.DUE_TIME.toString());


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Assignment> assignments = new ArrayList<>();

        try{
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                Assignment assignment = new Assignment();
                assignment.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                assignment.EntityId = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_ID.toString()));
                assignment.EntityURL = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_URL.toString()));
                assignment.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                assignment.Instructions = cursor.getString(cursor.getColumnIndex(Columns.INSTRUCTIONS.toString()));
                assignment.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));

                assignment.OpenTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.OPEN_TIME.toString())));
                assignment.DueTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.DUE_TIME.toString())));
                assignment.ClosedTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CLOSED_TIME.toString())));
                assignment.CreatedTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CREATED_TIME.toString())));
                assignment.LastModifiedDate = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.LAST_MODIFIED_DATE.toString())));


                assignment.Attachments = getAttachmentsForId(assignment.Id);

                assignments.add(assignment);

                cursor.moveToNext();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return assignments;
    }

    public ArrayList<Assignment> getAssignmentsForSite(String id){
        String QUERY_TEMPLATE =
                "SELECT * FROM ${ASSIGNMENTS} WHERE ${SITE_ID} = '${TARGET}' ORDER BY ${DUE_DATE} DESC";
        Map valuesMap = new HashMap();
        valuesMap.put("ASSIGNMENTS", Tables.ASSIGNMENTS.toString());
        valuesMap.put("DUE_DATE", Columns.DUE_TIME.toString());
        valuesMap.put("SITE_ID", Columns.SITE_ID.toString());
        valuesMap.put("TARGET", id);



        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Assignment> assignments = new ArrayList<>();

        try{
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                Assignment assignment = new Assignment();
                assignment.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                assignment.EntityId = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_ID.toString()));
                assignment.EntityURL = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_URL.toString()));
                assignment.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                assignment.Instructions = cursor.getString(cursor.getColumnIndex(Columns.INSTRUCTIONS.toString()));
                assignment.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));

                assignment.OpenTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.OPEN_TIME.toString())));
                assignment.DueTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.DUE_TIME.toString())));
                assignment.ClosedTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CLOSED_TIME.toString())));
                assignment.CreatedTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CREATED_TIME.toString())));
                assignment.LastModifiedDate = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.LAST_MODIFIED_DATE.toString())));


                assignment.Attachments = getAttachmentsForId(assignment.Id);

                assignments.add(assignment);

                cursor.moveToNext();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return assignments;
    }

    public Assignment getAssignment(String id){
        String QUERY_TEMPLATE =
                "SELECT *" +
                        "FROM ${ASSIGNMENTS} " +
                        "WHERE ${ID} = '${TARGET}';";

        Map valuesMap = new HashMap();
        valuesMap.put("ASSIGNMENTS", Tables.ASSIGNMENTS.toString());
        valuesMap.put("ID", Columns.ID.toString());
        valuesMap.put("TARGET", id);


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Assignment assignment = new Assignment();
                assignment.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                assignment.EntityId = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_ID.toString()));
                assignment.EntityURL = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_URL.toString()));
                assignment.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                assignment.Instructions = cursor.getString(cursor.getColumnIndex(Columns.INSTRUCTIONS.toString()));
                assignment.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));

                assignment.OpenTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.OPEN_TIME.toString())));
                assignment.DueTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.DUE_TIME.toString())));
                assignment.ClosedTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CLOSED_TIME.toString())));
                assignment.CreatedTime = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.CREATED_TIME.toString())));
                assignment.LastModifiedDate = dateFormatter.parse(cursor.getString(cursor.getColumnIndex(Columns.LAST_MODIFIED_DATE.toString())));


                assignment.Attachments = getAttachmentsForId(assignment.Id);


                return assignment;
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean saveAssignment(Assignment toSave){
        if (getAssignment(toSave.Id) != null)
            return false;

        ContentValues values = new ContentValues();
        values.put(Columns.ENTITY_ID.toString(), toSave.EntityId);
        values.put(Columns.ENTITY_URL.toString(), toSave.EntityURL);
        values.put(Columns.ID.toString(), toSave.Id);
        values.put(Columns.INSTRUCTIONS.toString(), toSave.Instructions);
        values.put(Columns.TITLE.toString(), toSave.Title);
        values.put(Columns.SITE_ID.toString(), toSave.SiteId);

        values.put(Columns.DUE_TIME.toString(), dateFormatter.format(toSave.DueTime));
        values.put(Columns.CLOSED_TIME.toString(), dateFormatter.format(toSave.ClosedTime));
        values.put(Columns.CREATED_TIME.toString(), dateFormatter.format(toSave.CreatedTime));
        values.put(Columns.OPEN_TIME.toString(), dateFormatter.format(toSave.OpenTime));
        values.put(Columns.LAST_MODIFIED_DATE.toString(), dateFormatter.format(toSave.LastModifiedDate));

        SQLiteDatabase db = getWritableDatabase();
        db.insert(Tables.ASSIGNMENTS.toString(), null, values);

        for (Attachment attachment: toSave.Attachments){
            saveAttachment(attachment);

            ContentValues attachment_lookup_values = new ContentValues();
            attachment_lookup_values.put(Columns.ID.toString(), toSave.Id);
            attachment_lookup_values.put(Columns.ATTACHMENT_ID.toString(), attachment.Id);
            db.insert(Tables.ATTACHMENTS_LOOKUP.toString(), null, attachment_lookup_values);
        }

        return true;
    }


    /*----SITES-----*/
    public ArrayList<Site> getAllSites(){
        String QUERY_TEMPLATE =
                "SELECT *" +
                        "FROM ${SITES};";

        Map valuesMap = new HashMap();
        valuesMap.put("SITES", Tables.SITES.toString());


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Site> sites = new ArrayList<>();

        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Site site = new Site();

                site.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                site.EntityId = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_ID.toString()));
                site.EntityUrl = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_URL.toString()));
                site.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                site.Description = cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION.toString()));
                site.Term = cursor.getString(cursor.getColumnIndex(Columns.TERM.toString()));
                site.Type = Site.SiteType.COURSE;

                sites.add(site);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return sites;
    }

    public Site getSite(String id){
        String QUERY_TEMPLATE =
                "SELECT *" +
                        "FROM ${SITES} " +
                        "WHERE ${ID} = '${TARGET}';";

        Map valuesMap = new HashMap();
        valuesMap.put("SITES", Tables.SITES.toString());
        valuesMap.put("ID", Columns.ID.toString());
        valuesMap.put("TARGET", id);


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Site site = new Site();

                site.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                site.EntityId = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_ID.toString()));
                site.EntityUrl = cursor.getString(cursor.getColumnIndex(Columns.ENTITY_URL.toString()));
                site.Title = cursor.getString(cursor.getColumnIndex(Columns.TITLE.toString()));
                site.Description = cursor.getString(cursor.getColumnIndex(Columns.DESCRIPTION.toString()));
                site.Term = cursor.getString(cursor.getColumnIndex(Columns.TERM.toString()));
                site.Type = Site.SiteType.COURSE;

                return site;
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean saveSite(Site toSave){
        if (getSite(toSave.Id) != null)
            return false;

        ContentValues values = new ContentValues();
        values.put(Columns.ENTITY_ID.toString(), toSave.EntityId);
        values.put(Columns.DESCRIPTION.toString(), toSave.Description);
        values.put(Columns.ENTITY_URL.toString(), toSave.EntityUrl);
        values.put(Columns.ID.toString(), toSave.Id);
        values.put(Columns.TERM.toString(), toSave.Term);
        values.put(Columns.TITLE.toString(), toSave.Title);
        values.put(Columns.TYPE.toString(), toSave.Type.getValue());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(Tables.SITES.toString(), null, values);


        return true;
    }


    /*-----GRADES----*/

    public Grade getGrade(String siteId, String itemName){
        String QUERY_TEMPLATE =
                "SELECT * FROM ${GRADES} " +
                        "WHERE ${SITE_ID} = '${TARGET_SITE_ID}' AND ${ITEM_NAME} = '${TARGET_ITEM_NAME}';";

        Map valuesMap = new HashMap();
        valuesMap.put("GRADES", Tables.GRADES.toString());
        valuesMap.put("SITE_ID", Columns.SITE_ID.toString());
        valuesMap.put("TARGET_SITE_ID", siteId);
        valuesMap.put("ITEM_NAME", Columns.ITEM_NAME.toString());
        valuesMap.put("TARGET_ITEM_NAME", itemName);


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Grade grade = new Grade();

                grade.ItemName = cursor.getString(cursor.getColumnIndex(Columns.ITEM_NAME.toString()));
                grade.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));
                grade.Grade = cursor.getString(cursor.getColumnIndex(Columns.GRADE.toString()));
                grade.Points = cursor.getString(cursor.getColumnIndex(Columns.POINTS.toString()));
                grade.UserId = cursor.getString(cursor.getColumnIndex(Columns.USER_ID.toString()));
                grade.UserName = cursor.getString(cursor.getColumnIndex(Columns.USER_NAME.toString()));

                return grade;
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean saveGrade(Grade toSave){
        if(getGrade(toSave.SiteId, toSave.ItemName) != null)
            return false;

        ContentValues values = new ContentValues();
        values.put(Columns.ITEM_NAME.toString(), toSave.ItemName);
        values.put(Columns.SITE_ID.toString(), toSave.SiteId);
        values.put(Columns.GRADE.toString(), toSave.Grade);
        values.put(Columns.POINTS.toString(), toSave.Points);
        values.put(Columns.USER_ID.toString(), toSave.UserId);
        values.put(Columns.USER_NAME.toString(), toSave.UserName);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(Tables.GRADES.toString(), null, values);


        return true;

    }

    public ArrayList<Grade> getGradesForSite(String siteId){
        String QUERY_TEMPLATE =
                "SELECT * FROM ${GRADES} " +
                        "WHERE ${SITE_ID} = '${TARGET_SITE_ID}';";

        Map valuesMap = new HashMap();
        valuesMap.put("GRADES", Tables.GRADES.toString());
        valuesMap.put("SITE_ID", Columns.SITE_ID.toString());
        valuesMap.put("TARGET_SITE_ID", siteId);


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Grade> grades = new ArrayList<>();

        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Grade grade = new Grade();

                grade.ItemName = cursor.getString(cursor.getColumnIndex(Columns.ITEM_NAME.toString()));
                grade.SiteId = cursor.getString(cursor.getColumnIndex(Columns.SITE_ID.toString()));
                grade.Grade = cursor.getString(cursor.getColumnIndex(Columns.GRADE.toString()));
                grade.Points = cursor.getString(cursor.getColumnIndex(Columns.POINTS.toString()));
                grade.UserId = cursor.getString(cursor.getColumnIndex(Columns.USER_ID.toString()));
                grade.UserName = cursor.getString(cursor.getColumnIndex(Columns.USER_NAME.toString()));

                grades.add(grade);

                cursor.moveToNext();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return grades;
    }

    /*----ATTACHMENTS----*/
    public void saveAttachment(Attachment toSave){
        ContentValues values = new ContentValues();
        values.put(Columns.ID.toString(), toSave.Id);
        values.put(Columns.NAME.toString(), toSave.Name);
        values.put(Columns.URL.toString(), toSave.Url);
        values.put(Columns.TYPE.toString(), toSave.Type);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(Tables.ATTACHMENTS.toString(), null, values);
    }

    public ArrayList<Attachment> getAttachmentsForId(String id){

        String QUERY_TEMPLATE = "SELECT ${ATTACHMENTS}.${ID}, ${ATTACHMENTS}.${TYPE}, ${ATTACHMENTS}.${NAME}, ${ATTACHMENTS}.${URL} " +
                "FROM ${ATTACHMENTS_LOOKUP} " +
                "INNER JOIN ${ATTACHMENTS} ON ${ATTACHMENTS_LOOKUP}.${ATTACHMENT_ID} = ${ATTACHMENTS}.${ID} " +
                "WHERE ${ATTACHMENTS_LOOKUP}.${ID} = '${TARGET}';";

        Map valuesMap = new HashMap();
        valuesMap.put("ATTACHMENTS", Tables.ATTACHMENTS.toString());
        valuesMap.put("ATTACHMENTS_LOOKUP", Tables.ATTACHMENTS_LOOKUP.toString());
        valuesMap.put("ID", Columns.ID.toString());
        valuesMap.put("TYPE", Columns.TYPE.toString());
        valuesMap.put("NAME", Columns.NAME.toString());
        valuesMap.put("URL", Columns.URL.toString());
        valuesMap.put("ATTACHMENT_ID", Columns.ATTACHMENT_ID.toString());
        valuesMap.put("TARGET", id);


        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String QUERY = sub.replace(QUERY_TEMPLATE);

        SQLiteDatabase db = getReadableDatabase();

        ArrayList<Attachment> attachments = new ArrayList<>();

        try{
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();

            for (; !cursor.isAfterLast(); cursor.moveToNext()){
                Attachment attachment = new Attachment();

                attachment.Id = cursor.getString(cursor.getColumnIndex(Columns.ID.toString()));
                attachment.Type = cursor.getString(cursor.getColumnIndex(Columns.TYPE.toString()));
                attachment.Name = cursor.getString(cursor.getColumnIndex(Columns.NAME.toString()));
                attachment.Url = cursor.getString(cursor.getColumnIndex(Columns.URL.toString()));

                attachments.add(attachment);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return attachments;
    }
}
