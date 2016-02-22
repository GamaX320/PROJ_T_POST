package com.tarpost.bryanty.proj_t_post.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.activity.AddEventActivity;
import com.tarpost.bryanty.proj_t_post.activity.EventMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.adapter.MyEventAdapter;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.object.Event;
import com.tarpost.bryanty.proj_t_post.sqlite.DbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by BRYANTY on 12-Feb-16.
 */
public class MyEventsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rv_myevent;
    private FloatingActionButton button_add;
    private ProgressBar progressBar;
    private SwipeRefreshLayout srl_refreshEvent;

    private RecyclerView.Adapter adapter;

    private RequestQueue requestQueue;

    private static final String GET_MYEVENT_URL = "http://projx320.webege" +
            ".com/tarpost/php/getAllUserEvent.php";

    //http://projx320.byethost4.com/tarpost/getAllUserPost.php

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private List<Event> events;

    private  DbHelper dbHelper;

    private Activity myActivity;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_my_events, container, false);

        //initial recycler view information item
        rv_myevent= (RecyclerView)view.findViewById(R.id.recyclerViewMyEvent);

        //initial progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarMyEvent);

        //initial swipe refresh layout
        srl_refreshEvent = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshMyEvent);

        events= new ArrayList<>();

        setupRecyclerView(rv_myevent);

        //initial button add new
        button_add= (FloatingActionButton)view.findViewById(R.id.button_add_event);
        button_add.setOnClickListener(this);

        dbHelper = new DbHelper(getActivity());

        myActivity = getActivity();

        return view;
    }

    private void setupRecyclerView(RecyclerView rv){
        rv.setHasFixedSize(true);

        //Check whether online data or offline data
        if(UserUtil.checkInternetConnection(getActivity().getApplicationContext())){
            getData();
        }else{
            Log.d("Offline Read", "Retrieve...");
            DbHelper dbHelper = new DbHelper(getActivity());
            events = dbHelper.getAllEventCreate();

            if(events != null && events.size() > 0){
                for(Event object : events){
                    Log.d("Offline Data","Id: "+object.getEventId()
                            +" Title: "+object.getTitle()
                            +" Content: "+object.getContent()
                            +" Type: "+object.getType()
                            +" Added: "+ object.getAddedDate());
                }

                Toast.makeText(getActivity(), getResources().getString(R.string.text_message_fetch_offline_data), Toast
                        .LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), getResources().getString(R.string.text_message_no_items_available), Toast
                        .LENGTH_LONG).show();
            }

        }


        adapter = new MyEventAdapter(getActivity(), events);
        //rv.setAdapter( new PostAdapter(getActivity(), getData()));
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (isLastItemDisplaying(rv_mypost)) {
//                    progressBar.setVisibility(View.VISIBLE);
//                    getData();
//                }
            }
        });

        srl_refreshEvent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_refreshEvent.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                events.removeAll(events);
                getData();
            }
        });

        rv.addOnItemTouchListener(new RecyclerViewClickListener(getActivity(), rv, new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                Event event = events.get(position);
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                intent.putExtra("mode", "MODIFY");
                intent.putExtra("detailsEvent", event);
                startActivity(intent);
            }
        }));

    }

    //get all drawer item data
    private List<Event> getData(){
        final List<Event> items= new ArrayList<>();

        UserUtil userUtil = new UserUtil(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_MYEVENT_URL+"?userId="+userUtil.getUserId()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);

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
                            event.setAvatarUrl(jsonObject.getString("creatorAvatarPic"));
                            event.setTitle(jsonObject.getString("title"));
                            event.setContent(jsonObject.getString("content"));
                            event.setImageUrl(jsonObject.getString("image"));

                            if(jsonObject.getString("locationLat") != null &&
                                    jsonObject.getString("locationLat") != "" &&
                                    jsonObject.getString("locationLat") != "null"){
                                event.setLocationLat(jsonObject.getDouble("locationLat"));
                            }

                            if(jsonObject.getString("locationLng") != null &&
                                    jsonObject.getString("locationLng") != "" &&
                                    jsonObject.getString("locationLng") != "null"){
                                event.setLocationLng(jsonObject.getDouble("locationLng"));
                            }

                            //TODO: If dont have internet access might occur fc
                            if(event.getLocationLat() != null && event.getLocationLng() != null){

                                Geocoder geocoder = new Geocoder(myActivity, Locale.getDefault());

//                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                try {
                                    List<Address> addresses = geocoder.getFromLocation(event.getLocationLat(),
                                            event.getLocationLng(),1);

                                    if(addresses.size() > 0){
                                        Log.v("location","location result > "+addresses.get(0).getLocality());
                                        Log.v("location","location result > "+addresses.get(0)
                                                .getAddressLine(0));

                                        event.setLocation(addresses.get(0).getLocality());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            event.setStartDateTime(DateUtil.convertStringToDate(jsonObject.getString("startDateTime")));
                            event.setStartDateTimeStr(DateUtil.convertDateToString(event
                                    .getStartDateTime()));
                            event.setEndDateTime(DateUtil.convertStringToDate(jsonObject.getString("endDateTime")));
                            event.setEndDateTimeStr(DateUtil.convertDateToString(event
                                    .getEndDateTime()));
                            event.setUpdateDateTime(DateUtil.convertStringToDate(jsonObject.getString("updateDateTime")));

                            event.setType("C");
                            event.setAddedDate(new Date());

                            //Offline data
                            Log.d("Offline Insert", "SQLITE Inserting...");
//                            DbHelper dbHelper = new DbHelper(getActivity());
                            dbHelper.addEvent(event);

                            events.add(event);
                        }
                        Log.d("response", "events size: " + items.size());

                        //Notify the adapter the date has been changed
                        adapter.notifyDataSetChanged();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Exception>>>>>>>>>> "+e, Toast.LENGTH_LONG)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getResources().getString(R.string.text_message_no_items_available), Toast
                        .LENGTH_LONG).show();
                Log.d("response", "Error Response: " + error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

        Log.d("response", "return items size: " + items.size());

        //Stop refresh
        srl_refreshEvent.setRefreshing(false);

        return items;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_add_event:
                Intent intent = new Intent(getActivity(), AddEventActivity.class);
                intent.putExtra("mode", "NEW");
                startActivity(intent);
                break;
        }
    }

    //Recycler view on item click listener
    public interface ItemClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private MyEventsFragment.ItemClickListener itemClickListener;

        public RecyclerViewClickListener(Context context, final RecyclerView rv, final
        MyEventsFragment.ItemClickListener itemClickListener){

            this.itemClickListener = itemClickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
//                    View child = rv.findChildViewUnder(e.getX(), e.getY());
//                   // itemClickListener.onLongClick(child, rv.getChildPosition(child));
//                    Log.d("response", "Item position: " + rv.getChildPosition(child));
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if(child != null && itemClickListener != null){
                        itemClickListener.onLongClick(child, rv.getChildPosition(child));
                    }
                    Log.d("response", "Item position: " + rv.getChildPosition(child));
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View view = rv.findChildViewUnder(e.getX(), e.getY());

            if(view != null && itemClickListener != null && gestureDetector.onTouchEvent(e)){
                itemClickListener.onClick(view, rv.getChildPosition(view));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}
