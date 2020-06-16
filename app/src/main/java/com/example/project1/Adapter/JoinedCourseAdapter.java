package com.example.project1.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Activity.CourseDetail;
import com.example.project1.Activity.JoinedCourseLessons;
import com.example.project1.Model.courseItem;
import com.example.project1.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class JoinedCourseAdapter extends RecyclerView.Adapter<JoinedCourseAdapter.CustomViewHolder> {
    private ArrayList<courseItem> items;
    private Context context;


    public JoinedCourseAdapter(ArrayList<courseItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public JoinedCourseAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JoinedCourseAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.joined_course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JoinedCourseAdapter.CustomViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());


        Picasso.get().load(items.get(position).getUrl()).placeholder(R.drawable.empty2).error(R.drawable.empty2).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView title, joinedAt;
        private ImageView imageView;

        public CustomViewHolder(View view) {
            super(view);
            title=view.findViewById(R.id.joinedCourseName1);
           joinedAt=view.findViewById(R.id.joinedAt);
            imageView=view.findViewById(R.id.joinedCourseImg);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, JoinedCourseLessons.class);
                    intent.putExtra("course",items.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
