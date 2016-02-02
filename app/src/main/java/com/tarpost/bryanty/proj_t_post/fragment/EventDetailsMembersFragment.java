package com.tarpost.bryanty.proj_t_post.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import com.tarpost.bryanty.proj_t_post.object.Event;
import com.tarpost.bryanty.proj_t_post.object.Post;
import com.tarpost.bryanty.proj_t_post.object.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BRYANTY on 27-Jan-2016.
 */
public class EventDetailsMembersFragment extends Fragment {

    private ListView lv;
    private List<User> users;
    private EventDetailMemberAdapter adapter;
    private Event event;

    private static final String GET_EVENT_MEMBERS_URL = "http://projx320.webege" +
            ".com/tarpost/php/getAllEventMembers.php";

    public EventDetailsMembersFragment(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_event_details_members, container,
                false);

        Bundle bundle= getActivity().getIntent().getExtras();
        Event event= bundle.getParcelable("detailEvent");
        this.event = event;

        lv = (ListView) view.findViewById(R.id.lvEventDetailsMembers);

        users= new ArrayList<>();

        setupListView(lv);

        return view;
    }

    private void setupListView(ListView lv){
        getData();
        adapter = new EventDetailMemberAdapter(getActivity(), users);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                GET_EVENT_MEMBERS_URL+"?eventId="+event.getEventId()
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

    //Event Details Members Adapter
    class EventDetailMemberAdapter extends BaseAdapter{
        private Context context;
        private LayoutInflater inflater;
        private List<User> users;

        public EventDetailMemberAdapter(Context context, List<User> users) {
            this.context = context;
            this.users = users;
            inflater= LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           // convertView= inflater.inflate(R.layout.row_item_event_member,parent, false);
            convertView= inflater.inflate(R.layout.row_item_event_member, null);
            ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

            TextView memberName = (TextView)convertView.findViewById(R.id.eventMember_userName);
            final CircleImageView memberAvatar = (CircleImageView)convertView.findViewById(R.id
                    .eventMember_avatar);
            NetworkImageView memberAvatarTemp = (NetworkImageView)convertView.findViewById(R.id
                    .eventMember_tempAvatar);

            User user = users.get(position);

            memberName.setText(user.getName());

            if(user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty() && user.getAvatarUrl
                    () != ""){
                memberAvatarTemp.setImageUrl(user.getAvatarUrl(), imageLoader);

                //avatar image loader listener
                imageLoader.get(user.getAvatarUrl(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
                            memberAvatar.setImageBitmap(response.getBitmap());
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }else{
                memberAvatar.setImageResource(R.drawable.avatar);
            }


            return convertView;
        }
    }
}
