package com.tarpost.bryanty.proj_t_post.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private SwipeRefreshLayout srl_refreshPost;

    private Adapter adapter;

    private RequestQueue requestQueue;

    private int requestCount = 1;

    //http://localhost/tarpost/addInformation.php
    private static final String GET_POST_URL = "http://projx320.webege.com/tarpost/php/getAllSubscribePost.php?userId=";
    private static final String GET_POST_URL2 = "http://projx320.webege" +
            ".com/tarpost/php/getAllSubscribePostWithStatusAndPaging.php";

    //http://projx320.byethost4.com/tarpost/addInformation.php

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private List<Post> posts;

    private int page=1;

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

        //initial swipe refresh layout
        srl_refreshPost = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshPost);

        posts= new ArrayList<>();

        setupRecyclerView(rv_post);

        //initial button add new
        button_add= (FloatingActionButton)view.findViewById(R.id.button_add);
        button_add.setOnClickListener(this);

        return view;
    }

    private void setupRecyclerView(RecyclerView rv){
        rv.setHasFixedSize(true);

//        adapter = new PostAdapter(getActivity(), getData());
        getData();
        adapter = new PostAdapter(getActivity(), posts);
        //rv.setAdapter( new PostAdapter(getActivity(), getData()));
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        rv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemDisplaying(rv_post)) {
                    progressBar.setVisibility(View.VISIBLE);
                    getData();
                }
            }
        });

        srl_refreshPost.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_refreshPost.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                page= 1;
               posts.removeAll(posts);
                getData();
            }
        });

    }

    //get all drawer item data
    private List<Post> getData(){
        final List<Post> items= new ArrayList<>();

        Log.d("response", "page size: " + page);

        //JsonObjectRequest of volley
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
//                "http://projx320.webege.com/tarpost/php/getAllSubscribePostTest.php?userId=A000000004"
//                , new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_POST_URL2+"?userId="+"A000000004"+"&page="+page
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);
                Log.d("response", "Response: " + response.toString());

                try{
                    int success = response.getInt("success");

                    if(success > 0){

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
                            post.setImageUrl(jsonObject.getString("image"));

                            Log.d("response", "JSONArray postId: " + jsonObject.getInt("postId"));
                            Log.d("response", "JSONArray creatorId: " + jsonObject.getString("creatorId"));
                            Log.d("response", "JSONArray title: " + jsonObject.getString("title"));
                            Log.d("response", "JSONArray content: " + jsonObject.getString("content"));
                            Log.d("response", "JSONArray content: " + jsonObject.getString("image"));

                            // items.add(post);
                            posts.add(post);
                        }
                        Log.d("response", "posts size: " + items.size());

//                    rv_post.setAdapter( new PostAdapter(getActivity(), items));
//                    rv_post.setLayoutManager(new LinearLayoutManager(getActivity()));

                        //Notify the adapter the date has been changed
                        adapter.notifyDataSetChanged();

                        page++;
                    }

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

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

        Log.d("response", "return items size: " + items.size());

        //Stop refresh
        srl_refreshPost.setRefreshing(false);

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
            int lastItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastCompletelyVisibleItemPosition();
            if (lastItemPosition != NO_POSITION && lastItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

}
