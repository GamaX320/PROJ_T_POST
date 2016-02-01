package com.tarpost.bryanty.proj_t_post.adapter;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.tarpost.bryanty.proj_t_post.MainActivity;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.activity.PostMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.activity.UserProfileActivity;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.common.DateUtil;
import com.tarpost.bryanty.proj_t_post.object.Information;
import com.tarpost.bryanty.proj_t_post.object.Post;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by BRYANTY on 10-Jan-2016.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private LayoutInflater inflater;
    List<Post> items= Collections.emptyList();
    private Context context;

    public PostAdapter(Context context, List<Post> items) {
        inflater= LayoutInflater.from(context);
        this.items= items;
        this.context= context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.row_card_post, parent, false);
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

        if(currentItem.getCreatorAvatarUrl() != null && !currentItem.getCreatorAvatarUrl().isEmpty()){
            holder.userAvatarTemp.setImageUrl(currentItem.getCreatorAvatarUrl(), imageLoader);
        }

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
        TextView timeStamp;
        TextView title;
        TextView content;
        NetworkImageView image;

        ImageButton bookmark, share, more;
        Post post;

        public ViewHolder(View itemView) {
            super(itemView);
            userAvatar= (CircleImageView)itemView.findViewById(R.id.circleImageView_userAvatar);
            userAvatarTemp= (NetworkImageView)itemView.findViewById(R.id.networkImageView_userAvatarTemp);
            userName= (TextView)itemView.findViewById(R.id.textView_userName);
            timeStamp= (TextView)itemView.findViewById(R.id.textView_timeStamp);
            title= (TextView)itemView.findViewById(R.id.textView_title);
            content= (TextView)itemView.findViewById(R.id.textView_content);
            image= (NetworkImageView)itemView.findViewById(R.id.imageView_image);

            bookmark= (ImageButton)itemView.findViewById(R.id.imageButton_bookmark);
            share= (ImageButton)itemView.findViewById(R.id.imageButton_share);
            more= (ImageButton)itemView.findViewById(R.id.imageButton_more);
            bookmark.setOnClickListener(this);
            share.setOnClickListener(this);
            more.setOnClickListener(this);

            userAvatar.setOnClickListener(this);
        }

        //Bookmark and share button onClick listener
        @Override
        public void onClick(final View v){

            if(v.getId() == bookmark.getId()){

                if(post.getPostId() != null){
                    final ProgressDialog pdProgressAdd;
                    final String ADD_INFORMATION_URL = "http://projx320.webege.com/tarpost/php/insertBookmark.php";

                    pdProgressAdd = new ProgressDialog(v.getContext());
                    pdProgressAdd.setMessage("Adding...");
                    pdProgressAdd.setCancelable(false);

                    pdProgressAdd.show();

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_INFORMATION_URL
                            , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pdProgressAdd.dismiss();

                            Toast.makeText(v.getContext(), "Bookmark Added Successfully", Toast
                                    .LENGTH_SHORT)
                                    .show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pdProgressAdd.dismiss();
                            Toast.makeText(v.getContext(), "Bookmark Added Failed", Toast
                                    .LENGTH_SHORT)
                                    .show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            SharedPreferences sharedPreferences = v.getContext()
                                    .getSharedPreferences("userLogin", Context.MODE_PRIVATE);
                            params.put("userId", sharedPreferences.getString("userId",null));
                            params.put("postId", post.getPostId().toString());

                            return params;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            20000, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    // Adding request to request queue
                    MyApplication.getInstance().addToReqQueue(stringRequest);
                }

            }else if(v.getId() == share.getId()){

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, post.getContent());
                v.getContext(). startActivity(Intent.createChooser(intent, "Share"));

            }else if(v.getId() == more.getId()){

                Intent intent = new Intent(v.getContext(), PostMoreDetailsActivity.class);
                intent.putExtra("detailsPost", post);
                v.getContext().startActivity(intent);

            }else if(v.getId() == userAvatar.getId()){
                Intent intent = new Intent(v.getContext(), UserProfileActivity.class);
                intent.putExtra("userId", post.getCreatorId());
                v.getContext().startActivity(intent);
            }
        }
    }

}
