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

import java.util.Collections;
import java.util.List;

/**
 * Created by BRYANTY on 13/06/2015.
 */
public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InformationViewHolder> {

    private LayoutInflater inflater;
    List<Information> item= Collections.emptyList();
    private Context context;

    public InformationAdapter(Context context, List<Information> item) {
        inflater= LayoutInflater.from(context);
        this.item= item;
        this.context= context;
    }

    @Override
    public InformationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= inflater.inflate(R.layout.row_card_information, parent, false);
        InformationViewHolder viewHolder= new InformationViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InformationViewHolder holder, int position) {
        Information currentItem= item.get(position);

        holder.userAvatar.setImageResource(currentItem.userAvatar);
        holder.userName.setText(currentItem.userName);
        holder.title.setText(currentItem.title);
        holder.content.setText(currentItem.content);
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    //view holder
    class InformationViewHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar;
        TextView userName;
        TextView title;
        TextView content;

        public InformationViewHolder(View itemView) {
            super(itemView);
            userAvatar= (ImageView)itemView.findViewById(R.id.cardview_userAvatar);
            userName= (TextView)itemView.findViewById(R.id.cardview_userName);
            title= (TextView)itemView.findViewById(R.id.cardview_title);
            content= (TextView)itemView.findViewById(R.id.cardview_content);

        }
    }
}
