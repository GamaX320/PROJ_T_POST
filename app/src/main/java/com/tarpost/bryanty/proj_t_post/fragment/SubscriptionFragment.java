package com.tarpost.bryanty.proj_t_post.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.activity.UserProfileActivity;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.UserUtil;
import com.tarpost.bryanty.proj_t_post.object.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {

    private GridView gv;
    private List<User> users;
    private GridViewAdapter adapter;

    private static final String GET_SUBSCRIPTION_USER_URL = "http://projx320.webege" +
            ".com/tarpost/php/getAllSubscribeUserWithStatus.php";

    public SubscriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);

        gv= (GridView)view.findViewById(R.id.gvSubscription);

        users = new ArrayList<>();

        setupGridView(gv);

        return view;
    }

    private void setupGridView(GridView gv){
        getData();
        adapter = new GridViewAdapter(getActivity(), users);
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra("userId", user.getUserId());
                startActivity(intent);
            }
        });
    }

    private void getData(){

        UserUtil userUtil = new UserUtil(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_SUBSCRIPTION_USER_URL+"?userId="+userUtil.getUserId()
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    int success = response.getInt("success");

                    if(success > 0){

                        JSONArray jsonArray = response.getJSONArray("users");

                        for(int i = 0 ; i < jsonArray.length() ; i++){
                            User user = new User();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            user.setUserId(jsonObject.getString("userId"));
                            user.setName(jsonObject.getString("name"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setPhoneNo(jsonObject.getString("phoneNo"));
                            user.setAvatarUrl(jsonObject.getString("avatarPic"));
                            user.setCoverUrl(jsonObject.getString("coverPic"));
                            user.setFaculty(jsonObject.getString("faculty"));
                            user.setCourse(jsonObject.getString("course"));
                            user.setDescription(jsonObject.getString("description"));
                            user.setStatus(jsonObject.getString("status"));

                            users.add(user);
                        }
                        Log.d("response", "posts size: " + users.size());

                        //Notify the adapter the date has been changed
                        adapter.notifyDataSetChanged();

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Exception>>>>>>>>>> " + e, Toast.LENGTH_SHORT)
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
    }

    //Grid view adapter
    class GridViewAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        private Context context;
        private List<User> items;

        public GridViewAdapter(Context context, List<User> items) {
            inflater= LayoutInflater.from(context);
            this.context = context;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public User getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            view = inflater.inflate(R.layout.row_item_subscription_user, parent, false);

            final CircleImageView userAvatar = (CircleImageView)view.findViewById(R.id.civ_subscription_avatar);
            NetworkImageView userAvatarTemp = (NetworkImageView)view.findViewById(R.id
                    .niv_subscription_tempAvatar);
            TextView userName = (TextView)view.findViewById(R.id.tv_subscription_name);

            userName.setText(items.get(position).getName());

            ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
            if(items.get(position).getAvatarUrl() != null && !items.get(position).getAvatarUrl()
                    .isEmpty() && items.get(position).getAvatarUrl() != ""){

                userAvatarTemp.setImageUrl(items.get(position).getAvatarUrl(), imageLoader);

                imageLoader.get(items.get(position).getAvatarUrl(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            userAvatar.setImageBitmap(response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }else{
                userAvatarTemp.setImageUrl(null, imageLoader);
                userAvatar.setImageResource(R.drawable.avatar);
            }

            return view;
        }
    }


}
