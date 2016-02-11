package com.tarpost.bryanty.proj_t_post.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.object.Event;
import com.tarpost.bryanty.proj_t_post.object.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by BRYANTY on 29-Jan-16.
 */
public class DbHelper extends SQLiteOpenHelper {

    //Db version
    private static final int DATABASE_VERSION = 1;
    //Db name
    private static final String DATABASE_NAME = "tarpost_offline";
    //Table name
    private static final String TABLE_POST = "post";
    private static final String TABLE_EVENT = "event";
    //Post Table columns
    private static final String COLUMN_POSTID = "postId";
    private static final String COLUMN_CREATORID = "creatorId";
    private static final String COLUMN_CREATORNAME = "creatorName";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_UPDATEDATETIME = "updateDateTime";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ADDEDDATE = "addedDate";
    //Event Table columns
    private static final String COLUMN_EVENTID = "postId";
    private static final String COLUMN_EVENT_CREATORID = "creatorId";
    private static final String COLUMN_EVENT_TITLE = "title";
    private static final String COLUMN_EVENT_CONTENT = "content";
    private static final String COLUMN_EVENT_IMAGE = "image";
    private static final String COLUMN_EVENT_STARTDATETIME = "startDateTime";
    private static final String COLUMN_EVENT_ENDDATETIME = "endDateTime";
    private static final String COLUMN_EVENT_LATITUDE = "locationLat";
    private static final String COLUMN_EVENT_LONGITUDU = "locationLng";
    private static final String COLUMN_EVENT_CREATEDATETIME = "createDateTime";
    private static final String COLUMN_EVENT_UPDATEDATETIME = "updateDateTime";
    private static final String COLUMN_EVENT_STATUS = "status";
    private static final String COLUMN_EVENT_TYPE = "type";
    private static final String COLUMN_EVENT_ADDEDDATE = "addedDate";

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_POST = "CREATE TABLE "+TABLE_POST+" ("
                +COLUMN_POSTID+" INTEGER PRIMARY KEY, "
                +COLUMN_CREATORID+" TEXT, "
                +COLUMN_CREATORNAME+" TEXT, "
                +COLUMN_TITLE+" TEXT, "
                +COLUMN_CONTENT+" TEXT, "
                +COLUMN_UPDATEDATETIME+" TEXT, "
                +COLUMN_STATUS+" TEXT, "
                +COLUMN_TYPE+" TEXT, "
                +COLUMN_ADDEDDATE+" TEXT "
                +")";

        String CREATE_TABLE_EVENT = "CREATE TABLE "+TABLE_EVENT+" ("
                +COLUMN_EVENTID+" INTEGER PRIMARY KEY, "
                +COLUMN_EVENT_CREATORID+" TEXT, "
                +COLUMN_EVENT_TITLE+" TEXT, "
                +COLUMN_EVENT_CONTENT+" TEXT, "
//                +COLUMN_EVENT_IMAGE+" BLOB, "
                +COLUMN_EVENT_STARTDATETIME+" TEXT, "
                +COLUMN_EVENT_ENDDATETIME+" TEXT, "
                +COLUMN_EVENT_LATITUDE+" DOUBLE, "
                +COLUMN_EVENT_LONGITUDU+" DOUBLE, "
//                +COLUMN_EVENT_CREATEDATETIME+" TEXT, "
                +COLUMN_EVENT_UPDATEDATETIME+" TEXT, "
                +COLUMN_EVENT_STATUS+" TEXT, "
                +COLUMN_EVENT_TYPE+" TEXT, "
                +COLUMN_EVENT_ADDEDDATE+" TEXT "
                +")";

        db.execSQL(CREATE_TABLE_POST);
        db.execSQL(CREATE_TABLE_EVENT);
    }

    //Upgrade database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_POST);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_EVENT);
        onCreate(db);
    }

    //Insert post record
    public void addPost(Post post){

        List<Post> postList = new ArrayList<Post>();

        if(post.getType().equals("P")){
            postList=getAllPost();
        }else if(post.getType().equals("M")){
            postList=getAllMyPost();
        }else if(post.getType().equals("B")){
            postList=getAllBookmarks();
        }

        if(postList != null && postList.size() > 0){
            //Duplicate record found
            for(Post object : postList){
                if(object.getPostId().equals(post.getPostId())){
                    return;
                }
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POSTID, post.getPostId());
        values.put(COLUMN_CREATORID, post.getCreatorId());
        values.put(COLUMN_CREATORNAME, post.getCreatorName());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_CONTENT, post.getContent());
        values.put(COLUMN_UPDATEDATETIME, post.getUpdateDateTime().toString());
        values.put(COLUMN_STATUS, post.getStatus());
        values.put(COLUMN_TYPE, post.getType());
        values.put(COLUMN_ADDEDDATE, post.getAddedDate().toString());

        db.insert(TABLE_POST, null, values);
        db.close();
    }

    //Insert event record
    public void addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();

        List<Event> eventList = new ArrayList<Event>();

        if(event.getType().equals("E")){
            eventList = getAllEvent();
        }else if(event.getType().equals("J")){
            eventList = getAllEventJoin();
        }else if(event.getType().equals("C")) {
            eventList = getAllEventCreate();
        }

        if(eventList != null && eventList.size() > 0){
            //Duplicate record found
            for(Event object : eventList){
                if(object.getEventId().equals(event.getEventId())){
                    return;
                }
            }
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTID, event.getEventId());
        values.put(COLUMN_EVENT_CREATORID, event.getCreatorId());
        values.put(COLUMN_EVENT_TITLE, event.getTitle());
        values.put(COLUMN_EVENT_CONTENT, event.getContent());
        values.put(COLUMN_EVENT_STARTDATETIME, event.getStartDateTime().toString());
        values.put(COLUMN_EVENT_ENDDATETIME, event.getEndDateTime().toString());
        values.put(COLUMN_EVENT_LATITUDE, event.getLocationLat());
        values.put(COLUMN_EVENT_LONGITUDU, event.getLocationLng());
        values.put(COLUMN_EVENT_UPDATEDATETIME, event.getUpdateDateTime().toString());
        values.put(COLUMN_EVENT_STATUS, event.getStatus());
        values.put(COLUMN_EVENT_TYPE, event.getType());
        values.put(COLUMN_EVENT_ADDEDDATE, event.getAddedDate().toString());

        db.insert(TABLE_EVENT, null, values);
        db.close();
    }

    //Get all posts record
    public List<Post> getAllPost(){
        List<Post> posts = new ArrayList<Post>();
//        String query = "SELECT * FROM "+TABLE_POST;
        String query = "SELECT * FROM "+TABLE_POST+" WHERE "+COLUMN_TYPE+" == 'P' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Post post = new Post();
                post.setPostId(Integer.parseInt(cursor.getString(0)));
                post.setCreatorId(cursor.getString(1));
                post.setCreatorName(cursor.getString(2));
                post.setTitle(cursor.getString(3));
                post.setContent(cursor.getString(4));
                post.setUpdateDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(5)));
                post.setStatus(cursor.getString(6));
                post.setType(cursor.getString(7));
                post.setAddedDate(DateUtil.convertStringToDateSQLite(cursor.getString(8)));

                posts.add(post);

            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return posts;
    }

    public List<Post> getAllBookmarks(){
        List<Post> posts = new ArrayList<Post>();
        String query = "SELECT * FROM "+TABLE_POST+" WHERE "+COLUMN_TYPE+" == 'B' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Post post = new Post();
                post.setPostId(Integer.parseInt(cursor.getString(0)));
                post.setCreatorId(cursor.getString(1));
                post.setCreatorName(cursor.getString(2));
                post.setTitle(cursor.getString(3));
                post.setContent(cursor.getString(4));
                post.setUpdateDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(5)));
                post.setStatus(cursor.getString(6));
                post.setType(cursor.getString(7));
                post.setAddedDate(DateUtil.convertStringToDateSQLite(cursor.getString(8)));

                posts.add(post);

            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return posts;
    }

    //Get all posts record
    public List<Post> getAllMyPost(){
        List<Post> posts = new ArrayList<Post>();
//        String query = "SELECT * FROM "+TABLE_POST;
        String query = "SELECT * FROM "+TABLE_POST+" WHERE "+COLUMN_TYPE+" == 'M' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Post post = new Post();
                post.setPostId(Integer.parseInt(cursor.getString(0)));
                post.setCreatorId(cursor.getString(1));
                post.setCreatorName(cursor.getString(2));
                post.setTitle(cursor.getString(3));
                post.setContent(cursor.getString(4));
                post.setUpdateDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(5)));
                post.setStatus(cursor.getString(6));
                post.setType(cursor.getString(7));
                post.setAddedDate(DateUtil.convertStringToDateSQLite(cursor.getString(8)));

                posts.add(post);

            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return posts;
    }

    //Get all events record
    public List<Event> getAllEvent(){
        List<Event> events = new ArrayList<Event>();
        String query = "SELECT * FROM "+TABLE_EVENT+" WHERE "+COLUMN_EVENT_TYPE+" == 'E' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Event event = new Event();
                event.setEventId(Integer.parseInt(cursor.getString(0)));
                event.setCreatorId(cursor.getString(1));
                event.setTitle(cursor.getString(2));
                event.setContent(cursor.getString(3));
                //Convert byte[] to bitmap
                //BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).length);
//                event.setImage(cursor.getBlob(4));
                event.setStartDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(4)));
                event.setEndDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(5)));
                event.setLocationLat(cursor.getDouble(6));
                event.setLocationLng(cursor.getDouble(7));
//                event.setCreateDateTime(DateUtil.convertStringToDate(cursor.getString(9)));
                event.setUpdateDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(8)));
                event.setStatus(cursor.getString(9));
                event.setType(cursor.getString(10));
                event.setAddedDate(DateUtil.convertStringToDateSQLite(cursor.getString(11)));

                events.add(event);

            }while(cursor.moveToNext());
        }

        return events;
    }

    public List<Event> getAllEventJoin(){
        List<Event> events = new ArrayList<Event>();
        String query = "SELECT * FROM "+TABLE_EVENT+" WHERE "+COLUMN_EVENT_TYPE+" == 'J' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Event event = new Event();
                event.setEventId(Integer.parseInt(cursor.getString(0)));
                event.setCreatorId(cursor.getString(1));
                event.setTitle(cursor.getString(2));
                event.setContent(cursor.getString(3));
                event.setStartDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(4)));
                event.setEndDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(5)));
                event.setLocationLat(cursor.getDouble(6));
                event.setLocationLng(cursor.getDouble(7));
                event.setUpdateDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(8)));
                event.setStatus(cursor.getString(9));
                event.setType(cursor.getString(10));
                event.setAddedDate(DateUtil.convertStringToDateSQLite(cursor.getString(11)));

                events.add(event);

            }while(cursor.moveToNext());
        }

        return events;
    }

    public List<Event> getAllEventCreate(){
        List<Event> events = new ArrayList<Event>();
        String query = "SELECT * FROM "+TABLE_EVENT+" WHERE "+COLUMN_EVENT_TYPE+" == 'C' ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Event event = new Event();
                event.setEventId(Integer.parseInt(cursor.getString(0)));
                event.setCreatorId(cursor.getString(1));
                event.setTitle(cursor.getString(2));
                event.setContent(cursor.getString(3));
                event.setStartDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(4)));
                event.setEndDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(5)));
                event.setLocationLat(cursor.getDouble(6));
                event.setLocationLng(cursor.getDouble(7));
                event.setUpdateDateTime(DateUtil.convertStringToDateSQLite(cursor.getString(8)));
                event.setStatus(cursor.getString(9));
                event.setType(cursor.getString(10));
                event.setAddedDate(DateUtil.convertStringToDateSQLite(cursor.getString(11)));

                events.add(event);

            }while(cursor.moveToNext());
        }

        return events;
    }

    //Get total posts count
    public int getPostCount(){
        String query = "SELECT * FROM "+TABLE_POST;
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        count = cursor.getCount();

        return count;
    }

    //Get total events count
    public int getEventCount(){
        String query = "SELECT * FROM "+TABLE_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        count = cursor.getCount();

        return count;
    }

    //Update post record
    public int updatePost(Post post){
        SQLiteDatabase db = this.getWritableDatabase();
        int result =0;

        ContentValues values = new ContentValues();
        values.put(COLUMN_CREATORID, post.getCreatorId());
        values.put(COLUMN_CREATORNAME, post.getCreatorName());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_CONTENT, post.getContent());
        values.put(COLUMN_UPDATEDATETIME, post.getUpdateDateTime().toString());
        values.put(COLUMN_STATUS, post.getStatus());
        values.put(COLUMN_TYPE, post.getType());
        result = db.update(TABLE_POST, values, COLUMN_POSTID + " = ?", new String[]{String
                .valueOf(post.getPostId())});

        return result;
    }

    //Update event record
    public int updateEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        int result =0;

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_CREATORID, event.getCreatorId());
        values.put(COLUMN_EVENT_TITLE, event.getTitle());
        values.put(COLUMN_EVENT_CONTENT, event.getContent());
//        values.put(COLUMN_EVENT_IMAGE, event.getImage());
        values.put(COLUMN_EVENT_STARTDATETIME, event.getStartDateTime().toString());
        values.put(COLUMN_EVENT_ENDDATETIME, event.getEndDateTime().toString());
        values.put(COLUMN_EVENT_LATITUDE, event.getLocationLat());
        values.put(COLUMN_EVENT_LONGITUDU, event.getLocationLng());
//        values.put(COLUMN_EVENT_CREATEDATETIME, event.getCreateDateTime().toString());
        values.put(COLUMN_EVENT_UPDATEDATETIME, event.getUpdateDateTime().toString());
        values.put(COLUMN_EVENT_STATUS, event.getStatus());
        values.put(COLUMN_EVENT_TYPE, event.getType());
        values.put(COLUMN_EVENT_ADDEDDATE, event.getAddedDate().toString());

        result = db.update(TABLE_EVENT, values, COLUMN_EVENTID + " = ?", new String[]{String
                .valueOf(event.getEventId())});

        return result;
    }

    //Delete post record
    public void deletePost(Post post){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POST, COLUMN_POSTID + " = ?", new String[]{String
                .valueOf(post.getPostId())});
        db.close();
    }

    //Delete older post record
    public void deleteOldPost(){
        Log.v("SQLite Delete","Deleting old post records...");
        String query = "DELETE FROM "+TABLE_POST+" WHERE "+COLUMN_ADDEDDATE+" <=date('now', '-2 day')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    //Delete event record
    public void deleteEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, COLUMN_EVENTID + " = ?", new String[]{String
                .valueOf(event.getEventId())});
        db.close();
    }

    //Delete older post record
    public void deleteOldEvent(){
        Log.v("SQLite Delete","Deleting old event records...");
        String query = "DELETE FROM "+TABLE_EVENT+" WHERE "+COLUMN_ADDEDDATE+" <=date('now', '-2 day')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

}
