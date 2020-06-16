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

public class ParentCommentAdapter extends RecyclerView.Adapter<ParentCommentAdapter.CustomViewHolder> {
    private ArrayList<UserComment> items;
    private Context context;
    Lesson lesson;

    public ParentCommentAdapter(ArrayList<UserComment> items, Context context,Lesson lesson) {
        this.items = items;
        this.lesson=lesson;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentCommentAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentCommentAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.parent_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParentCommentAdapter.CustomViewHolder holder, int position) {

        Picasso.get().load(items.get(position).getAvatar()).placeholder(R.drawable.empty2).error(R.drawable.empty2).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.userAvatar);


        holder.userName.setText(items.get(position).getUserName());
        holder.cmtContent.setText(items.get(position).getContent());


    }



    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName, cmtContent;
        public CustomViewHolder(View view) {
            super(view);
            userAvatar=view.findViewById(R.id.commentAvatar);

            userName=view.findViewById(R.id.commentUserName);
            cmtContent=view.findViewById(R.id.CommentContent);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, CommentDetailActivity.class);
                    intent.putExtra("comment",items.get(getAdapterPosition()));
                    intent.putExtra("lesson",lesson);
                    context.startActivity(intent);
                    CustomIntent.customType(context,"bottom-to-up");

                }
            });

        }
    }
}
