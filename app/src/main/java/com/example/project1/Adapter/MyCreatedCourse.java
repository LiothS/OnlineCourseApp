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

import com.example.project1.Activity.CourseByCategory;
import com.example.project1.Activity.CreatedCourse;
import com.example.project1.Model.courseItem;
import com.example.project1.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MyCreatedCourse extends RecyclerView.Adapter< MyCreatedCourse.CustomViewHolder> {
    private ArrayList<courseItem> items;
    private Context context;


    public  MyCreatedCourse(ArrayList<courseItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public  MyCreatedCourse.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new  MyCreatedCourse.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull  MyCreatedCourse.CustomViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        NumberFormat formatter = new DecimalFormat("#,###");
        double price=(double)items.get(position).getPrice();
        String formattedNumber1 = formatter.format(price);
        if(formattedNumber1.equals("0")) holder.fee.setText("Miễn phí");
        else
            holder.fee.setText(formattedNumber1+" đ");

        Picasso.get().load(items.get(position).getUrl()).placeholder(R.drawable.empty).error(R.drawable.empty).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
        holder.ratingBar.setRating(items.get(position).getRating());

        if(items.get(position).getDiscount()!=0)
        {

            holder.discount.setPaintFlags(holder.fee.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            price=price-(price*items.get(position).getDiscount())/100;
            String formattedNumber2 = formatter.format(price);
            holder.fee.setText(formattedNumber2+" đ");
            holder.discount.setText(formattedNumber1);
        }
        else holder.discount.setVisibility(View.GONE);


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

        private TextView title,fee,discount;
        private ImageView imageView;
        private RatingBar ratingBar;
        public CustomViewHolder(View view) {
            super(view);
            title=view.findViewById(R.id.courseName1);
            fee=view.findViewById(R.id.cousers_fee);
            imageView=view.findViewById(R.id.courseImage1);
            ratingBar=view.findViewById(R.id.course_rating);
            discount=view.findViewById(R.id.discount);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, CreatedCourse.class);
                    intent.putExtra("course",items.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

        }
    }
}