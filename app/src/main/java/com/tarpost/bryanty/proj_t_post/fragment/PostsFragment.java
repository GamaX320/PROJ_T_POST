package com.tarpost.bryanty.proj_t_post.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.activity.AddInformationActivity;
import com.tarpost.bryanty.proj_t_post.adapter.InformationAdapter;
import com.tarpost.bryanty.proj_t_post.adapter.PostAdapter;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.object.Information;
import com.tarpost.bryanty.proj_t_post.object.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by BRYANTY on 10-Jan-2016.
 */
public class PostsFragment extends Fragment implements View.OnClickListener{

    private RecyclerView rv_post;
    private FloatingActionButton button_add;
    private ProgressBar progressBar;

    private Adapter adapter;

    private RequestQueue requestQueue;

    private int requestCount = 1;

    //http://localhost/tarpost/addInformation.php
    private static final String GET_POST_URL = "http://projx320.webege" +
            ".com/tarpost/php/getAllSubscribePost.php?userId=";

    //http://projx320.byethost4.com/tarpost/addInformation.php

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private List<Post> posts;


    public PostsFragment() {
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
        View view= inflater.inflate(R.layout.fragment_posts, container, false);

        //initial recycler view information item
        rv_post= (RecyclerView)view.findViewById(R.id.recyclerViewPost);

        //initial progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarPost);

        posts= new ArrayList<>();

//        requestQueue = Volley.newRequestQueue(getActivity());

        setupRecyclerView(rv_post);

        //initial button add new
        button_add= (FloatingActionButton)view.findViewById(R.id.button_add);
        button_add.setOnClickListener(this);

        return view;
    }

    private void setupRecyclerView(RecyclerView rv){
        rv.setHasFixedSize(true);
//        getData();
        rv.setAdapter( new PostAdapter(getActivity(), getData()));
//        rv.setAdapter(new PostAdapter(getActivity(), posts));
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    //get all drawer item data
    private List<Post> getData(){
        final List<Post> items= new ArrayList<>();

        //display progress bar
//        progressBar.setVisibility(View.VISIBLE);
        //setProgressBarIndeterminateVisibility(true);

//        String GET_POST_URL = "http://projx320.webege.com/tarpost/php/getAllSubscribePostTest.php?userId=";
//
//        //JsonArrayRequest from server
//        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest("http://projx320.webege.com/tarpost/php/getAllSubscribePostTest.php?userId=A000000004",
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//
////                        progressBar.setVisibility(View.GONE);
//
//                        Toast.makeText(getActivity(), "Get Data > "+ response, Toast.LENGTH_LONG)
//                                .show();
//
//                        if(response != null){
//                            //load data from json array
//                            for(int i=0; i < response.length(); i++){
//                                Post post = new Post();
//                                JSONObject jsonObject = null;
//
//                                try{
//                                    jsonObject = response.getJSONObject(i);
//
//                                    post.setPostId(jsonObject.getInt("postId"));
//                                    post.setCreatorId(jsonObject.getString("creatorId"));
//                                    post.setTitle(jsonObject.getString("title"));
//                                    post.setContent(jsonObject.getString("content"));
//                                    //set create date and update date
//
//                                }catch(JSONException e){
//                                    e.printStackTrace();
//                                }
//
//                                posts.add(post);
//                            }
//                        }
//                    }
//
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "No More Items Available", Toast.LENGTH_SHORT).show();
//            }
//        });

        // Adding request to request queue
//        MyApplication.getInstance().addToReqQueue(jsonArrayRequest);


        //JsonObjectRequest of volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "http://projx320.webege.com/tarpost/php/getAllSubscribePostTest" +
                        ".php?userId=A000000004"
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Response Enter "+response, Toast.LENGTH_SHORT)
                        .show();
                Log.d("response", "Response: " + response.toString());

                try{
                    JSONArray jsonArray = response.getJSONArray("posts");

                    Log.d("response", "JSONArray: " + jsonArray.toString());
                    Log.d("response", "JSONArray Size: " + jsonArray.length());

                    for(int i = 0 ; i < jsonArray.length() ; i++){
                        Post post = new Post();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        post.setPostId(jsonObject.getInt("postId"));
                        post.setCreatorId(jsonObject.getString("creatorId"));
                        post.setTitle(jsonObject.getString("title"));
                        post.setContent(jsonObject.getString("content"));

                        Log.d("response", "JSONArray postId: " + jsonObject.getInt("postId"));
                        Log.d("response", "JSONArray creatorId: " + jsonObject.getString("creatorId"));
                        Log.d("response", "JSONArray title: " + jsonObject.getString("title"));
                        Log.d("response", "JSONArray content: " + jsonObject.getString("content"));

                        items.add(post);
                    }
                    Log.d("response", "posts size: " + items.size());

                    rv_post.setAdapter( new PostAdapter(getActivity(), items));
                    rv_post.setLayoutManager(new LinearLayoutManager(getActivity()));

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Exception>>>>>>>>>> "+e, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "No More Items Available", Toast.LENGTH_SHORT).show();
                Log.d("response", "Error Response: " + error.toString());
            }
        });

//        adapter.notifyDataSetChanged();

//        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
//                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

        Log.d("response", "return items size: " + items.size());
        return items;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.button_add:
                startActivity(new Intent(getActivity(), AddInformationActivity.class));
                break;
        }
    }

    //This method would check that the recyclerview scroll has reached the bottom or not
    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

//    //Overriden method to detect scrolling
//    @Override
//    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//        //Ifscrolled at last then
//        if (isLastItemDisplaying(rv_post)) {
//            //Calling the method getdata again
//            getData();
//        }
//    }
}
