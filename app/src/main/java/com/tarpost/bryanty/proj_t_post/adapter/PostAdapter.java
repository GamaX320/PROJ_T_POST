package com.tarpost.bryanty.proj_t_post.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.object.Information;
import com.tarpost.bryanty.proj_t_post.object.Post;

import java.util.Collections;
import java.util.List;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post currentItem= items.get(position);

      //  holder.userAvatar.setImageResource(currentItem.userAvatar);
        holder.userName.setText(currentItem.getCreatorName());
        holder.title.setText(currentItem.getTitle());
        holder.content.setText(currentItem.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //view holder
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar;
        TextView userName;
        TextView title;
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            userAvatar= (ImageView)itemView.findViewById(R.id.circleImageView_userAvatar);
            userName= (TextView)itemView.findViewById(R.id.textView_userName);
            title= (TextView)itemView.findViewById(R.id.textView_title);
            content= (TextView)itemView.findViewById(R.id.textView_content);

        }
    }

}
