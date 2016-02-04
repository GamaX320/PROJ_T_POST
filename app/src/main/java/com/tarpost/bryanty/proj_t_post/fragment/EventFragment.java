package com.tarpost.bryanty.proj_t_post.fragment;

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
import com.tarpost.bryanty.proj_t_post.activity.AddInformationActivity;
import com.tarpost.bryanty.proj_t_post.activity.EventMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.adapter.EventAdapter;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.object.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by BRYANTY on 27-Jan-2016.
 */
public class EventFragment extends Fragment implements View.OnClickListener {

    private RecyclerView rv_event;
    private FloatingActionButton button_add;
    private ProgressBar progressBar;
    private SwipeRefreshLayout srl_refreshEvent;

    private RecyclerView.Adapter adapter;

    private RequestQueue requestQueue;

    private int requestCount = 1;

    //http://localhost/tarpost/getAllSubscribeEventWithStatusAndPaging.php
    private static final String GET_EVENT_URL = "http://projx320.webege" +
            ".com/tarpost/php/getAllSubscribeEventWithStatusAndPaging.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private List<Event> events;

    private int page=1;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_event, container, false);

        //initial recycler view information item
        rv_event= (RecyclerView)view.findViewById(R.id.recyclerViewEvent);

        //initial progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarEvent);

        //initial swipe refresh layout
        srl_refreshEvent = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshEvent);

        events= new ArrayList<>();

        setupRecyclerView(rv_event);

        //initial button add new
        button_add= (FloatingActionButton)view.findViewById(R.id.button_add);
        button_add.setOnClickListener(this);

        return view;
    }

    private void setupRecyclerView(RecyclerView rv){
        rv.setHasFixedSize(true);

        getData();
        adapter = new EventAdapter(getActivity(), events);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemDisplaying(rv_event)) {
                    progressBar.setVisibility(View.VISIBLE);
                    getData();
                }
            }
        });

        srl_refreshEvent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_refreshEvent.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                page = 1;
                events.removeAll(events);
                getData();
            }
        });

        rv.addOnItemTouchListener(new RecyclerViewClickListener(getActivity(), rv, new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Post post = posts.get(position);
//                Toast.makeText(getActivity(), post.getTitle() + " is selected!", Toast.LENGTH_SHORT)
//                        .show();
//                Intent intent = new Intent(getActivity(), PostMoreDetailsActivity.class);
//                intent.putExtra("detailsPost", post);
//                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Event event = events.get(position);

                Toast.makeText(getActivity(), position + " host is selected!", Toast
                        .LENGTH_SHORT)
                        .show();

                Intent intent = new Intent(getActivity(), EventMoreDetailsActivity.class);
                intent.putExtra("detailEvent", event);
                startActivity(intent);
            }
        }));

    }

    //get all drawer item data
    private List<Event> getData(){
        final List<Event> items= new ArrayList<>();

        Log.d("response", "page size: " + page);

        //JsonObjectRequest of volley
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                GET_EVENT_URL+"?userId="+"A000000004"+"&page="+page
//                , new Response.Listener<JSONObject>() {

        UserUtil userUtil = new UserUtil(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_EVENT_URL+"?userId="+userUtil.getUserId()+"&page="+page
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                Log.d("response", "Response: " + response.toString());

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("events");

                        Log.d("response", "JSONArray: " + jsonArray.toString());
                        Log.d("response", "JSONArray Size: " + jsonArray.length());

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

                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

                            event.setStartDateTime(DateUtil.convertStringToDate(jsonObject.getString("startDateTime")));
                            event.setEndDateTime(DateUtil.convertStringToDate(jsonObject.getString("endDateTime")));
                            event.setUpdateDateTime(DateUtil.convertStringToDate(jsonObject.getString("updateDateTime")));

                            // items.add(post);
                            events.add(event);
                        }
                        Log.d("response", "posts size: " + items.size());

                        //Notify the adapter the date has been changed
                        adapter.notifyDataSetChanged();

                        page++;
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
            case R.id.button_add:
                startActivity(new Intent(getActivity(), AddEventActivity.class));
                break;
        }
    }

    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastCompletelyVisibleItemPosition();
            if (lastItemPosition != NO_POSITION && lastItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    //Recycler view on item click listener
    public interface ItemClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private EventFragment.ItemClickListener itemClickListener;

        public RecyclerViewClickListener(Context context, final RecyclerView rv, final
        EventFragment.ItemClickListener itemClickListener){

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
