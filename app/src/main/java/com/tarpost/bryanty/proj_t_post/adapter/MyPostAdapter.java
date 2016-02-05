package com.tarpost.bryanty.proj_t_post.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.tarpost.bryanty.proj_t_post.activity.PostMoreDetailsActionActivity;
import com.tarpost.bryanty.proj_t_post.activity.PostMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.object.Post;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BRYANTY on 25-Jan-2016.
 */
public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder>{
    private LayoutInflater inflater;
    List<Post> items= Collections.emptyList();
    private Context context;

    public MyPostAdapter(Context context, List<Post> items) {
        inflater= LayoutInflater.from(context);
        this.items= items;
        this.context= context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.row_card_mypost, parent, false);
        ViewHolder viewHolder= new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post currentItem= items.get(position);

        //  holder.userAvatar.setImageResource(currentItem.userAvatar);
        holder.userName.setText(currentItem.getCreatorName());
        holder.title.setText(currentItem.getTitle());
        holder.content.setText(currentItem.getContent());

        ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
        if( currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()){
            holder.image.setImageUrl(currentItem.getImageUrl(),imageLoader);
        }

        if(currentItem.getCreatorAvatarUrl() != null && !currentItem.getCreatorAvatarUrl()
                .isEmpty()){
            holder.userAvatarTemp.setImageUrl(currentItem.getCreatorAvatarUrl(), imageLoader);
            //avatar image loader listener
            imageLoader.get(currentItem.getCreatorAvatarUrl(), new ImageLoader.ImageListener() {
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
        }else {
            holder.userAvatarTemp.setImageUrl(null, imageLoader);
            holder.userAvatar.setImageResource(R.drawable.avatar);
        }

        holder.timeStamp.setText(DateUtil.getTimeRangeStr(currentItem.getUpdateDateTime()));
        holder.post= currentItem;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView userAvatar;
        NetworkImageView userAvatarTemp;
        TextView userName;
        TextView title;
        TextView content;
        NetworkImageView image;
        TextView timeStamp;

        ImageButton  modify, delete, share, more;
        Post post;

        public ViewHolder(View itemView) {
            super(itemView);
            userAvatar= (CircleImageView)itemView.findViewById(R.id.circleImageView_mypost_userAvatar);
            userAvatarTemp= (NetworkImageView)itemView.findViewById(R.id
                    .networkImageView_mypost_userAvatarTemp);
            userName= (TextView)itemView.findViewById(R.id.textView_mypost_userName);
            title= (TextView)itemView.findViewById(R.id.textView_mypost_title);
            content= (TextView)itemView.findViewById(R.id.textView_mypost_content);
            image= (NetworkImageView)itemView.findViewById(R.id.imageView_mypost_image);
            timeStamp= (TextView)itemView.findViewById(R.id.textView_mypost_timeStamp);

            modify= (ImageButton)itemView.findViewById(R.id.imageButton_mypost_modify);
            delete= (ImageButton)itemView.findViewById(R.id.imageButton_mypost_delete);
            more= (ImageButton)itemView.findViewById(R.id.imageButton_mypost_more);
            share= (ImageButton)itemView.findViewById(R.id.imageButton_mypost_share);
            modify.setOnClickListener(this);
            delete.setOnClickListener(this);
            more.setOnClickListener(this);
            share.setOnClickListener(this);
        }

        //Bookmark and share button onClick listener
        @Override
        public void onClick(final View v){

            if(v.getId() == modify.getId()) {
                Intent intent = new Intent(v.getContext(), PostMoreDetailsActionActivity.class);
                intent.putExtra("mode", "MODIFY");
                intent.putExtra("detailsPost", post);
                v.getContext().startActivity(intent);

            }else if(v.getId() == delete.getId()){
                AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                        .setTitle(v.getResources().getString(R.string
                                .text_dialog_confirm_title))
                        .setMessage(v.getResources().getString(R.string
                                .text_dialog_confirm_content))
                        .setPositiveButton(R.string.text_dialog_confirm_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                final ProgressDialog pdProgressAdd;
                                final String DELETE_EVENT_MEMBER_URL = "http://projx320.webege" +
                                        ".com/tarpost/php/updatePostStatus.php";

                                pdProgressAdd = new ProgressDialog(v.getContext());
                                pdProgressAdd.setMessage(v.getResources().getString(R.string.text_dialog_removing));
                                pdProgressAdd.setCancelable(false);

                                pdProgressAdd.show();

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_EVENT_MEMBER_URL
                                        , new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        pdProgressAdd.dismiss();

                                        Toast.makeText(v.getContext(), v.getResources().getString
                                                (R.string.text_message_delete_success), Toast
                                                .LENGTH_LONG)
                                                .show();

                                        Log.v("delete post","post result "+response);

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        pdProgressAdd.dismiss();
                                        Toast.makeText(v.getContext(), v.getResources().getString
                                                (R.string.text_message_delete_failed), Toast
                                                .LENGTH_LONG)
                                                .show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("postId", post.getPostId().toString());
                                        params.put("status", "D");

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

            }else if(v.getId() == share.getId()){

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, post.getContent());
                v.getContext().startActivity(Intent.createChooser(intent, "Share"));

            }else if(v.getId() == more.getId()){

                Intent intent = new Intent(v.getContext(), PostMoreDetailsActivity.class);
                intent.putExtra("detailsPost", post);
                v.getContext().startActivity(intent);

            }
        }
    }
}
