package com.example.project1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Model.category_item;
import com.example.project1.R;

import java.util.ArrayList;

public class categorySearchAdapter extends RecyclerView.Adapter<categorySearchAdapter.CustomViewHolder> {
    private ArrayList<category_item> items;
    private Context context;

    public categorySearchAdapter(ArrayList<category_item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public categorySearchAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.search_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull categorySearchAdapter.CustomViewHolder holder, int position) {
        holder.textView.setText(items.get(position).getname());

    }



    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        public CustomViewHolder(View view) {
            super(view);
            textView=view.findViewById(R.id.tvCategory);

        }
    }
}
