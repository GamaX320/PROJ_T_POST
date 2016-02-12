package com.tarpost.bryanty.proj_t_post;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.tarpost.bryanty.proj_t_post.activity.LoginActivity;
import com.tarpost.bryanty.proj_t_post.activity.SettingsActivity;
import com.tarpost.bryanty.proj_t_post.activity.SettingsMainActivity;
import com.tarpost.bryanty.proj_t_post.activity.UserProfileActivity;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.fragment.BookmarksFragment;
import com.tarpost.bryanty.proj_t_post.fragment.EventFragment;
import com.tarpost.bryanty.proj_t_post.fragment.EventJoinFragment;
import com.tarpost.bryanty.proj_t_post.fragment.HomeFragment;
import com.tarpost.bryanty.proj_t_post.fragment.MyEventsFragment;
import com.tarpost.bryanty.proj_t_post.fragment.MyPostsFragment;
import com.tarpost.bryanty.proj_t_post.fragment.PostsFragment;
import com.tarpost.bryanty.proj_t_post.fragment.SubscriptionFragment;
import com.tarpost.bryanty.proj_t_post.notification.NotificationReceiver;
import com.tarpost.bryanty.proj_t_post.notification.NotificationService;
import com.tarpost.bryanty.proj_t_post.object.Event;
import com.tarpost.bryanty.proj_t_post.slide.Intro;
import com.tarpost.bryanty.proj_t_post.sqlite.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.*;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar toolbar;
    private String userId, userName, userEmail, userAvatar, userCover;

    private MaterialSearchView searchView;

    private List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check user is first time or not
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("userLogin", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("firstTime", false)){
            Intent intent = new Intent(this, Intro.class);
            startActivity(intent);
        }

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_indicator);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initial drawer layout
        mDrawerLayout= (DrawerLayout)findViewById(R.id.drawer_layout);

        //initial navigation drawer layout
        mNavigationView= (NavigationView)findViewById(R.id.nav_view);
        if(mNavigationView!= null){
            //setup navigation drawer content
            setupNavigationDrawer(mNavigationView);
        }

        //Launch the first item in navigation drawer
        mNavigationView.getMenu().getItem(0).setChecked(true);
        getContent(R.id.navigation_item_testpost);

        searchView = (MaterialSearchView)findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent intent = new Intent(getApplication(), SearchResultActivity.class);
                intent.putExtra("searchQuery", query);
                startActivity(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

       setupUser();

        UserUtil.setupTheme(this, toolbar);

        //delete old offline data
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.deleteOldPost();
        dbHelper.deleteOldEvent();

        //setup notification
        if(UserUtil.checkNotificationOnOff(this)){
            Log.v("NotificationOnOff","Receive notification On");
            events = new ArrayList<Event>();
            setupNotification();
        }else{
            Log.v("NotificationOnOff","Receive notification Off");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        switch(id){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
          /*  case R.id.action_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            case R.id.action_collapsing:
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                return true;*/

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    //setup navigation drawer content
    private void setupNavigationDrawer(NavigationView nv){
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                getContent(menuItem.getItemId());
                return true;
            }
        });

    }

    //get selected content
    private void getContent(int position){
        Fragment fragment= null;
        FragmentManager fm= this.getFragmentManager();

        Log.v("position", "selected > " + position);
        switch (position){
           /* case R.id.navigation_item_home:
                fragment= new HomeFragment();
                break;*/
            case R.id.navigation_item_post:
                fragment= new MyPostsFragment();
                break;
            case R.id.navigation_item_event:
                fragment= new EventFragment();
                break;
            case R.id.navigation_item_bookmark:
                fragment= new BookmarksFragment();
                break;
            case R.id.navigation_item_event_my:
                fragment= new MyEventsFragment();
                break;
            case R.id.navigation_item_event_join:
                fragment= new EventJoinFragment();
                break;
            case R.id.navigation_item_subscription:
                fragment= new SubscriptionFragment();
                break;
            case R.id.navigation_item_testpost:
                fragment= new PostsFragment();
                break;
            case R.id.navigation_item_settings:
                Intent intent = new Intent(MainActivity.this, SettingsMainActivity.class);
                startActivity(intent);
               return;
               // break;
            case R.id.navigation_item_logout:

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string
                                .text_dialog_confirm_title))
                        .setMessage(getResources().getString(R.string
                                .text_dialog_confirm_content))
                        .setPositiveButton(R.string.text_dialog_confirm_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                UserUtil.userLogout(getApplication().getApplicationContext());
                            }
                        })
                        .setNegativeButton(R.string.text_dialog_confirm_no, null).show();
                return;
                //break;
            case R.id.navigation_item_feedback:
                Intent feedback = new Intent(Intent.ACTION_SEND);
                feedback.setType("text/email");
                feedback.putExtra(Intent.EXTRA_EMAIL, new String[] { "proj.x320@gmail.com" });
                feedback.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                startActivity(Intent.createChooser(feedback, "Send Feedback"));
                return;
        }

        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void setupUser(){
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences("userLogin", Context.MODE_PRIVATE);

        if(sharedPreferences.contains("login")){
            if(sharedPreferences.getBoolean("login", false)){

                userId = sharedPreferences.getString("userId",null);
                userEmail = sharedPreferences.getString("userEmail",null);
                userName = sharedPreferences.getString("userName",null);
                userAvatar = sharedPreferences.getString("userAvatar",null);
                userCover = sharedPreferences.getString("userCover",null);

                //mNavigationView
                //Find header - required on API 23 and above
                View header = mNavigationView.getHeaderView(0);

                TextView headerUserName = (TextView)header.findViewById(R.id.header_userName);
                TextView headerEmail = (TextView)header.findViewById(R.id.header_userEmail);
                final CircleImageView headerAvatar = (CircleImageView)header.findViewById(R.id
                        .header_userAvatar);
                final NetworkImageView headerTempAvatar = (NetworkImageView)header.findViewById(R.id
                        .header_tempAvatar);
                final NetworkImageView headerTempCover = (NetworkImageView)header.findViewById(R.id
                        .header_tempCover);
                final LinearLayout headerLinearLayout = (LinearLayout) header.findViewById(R.id
                        .header_linerLayout);

//                headerUserName.setText(userId);
                headerUserName.setText(userName);
                headerEmail.setText(userEmail);

                //Set avatar and cover picture
                ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
                headerTempAvatar.setImageUrl(userAvatar, imageLoader);
                headerTempCover.setImageUrl(userCover,imageLoader);

                //avatar image loader listener
                imageLoader.get(userAvatar, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            headerAvatar.setImageBitmap(response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                //header avatar profile onClick listener
                headerAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    }
                });

                //cover image loader listener
                imageLoader.get(userCover, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
                                    response.getBitmap());
                            headerLinearLayout.setBackgroundDrawable(bitmapDrawable);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

            }else{
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }else{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    public void setupNotification(){
//        List<Event> events = new ArrayList<Event>();
        Log.d("Offline Read", "Retrieve...");
        DbHelper dbHelper = new DbHelper(this);
//        events = dbHelper.getAllEventJoin();

        //Check whether online data or offline data
        if(UserUtil.checkInternetConnection(this.getApplicationContext())){
            getAllEventsJoin();

            if( events == null || events.size() == 0){
                events = dbHelper.getAllEventJoin();
            }

        }else {
            events = dbHelper.getAllEventJoin();
        }

        if(events != null && events.size() > 0){

            int i = 0;
            for(Event event : events){
                Log.v("Notification", "Adding notification...");
                Intent intent= new Intent(MainActivity.this, NotificationReceiver.class);
                intent.putExtra("joinedEvent", event);

                Calendar eventDateTime =  Calendar.getInstance();
                eventDateTime.setTimeInMillis(System.currentTimeMillis());
                eventDateTime.set((event.getStartDateTime().getYear() + 1900),
                        event.getStartDateTime().getMonth(),
                        event.getStartDateTime().getDate(),
                        event.getStartDateTime().getHours(),
                        event.getStartDateTime().getMinutes(),
                        event.getStartDateTime().getSeconds());

                Log.v("Notification", "Adding notification on date: "
                        + (event.getStartDateTime().getYear()+1900) + " "
                        + event.getStartDateTime().getMonth() + " "
                        + event.getStartDateTime().getDate() + " "
                        + event.getStartDateTime().getHours() + " "
                        + event.getStartDateTime().getMinutes() + " "
                        + event.getStartDateTime().getSeconds());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, i,
                        intent,0);
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, eventDateTime.getTimeInMillis(), pendingIntent);

//                if(i>0){
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTimeInMillis(System.currentTimeMillis());
//                    calendar.set(2016, 1, 11, 16, 48, 0);
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                }

                i++;
            }

        }

    }

    private void getAllEventsJoin(){
        final UserUtil userUtil = new UserUtil(this.getApplicationContext());
        final String GET_EVENT_JOIN_URL = "http://projx320.webege" +
                ".com/tarpost/php/getAllUserEventJoin.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_EVENT_JOIN_URL+"?userId="+userUtil.getUserId()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("events");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            Event event = new Event();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            event.setEventId(jsonObject.getInt("eventId"));
                            event.setCreatorId(jsonObject.getString("creatorId"));
                            event.setCreatorName(jsonObject.getString("creatorName"));
                            event.setAvatarUrl(jsonObject.getString("avatarPic"));
                            event.setTitle(jsonObject.getString("title"));
                            event.setContent(jsonObject.getString("content"));
                            event.setImageUrl(jsonObject.getString("image"));

                            event.setLocationLat(jsonObject.getDouble("locationLat"));
                            event.setLocationLng(jsonObject.getDouble("locationLng"));

                            event.setStartDateTime(DateUtil.convertStringToDate(jsonObject.getString("startDateTime")));
                            event.setStartDateTimeStr(DateUtil.convertDateToString(event
                                    .getStartDateTime()));
                            event.setEndDateTime(DateUtil.convertStringToDate(jsonObject.getString("endDateTime")));
                            event.setEndDateTimeStr(DateUtil.convertDateToString(event
                                    .getEndDateTime()));
                            event.setUpdateDateTime(DateUtil.convertStringToDate(jsonObject.getString("updateDateTime")));

                            event.setType("J");
                            event.setAddedDate(new Date());

                            events.add(event);
                        }
                        Log.d("response", "Response: " + response.toString());

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Exception>>>>>>>>>> "+e, Toast.LENGTH_LONG)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.text_message_no_items_available), Toast
                        .LENGTH_LONG).show();
                Log.d("response", "Error Response: " + error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);
    }

}
