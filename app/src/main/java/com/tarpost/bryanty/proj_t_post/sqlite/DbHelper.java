package com.tarpost.bryanty.proj_t_post.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_CREATEDATETIME = "createDateTime";
    private static final String COLUMN_UPDATEDATETIME = "updateDateTime";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_TYPE = "type";
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

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_POST = "CREATE TABLE "+TABLE_POST+" ("
                +COLUMN_POSTID+" INTEGER PRIMARY KEY, "
                +COLUMN_CREATORID+" TEXT, "
                +COLUMN_TITLE+" TEXT, "
                +COLUMN_CONTENT+" TEXT, "
                +COLUMN_IMAGE+" BLOB, "
                +COLUMN_CREATEDATETIME+" TEXT, "
                +COLUMN_UPDATEDATETIME+" TEXT, "
                +COLUMN_STATUS+" TEXT, "
                +COLUMN_TYPE+" TEXT "
                +")";

        String CREATE_TABLE_EVENT = "CREATE TABLE "+TABLE_EVENT+" ("
                +COLUMN_EVENTID+" INTEGER PRIMARY KEY, "
                +COLUMN_EVENT_CREATORID+" TEXT, "
                +COLUMN_EVENT_TITLE+" TEXT, "
                +COLUMN_EVENT_CONTENT+" TEXT, "
                +COLUMN_EVENT_IMAGE+" BLOB, "
                +COLUMN_EVENT_STARTDATETIME+" TEXT, "
                +COLUMN_EVENT_ENDDATETIME+" TEXT, "
                +COLUMN_EVENT_LATITUDE+" DOUBLE, "
                +COLUMN_EVENT_LONGITUDU+" DOUBLE, "
                +COLUMN_EVENT_CREATEDATETIME+" TEXT, "
                +COLUMN_EVENT_UPDATEDATETIME+" TEXT, "
                +COLUMN_EVENT_STATUS+" TEXT, "
                +COLUMN_EVENT_TYPE+" TEXT "
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
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_POSTID, post.getPostId());
        values.put(COLUMN_CREATORID, post.getCreatorId());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_CONTENT, post.getContent());
        values.put(COLUMN_IMAGE, post.getImage());
        values.put(COLUMN_CREATEDATETIME, post.getCreateDateTime().toString());
        values.put(COLUMN_UPDATEDATETIME, post.getUpdateDateTime().toString());
        values.put(COLUMN_STATUS, post.getStatus());
        values.put(COLUMN_TYPE, post.getType());

        db.insert(TABLE_POST, null, values);
        db.close();
    }

    //Insert event record
    public void addEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENTID, event.getEventId());
        values.put(COLUMN_EVENT_CREATORID, event.getCreatorId());
        values.put(COLUMN_EVENT_TITLE, event.getTitle());
        values.put(COLUMN_EVENT_CONTENT, event.getContent());
        values.put(COLUMN_EVENT_IMAGE, event.getImage());
        values.put(COLUMN_EVENT_STARTDATETIME, event.getStartDateTime().toString());
        values.put(COLUMN_EVENT_ENDDATETIME, event.getEndDateTime().toString());
        values.put(COLUMN_EVENT_LATITUDE, event.getLocationLat());
        values.put(COLUMN_EVENT_LONGITUDU, event.getLocationLng());
        values.put(COLUMN_EVENT_CREATEDATETIME, event.getCreateDateTime().toString());
        values.put(COLUMN_EVENT_UPDATEDATETIME, event.getUpdateDateTime().toString());
        values.put(COLUMN_EVENT_STATUS, event.getStatus());
        values.put(COLUMN_EVENT_TYPE, event.getType());

        db.insert(TABLE_EVENT, null, values);
        db.close();
    }

    //Get all posts record
    public List<Post> getAllPost(){
        List<Post> posts = new ArrayList<Post>();
        String query = "SELECT * FROM "+TABLE_POST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Post post = new Post();
                post.setPostId(Integer.parseInt(cursor.getString(0)));
                post.setCreatorId(cursor.getString(1));
                post.setTitle(cursor.getString(2));
                post.setContent(cursor.getString(3));
                //Convert byte[] to bitmap
                //BitmapFactory.decodeByteArray(cursor.getBlob(4), 0, cursor.getBlob(4).length);
                post.setImage(cursor.getBlob(4));
                post.setCreateDateTime(DateUtil.convertStringToDate(cursor.getString(5)));
                post.setUpdateDateTime(DateUtil.convertStringToDate(cursor.getString(6)));
                post.setStatus(cursor.getString(7));
                post.setType(cursor.getString(8));

                posts.add(post);

            }while(cursor.moveToNext());
        }

        return posts;
    }

    //Get all events record
    public List<Event> getAllEvent(){
        List<Event> events = new ArrayList<Event>();
        String query = "SELECT * FROM "+TABLE_EVENT;
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
                event.setImage(cursor.getBlob(4));
                event.setStartDateTime(DateUtil.convertStringToDate(cursor.getString(5)));
                event.setEndDateTime(DateUtil.convertStringToDate(cursor.getString(6)));
                event.setLocationLat(cursor.getDouble(7));
                event.setLocationLng(cursor.getDouble(8));
                event.setCreateDateTime(DateUtil.convertStringToDate(cursor.getString(9)));
                event.setUpdateDateTime(DateUtil.convertStringToDate(cursor.getString(10)));
                event.setStatus(cursor.getString(11));
                event.setType(cursor.getString(12));

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
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_CONTENT, post.getContent());
        values.put(COLUMN_IMAGE, post.getImage());
        values.put(COLUMN_CREATEDATETIME, post.getCreateDateTime().toString());
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
        values.put(COLUMN_EVENT_IMAGE, event.getImage());
        values.put(COLUMN_EVENT_STARTDATETIME, event.getStartDateTime().toString());
        values.put(COLUMN_EVENT_ENDDATETIME, event.getEndDateTime().toString());
        values.put(COLUMN_EVENT_LATITUDE, event.getLocationLat());
        values.put(COLUMN_EVENT_LONGITUDU, event.getLocationLng());
        values.put(COLUMN_EVENT_CREATEDATETIME, event.getCreateDateTime().toString());
        values.put(COLUMN_EVENT_UPDATEDATETIME, event.getUpdateDateTime().toString());
        values.put(COLUMN_EVENT_STATUS, event.getStatus());
        values.put(COLUMN_EVENT_TYPE, event.getType());
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

    //Delete event record
    public void deleteEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENT, COLUMN_EVENTID + " = ?", new String[]{String
                .valueOf(event.getEventId())});
        db.close();
    }

}
