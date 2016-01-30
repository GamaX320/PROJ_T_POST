package com.tarpost.bryanty.proj_t_post.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.activity.PostMoreDetailsActivity;
import com.tarpost.bryanty.proj_t_post.application.MyApplication;
import com.tarpost.bryanty.proj_t_post.object.Post;

import java.util.Collections;
import java.util.List;

/**
 * Created by BRYANTY on 26-Jan-2016.
 */
public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {

    private LayoutInflater inflater;
    List<Post> items= Collections.emptyList();
    private Context context;

    public BookmarksAdapter(Context context, List<Post> items) {
        inflater= LayoutInflater.from(context);
        this.items= items;
        this.context= context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.row_card_bookmarks, parent, false);
        ViewHolder viewHolder= new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post currentItem= items.get(position);

        //Only display the bookmark status Active
        if(currentItem.getStatus().equals("A")){
            //  holder.userAvatar.setImageResource(currentItem.userAvatar);
            holder.userName.setText(currentItem.getCreatorName());
            holder.title.setText(currentItem.getTitle());
            holder.content.setText(currentItem.getContent());

            if( currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()){
                ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();
                holder.image.setImageUrl(currentItem.getImageUrl(),imageLoader);
            }

            holder.post= currentItem;
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //view holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userAvatar;
        TextView userName;
        TextView title;
        TextView content;
        NetworkImageView image;

        ImageButton share, delete, more;
        Post post;

        public ViewHolder(View itemView) {
            super(itemView);
            userAvatar= (ImageView)itemView.findViewById(R.id.circleImageView_bookmarks_userAvatar);
            userName= (TextView)itemView.findViewById(R.id.textView_bookmarks_userName);
            title= (TextView)itemView.findViewById(R.id.textView_bookmarks_title);
            content= (TextView)itemView.findViewById(R.id.textView_bookmarks_content);
            image= (NetworkImageView)itemView.findViewById(R.id.imageView_bookmarks_image);

            share= (ImageButton)itemView.findViewById(R.id.imageButton_bookmarks_share);
            delete= (ImageButton)itemView.findViewById(R.id.imageButton_bookmarks_delete);
            more= (ImageButton)itemView.findViewById(R.id.imageButton_bookmarks_more);
            share.setOnClickListener(this);
            delete.setOnClickListener(this);
            more.setOnClickListener(this);
        }

        //Bookmark and share button onClick listener
        @Override
        public void onClick(final View v){

            if(v.getId() == share.getId()){

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, post.getContent());
                v.getContext(). startActivity(Intent.createChooser(intent, "Share"));

            }else if(v.getId() == delete.getId()){

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, post.getContent());
                v.getContext(). startActivity(Intent.createChooser(intent, "Share"));

            }else if(v.getId() == more.getId()){

                Intent intent = new Intent(v.getContext(), PostMoreDetailsActivity.class);
                intent.putExtra("detailsPost", post);
                v.getContext().startActivity(intent);

            }
        }
    }
}
