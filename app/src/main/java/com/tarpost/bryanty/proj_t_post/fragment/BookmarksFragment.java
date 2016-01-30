package com.tarpost.bryanty.proj_t_post.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
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
import com.tarpost.bryanty.proj_t_post.activity.PostMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.adapter.BookmarksAdapter;
import com.tarpost.bryanty.proj_t_post.adapter.MyPostAdapter;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.object.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {

    private RecyclerView rv_bookmarks;
    private FloatingActionButton button_add;
    private ProgressBar progressBar;
    private SwipeRefreshLayout srl_refreshBookmarks;

    private RecyclerView.Adapter adapter;

    private RequestQueue requestQueue;

    private static final String GET_BOOKMARKS_URL = "http://projx320.webege" +
            ".com/tarpost/php/getAllUserBookmarks.php";

    //http://projx320.byethost4.com/tarpost/getAllUserBookmarks.php

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private List<Post> posts;

    public BookmarksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_bookmarks, container, false);

        //initial recycler view information item
        rv_bookmarks= (RecyclerView)view.findViewById(R.id.recyclerViewBookmarks);

        //initial progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarBookmarks);

        //initial swipe refresh layout
        srl_refreshBookmarks = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshBookmarks);

        posts= new ArrayList<>();

        setupRecyclerView(rv_bookmarks);

        //initial button add new
//        button_add= (FloatingActionButton)view.findViewById(R.id.button_add);
//        button_add.setOnClickListener(this);

        return view;
    }

    private void setupRecyclerView(RecyclerView rv){
        rv.setHasFixedSize(true);

        getData();
        adapter = new BookmarksAdapter(getActivity(), posts);
        //rv.setAdapter( new PostAdapter(getActivity(), getData()));
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        srl_refreshBookmarks.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl_refreshBookmarks.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                posts.removeAll(posts);
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
                Post post = posts.get(position);
                Toast.makeText(getActivity(), post.getTitle() + " is selected!", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(getActivity(), PostMoreDetailsActivity.class);
                intent.putExtra("detailsPost", post);
                startActivity(intent);
            }
        }));

    }

    //get all drawer item data
    private List<Post> getData(){
        final List<Post> items= new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_BOOKMARKS_URL+"?userId="+"A000000004"
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                progressBar.setVisibility(View.GONE);

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("posts");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            Post post = new Post();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            post.setPostId(jsonObject.getInt("postId"));
                            post.setCreatorId(jsonObject.getString("creatorId"));
                            post.setTitle(jsonObject.getString("title"));
                            post.setContent(jsonObject.getString("content"));
                            post.setImageUrl(jsonObject.getString("image"));
                            post.setStatus(jsonObject.getString("status"));

                            posts.add(post);
                        }
                        Log.d("response", "posts size: " + items.size());

                        //Notify the adapter the date has been changed
                        adapter.notifyDataSetChanged();

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
        srl_refreshBookmarks.setRefreshing(false);

        return items;
    }

    //Recycler view on item click listener
    public interface ItemClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private BookmarksFragment.ItemClickListener itemClickListener;

        public RecyclerViewClickListener(Context context, final RecyclerView rv, final
        BookmarksFragment.ItemClickListener itemClickListener){

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
    }

}
