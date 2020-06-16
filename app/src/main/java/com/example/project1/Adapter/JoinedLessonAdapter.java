package com.example.project1.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Activity.LessonDetailActivity;
import com.example.project1.Activity.OwnLessonActivity;
import com.example.project1.Model.Lesson;
import com.example.project1.R;

import java.util.ArrayList;

public class JoinedLessonAdapter extends RecyclerView.Adapter<JoinedLessonAdapter.CustomViewHolder>  {
    private ArrayList<Lesson> items;
    private Context context;

    public JoinedLessonAdapter(ArrayList<Lesson> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public JoinedLessonAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JoinedLessonAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.joined_course_lesson_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JoinedLessonAdapter.CustomViewHolder holder, int position) {
        //holder.textView1.setText(items.get(position).getOrder());
        holder.textView2.setText("Bài "+ items.get(position).getOrder()+"- "+items.get(position).getTitle());
        String info="";




    }



    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1,textView2, textview3;
        public CustomViewHolder(View view) {
            super(view);

            textView2=view.findViewById(R.id.joinedLessonTitle);
            ProgressBar progressBar=view.findViewById(R.id.circularProgressbar);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, LessonDetailActivity.class);
                    intent.putExtra("lesson",items.get(getAdapterPosition()));
                    ((Activity) context).startActivityForResult(intent,1900);
                }
            });
        }
    }
}
