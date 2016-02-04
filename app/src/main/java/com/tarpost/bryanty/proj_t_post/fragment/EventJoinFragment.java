package com.tarpost.bryanty.proj_t_post.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.activity.EventMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.adapter.EventJoinAdapter;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.object.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BRYANTY on 03-Feb-16.
 */
public class EventJoinFragment extends Fragment{

    private RecyclerView rv_eventJoin;
    private SwipeRefreshLayout srl_refreshEventJoin;
    private ProgressBar progressBar;

    private RecyclerView.Adapter adapter;

    private static final String GET_EVENT_JOIN_URL = "http://projx320.webege" +
            ".com/tarpost/php/getAllUserEventJoin.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private List<Event> events;

    public EventJoinFragment() {
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
        View view= inflater.inflate(R.layout.fragment_event_join, container, false);

        //initial recycler view information item
        rv_eventJoin= (RecyclerView)view.findViewById(R.id.recyclerViewEventJoin);

        //initial swipe refresh layout
        srl_refreshEventJoin = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshEventJoin);

        //initial progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarEventJoin);

        events= new ArrayList<>();

        setupRecyclerView(rv_eventJoin);

        return view;
    }

    private void setupRecyclerView(RecyclerView rv){
        rv.setHasFixedSize(true);

        getData();
        adapter = new EventJoinAdapter(getActivity(), events);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

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

                Intent intent = new Intent(getActivity(), EventMoreDetailsActivity.class);
                intent.putExtra("detailEvent", event);
                startActivity(intent);
            }
        }));

        srl_refreshEventJoin.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_refreshEventJoin.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                events.removeAll(events);
                getData();
            }
        });

    }

    //get all drawer item data
    private void getData(){

        UserUtil userUtil = new UserUtil(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_EVENT_JOIN_URL+"?userId="+userUtil.getUserId()
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
                            event.setAvatarUrl(jsonObject.getString("avatarPic"));
                            event.setTitle(jsonObject.getString("title"));
                            event.setContent(jsonObject.getString("content"));
                            event.setImageUrl(jsonObject.getString("image"));

                            event.setLocationLat(jsonObject.getDouble("locationLat"));
                            event.setLocationLng(jsonObject.getDouble("locationLng"));

                            event.setStartDateTime(DateUtil.convertStringToDate(jsonObject.getString("startDateTime")));
                            event.setEndDateTime(DateUtil.convertStringToDate(jsonObject.getString("endDateTime")));
                            event.setUpdateDateTime(DateUtil.convertStringToDate(jsonObject.getString("updateDateTime")));

                            events.add(event);
                        }

                        Log.d("response", "Response: " + response.toString());

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

        //Stop refresh
        srl_refreshEventJoin.setRefreshing(false);

    }

    //Recycler view on item click listener
    public interface ItemClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private EventJoinFragment.ItemClickListener itemClickListener;

        public RecyclerViewClickListener(Context context, final RecyclerView rv, final
        EventJoinFragment.ItemClickListener itemClickListener){

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
