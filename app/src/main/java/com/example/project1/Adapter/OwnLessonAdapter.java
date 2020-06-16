package com.example.project1.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Activity.CourseByCategory;
import com.example.project1.Activity.OwnLessonActivity;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.category_item;
import com.example.project1.R;

import java.util.ArrayList;

public class OwnLessonAdapter extends RecyclerView.Adapter<OwnLessonAdapter.CustomViewHolder> {
    private ArrayList<Lesson> items;
    private Context context;

    public OwnLessonAdapter(ArrayList<Lesson> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public OwnLessonAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OwnLessonAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.lesson_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OwnLessonAdapter.CustomViewHolder holder, int position) {
        holder.textView1.setText(items.get(position).getOrder());
        holder.textView2.setText(items.get(position).getTitle());
        String info="";
        info=info+ items.get(position).getMultiChoice().size()+" câu trắc nghiệm, "+ items.get(position).getFile().size()+ " file tài liệu";
        holder.textview3.setText(info);



    }



    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1,textView2, textview3;
        public CustomViewHolder(View view) {
            super(view);
            textView1=view.findViewById(R.id.lessonOrder);
            textView2=view.findViewById(R.id.lessonTitle);
            textview3=view.findViewById(R.id.lessonInfo);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, OwnLessonActivity.class);
                    intent.putExtra("lesson",items.get(getAdapterPosition()));
                    ((Activity) context).startActivityForResult(intent,1900);
                }
            });
        }
    }
}
