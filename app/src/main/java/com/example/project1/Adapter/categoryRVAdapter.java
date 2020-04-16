package com.example.project1.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Activity.CourseByCategory;
import com.example.project1.Activity.CreateCourseActivity;
import com.example.project1.Activity.UserInfoActivity;
import com.example.project1.Model.category_item;
import com.example.project1.R;

import java.util.ArrayList;

public class categoryRVAdapter extends RecyclerView.Adapter<categoryRVAdapter.CustomViewHolder> {
    private ArrayList<category_item> items;
    private Context context;

    public categoryRVAdapter(ArrayList<category_item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public categoryRVAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.category_item2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull categoryRVAdapter.CustomViewHolder holder, int position) {
        holder.textView.setText(items.get(position).getname().toUpperCase());



    }



    @Override
    public int getItemCount() {
        return items.size();
    }
public class CustomViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    public CustomViewHolder(View view) {
        super(view);
        textView=view.findViewById(R.id.category_tv);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CourseByCategory.class);
                intent.putExtra("name",items.get(getAdapterPosition()).getname());
                intent.putExtra("ID",items.get(getAdapterPosition()).getID());
                //Toast.makeText(context, items.get(getAdapterPosition()).getID(), Toast.LENGTH_SHORT).show();
               context.startActivity(intent);
            }
        });
    }
}
}
