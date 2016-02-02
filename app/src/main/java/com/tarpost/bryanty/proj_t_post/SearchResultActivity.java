package com.tarpost.bryanty.proj_t_post;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tarpost.bryanty.proj_t_post.activity.EventMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.activity.PostMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.activity.UserProfileActivity;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.object.Event;
import com.tarpost.bryanty.proj_t_post.object.Post;
import com.tarpost.bryanty.proj_t_post.object.SearchObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResultActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private ListView lv;
    private List<SearchObject> results;
    private SearchResultAdapter adapter;
    private SearchObject searchObject;

    private static final String GET_SEARCH_RESULT = "http://projx320.webege" +
            ".com/tarpost/php/searchUserPostEvent.php";

    private static final String GET_POST = "http://projx320.webege" +
            ".com/tarpost/php/getPost.php";

    private static final String GET_EVENT = "http://projx320.webege" +
            ".com/tarpost/php/getEvent.php";

    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //initial toolbar
        toolbar= (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle= getIntent().getExtras();
        searchQuery= bundle.getString("searchQuery");

        lv = (ListView)findViewById(R.id.lvSearchResult);

        results = new ArrayList<>();

        setupListView(lv);
    }

    private void setupListView(ListView lv){
        getData();
        adapter = new SearchResultAdapter(this, results);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchObject result = (SearchObject) parent.getItemAtPosition(position);
                Toast.makeText(SearchResultActivity.this, "selected> " + result.getId() + " " +
                        "name" + result.getName() + " type" + result.getResultType(), Toast
                        .LENGTH_LONG).show();

                if (result.getResultType().equals("user")) {
                    Intent intent = new Intent(SearchResultActivity.this, UserProfileActivity
                            .class);
                    intent.putExtra("userId", result.getId());
                    startActivity(intent);

                } else if (result.getResultType().equals("post")) {
                    getPost(result.getId());

                } else if (result.getResultType().equals("event")) {
                    getEvent(result.getId());
//                    Intent intent = new Intent(SearchResultActivity.this, EventMoreDetailsActivity
//                            .class);
//                    intent.putExtra("detailEvent", getEvent(result.getId()));
//                    startActivity(intent);

                }
            }
        });
    }

    private void getData(){

        UserUtil userUtil = new UserUtil(this.getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_SEARCH_RESULT+"?keyword="+searchQuery+"&userId="+userUtil.getUserId()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("result");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            SearchObject result = new SearchObject();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            result.setId(jsonObject.getString("id"));
                            result.setName(jsonObject.getString("name"));
                            result.setResultType(jsonObject.getString("resultType"));

                            results.add(result);
                        }

                        //Notify the adapter the date has been changed
                        adapter.notifyDataSetChanged();

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(SearchResultActivity.this
                            , "Exception>>>>>>>>>> " + e, Toast
                            .LENGTH_SHORT)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchResultActivity.this, "No More Items Available", Toast
                        .LENGTH_SHORT).show();
                Log.d("response", "Error Response: " + error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

    }

    public void getPost(String postId){;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_POST+"?postId="+postId
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){
                        Post post = new Post();
                        JSONArray jsonArray = response.getJSONArray("posts");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            post.setPostId(jsonObject.getInt("postId"));
                            post.setCreatorId(jsonObject.getString("creatorId"));
                            post.setCreatorName(jsonObject.getString("creatorName"));
                            post.setCreatorAvatarUrl(jsonObject.getString("creatorAvatarPic"));
                            post.setTitle(jsonObject.getString("title"));
                            post.setContent(jsonObject.getString("content"));
                            post.setImageUrl(jsonObject.getString("image"));
                            post.setStatus(jsonObject.getString("status"));
                            post.setUpdateDateTime(DateUtil.convertStringToDate(jsonObject.getString("updateDateTime")));

                        }

                        if(post != null){
                            Intent intent = new Intent(SearchResultActivity.this, PostMoreDetailsActivity
                                    .class);
                            intent.putExtra("detailsPost", post);
                            startActivity(intent);
                        }

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchResultActivity.this, "No More Items Available", Toast
                        .LENGTH_SHORT).show();
                Log.d("response", "Error Response: " + error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

    }

    private void getEvent(String eventId){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_EVENT+"?eventId="+eventId
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){
                        Event event = new Event();
                        JSONArray jsonArray = response.getJSONArray("events");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
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

                            Geocoder geocoder = new Geocoder(SearchResultActivity.this, Locale
                                    .getDefault());
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

                        }

                        Intent intent = new Intent(SearchResultActivity.this, EventMoreDetailsActivity
                                .class);
                        intent.putExtra("detailEvent", event);
                        startActivity(intent);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(SearchResultActivity.this, "Exception>>>>>>>>>> "+e, Toast
                            .LENGTH_SHORT)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchResultActivity.this, "No More Items Available", Toast.LENGTH_SHORT).show();
                Log.d("response", "Error Response: " + error.toString());
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyApplication.getInstance().addToReqQueue(jsonObjectRequest);

    }

    class SearchResultAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private List<SearchObject> results;

        public SearchResultAdapter(Context context, List<SearchObject> results) {
            this.context = context;
            this.results = results;
            inflater= LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public Object getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView= inflater.inflate(R.layout.row_item_search_result, null);

            TextView resultName = (TextView)convertView.findViewById(R.id.searchResult_name);
            ImageView resultType = (ImageView)convertView.findViewById(R.id.searchResult_type);

            SearchObject result = results.get(position);

            resultName.setText(result.getName());

            if(result.getResultType().equals("user")){
                resultType.setImageResource(R.drawable.ic_admin);
            }else if(result.getResultType().equals("post")){
                resultType.setImageResource(R.drawable.ic_post);
            }else if(result.getResultType().equals("event")){
                resultType.setImageResource(R.drawable.ic_event);
            }

            return convertView;
        }
    }

}
