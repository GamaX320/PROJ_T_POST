package com.tarpost.bryanty.proj_t_post.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.activity.EventMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.activity.UserProfileActivity;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.fragment.EventJoinFragment;
import com.tarpost.bryanty.proj_t_post.object.Event;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BRYANTY on 03-Feb-16.
 */
public class EventJoinAdapter  extends RecyclerView.Adapter<EventJoinAdapter.ViewHolder>  {
    private LayoutInflater inflater;
    List<Event> items= Collections.emptyList();
    private Context context;

    public EventJoinAdapter(Context context, List<Event> items) {
        inflater= LayoutInflater.from(context);
        this.items= items;
        this.context= context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.row_card_event_join, parent, false);
        ViewHolder viewHolder= new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Event currentItem= items.get(position);

        //  holder.userAvatar.setImageResource(currentItem.userAvatar);
        holder.userName.setText(currentItem.getCreatorName());
        holder.title.setText(currentItem.getTitle());
        holder.startDateTime.setText(DateUtil.convertDateToString(currentItem.getStartDateTime()));
        holder.endDateTime.setText(DateUtil.convertDateToString(currentItem.getEndDateTime()));

        Geocoder geocoder = new Geocoder(holder.itemView.getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(currentItem.getLocationLat(),
                    currentItem.getLocationLng(),1);

            if(addresses.size() > 0){
                Log.v("location", "location result > " + addresses.get(0).getLocality());
                Log.v("location","location result > "+addresses.get(0)
                        .getAddressLine(0));

                currentItem.setLocation(addresses.get(0).getLocality());
                holder.location.setText(R.string.text_location+": "+currentItem.getLocation());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
        if(currentItem.getAvatarUrl() != null && !currentItem.getAvatarUrl().isEmpty() &&
                currentItem.getAvatarUrl() != ""){
            holder.userAvatarTemp.setImageUrl(currentItem.getAvatarUrl(), imageLoader);

            imageLoader.get(currentItem.getAvatarUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        holder.userAvatar.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

        }else{
            holder.userAvatarTemp.setImageUrl(null, imageLoader);
            holder.userAvatar.setImageResource(R.drawable.avatar);
        }

        holder.event= currentItem;
    }

    public int getItemCount() {
        return items.size();
    }

    //view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView userAvatar;
        NetworkImageView userAvatarTemp;
        TextView userName;
        TextView title;
        TextView startDateTime;
        TextView endDateTime;
        TextView location;

        ImageButton cancelJoin, share, more;
        Event event;

        public ViewHolder(View itemView) {
            super(itemView);
            userAvatar= (CircleImageView)itemView.findViewById(R.id.circleImageView_event_join_userAvatar);
            userAvatarTemp = (NetworkImageView)itemView.findViewById(R.id.networkImageView_event_join_userAvatarTemp);
            userName= (TextView)itemView.findViewById(R.id.textView_event_join_userName);
            title= (TextView)itemView.findViewById(R.id.textView_event_join_title);
            startDateTime= (TextView)itemView.findViewById(R.id.textView_event_join_startDateTime);
            endDateTime= (TextView)itemView.findViewById(R.id.textView_event_join_endDateTime);
            location= (TextView)itemView.findViewById(R.id.textView_event_join_location);

            cancelJoin= (ImageButton)itemView.findViewById(R.id.imageButton_event_joing_cancel_join);
            share= (ImageButton)itemView.findViewById(R.id.imageButton_event_joing_share);
            more= (ImageButton)itemView.findViewById(R.id.imageButton_event_joing_more);
            cancelJoin.setOnClickListener(this);
            share.setOnClickListener(this);
            more.setOnClickListener(this);
            userAvatar.setOnClickListener(this);
        }

        //Bookmark and share button onClick listener
        @Override
        public void onClick(final View v){

            if(v.getId() == cancelJoin.getId()){

                if(event.getEventId() != null){

                    AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                            .setTitle(v.getResources().getString(R.string
                                    .text_dialog_skip_event_title))
                            .setMessage(v.getResources().getString(R.string
                                    .text_dialog_skip_event_content))
                            .setPositiveButton(R.string.text_dialog_confirm_yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    final ProgressDialog pdProgressAdd;
                                    final String DELETE_EVENT_MEMBER_URL = "http://projx320.webege" +
                                            ".com/tarpost/php/deleteEventMember.php";

                                    pdProgressAdd = new ProgressDialog(v.getContext());
                                    pdProgressAdd.setMessage(v.getResources().getString(R.string.text_dialog_removing));
                                    pdProgressAdd.setCancelable(false);

                                    pdProgressAdd.show();

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_EVENT_MEMBER_URL
                                            , new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            pdProgressAdd.dismiss();

                                            Toast.makeText(v.getContext(), v.getResources()
                                                            .getString(R.string.text_message_unjoin_event),
                                                    Toast
                                                    .LENGTH_LONG)
                                                    .show();

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            pdProgressAdd.dismiss();
                                            Toast.makeText(v.getContext(), v.getResources()
                                                            .getString(R.string.text_message_unjoin_event_failed),
                                                    Toast
                                                    .LENGTH_LONG)
                                                    .show();
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<String, String>();
                                            SharedPreferences sharedPreferences = v.getContext()
                                                    .getSharedPreferences("userLogin", Context.MODE_PRIVATE);
                                            params.put("eventMemId", sharedPreferences.getString("userId",null));
                                            params.put("eventId", event.getEventId().toString());

                                            return params;
                                        }
                                    };

                                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                            20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                    // Adding request to request queue
                                    MyApplication.getInstance().addToReqQueue(stringRequest);

                                }
                            })
                            .setNegativeButton(R.string.text_dialog_confirm_no, null).show();

                }

            }else if(v.getId() == share.getId()){

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, event.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, event.getContent()
                        + System.getProperty("line.separator")
                        + v.getResources().getString(R.string.text_startTime) + " :"
                        + event.getStartDateTime()
                        + System.getProperty("line.separator")
                        +v.getResources().getString(R.string.text_endTime)+" :"
                        +event.getEndDateTime()
                        + System.getProperty("line.separator")
                        +v.getResources().getString(R.string.text_location)+" :"
                        +event.getLocation());
                v.getContext(). startActivity(Intent.createChooser(intent, "Share"));

            }else if(v.getId() == more.getId()){
                Intent intent = new Intent(v.getContext(), EventMoreDetailsActivity.class);
                intent.putExtra("detailEvent", event);
                v.getContext().startActivity(intent);
            }else if(v.getId() == userAvatar.getId()){
                Intent intent = new Intent(v.getContext(), UserProfileActivity.class);
                intent.putExtra("userId", event.getCreatorId());
                v.getContext().startActivity(intent);
            }
        }
    }
}
