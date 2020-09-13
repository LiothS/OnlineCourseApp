package com.example.project1.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Activity.CommentDetailActivity;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.UserComment;
import com.example.project1.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

import static android.view.View.GONE;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CustomViewHolder> {
    private ArrayList<UserComment> items;
    private Context context;
private Lesson lesson;
    public CommentAdapter(ArrayList<UserComment> items, Context context,Lesson lesson) {
        this.items = items;
        this.lesson=lesson;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CustomViewHolder holder, int position) {
        if(!items.get(position).getCmtImg().isEmpty())
            Picasso.get().load("http://13.68.245.234:9000/upload/comment_image/"+items.get(position).getCmtImg()).placeholder(R.drawable.empty).error(R.drawable.empty).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.cmtImg);
        Picasso.get().load(items.get(position).getAvatar()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.userAvatar);

        if(items.get(position).getCmtImg().isEmpty()) holder.cmtImg.setVisibility(GONE);
    if(items.get(position).getChildrenComments().length()>0) holder.moreCmt.setVisibility(View.VISIBLE);
        holder.userName.setText(items.get(position).getUserName());
        holder.cmtContent.setText(items.get(position).getContent());
        holder.replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CommentDetailActivity.class);
                intent.putExtra("comment",items.get(position));
                intent.putExtra("lesson",lesson);
                context.startActivity(intent);
                ((Activity)context).finish();
                CustomIntent.customType(context,"bottom-to-up");
            }
        });
        holder.moreCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CommentDetailActivity.class);
                intent.putExtra("comment",items.get(position));
                intent.putExtra("lesson",lesson);

                context.startActivity(intent);
                ((Activity)context).finish();
                CustomIntent.customType(context,"bottom-to-up");
            }
        });

    }



    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar, cmtImg,childrenAvatar;
        TextView userName, cmtContent, postTime, replyBtn,childrenUserName, childrenCmtContent, moreCmt;
        public CustomViewHolder(View view) {
            super(view);
           userAvatar=view.findViewById(R.id.parentAvatar);
           cmtImg=view.findViewById(R.id.parentImage);
           userName=view.findViewById(R.id.parentUserName);
           cmtContent=view.findViewById(R.id.parentCommentContent);
           postTime=view.findViewById(R.id.parentPostTime);
           replyBtn=view.findViewById(R.id.parentReplyBtn);
           childrenAvatar=view.findViewById(R.id.childrenAvatar);
           childrenUserName=view.findViewById(R.id.childrenUserName);
           childrenCmtContent=view.findViewById(R.id.childrenCommentContent);
            moreCmt=view.findViewById(R.id.moreComment);
        }
    }
}
