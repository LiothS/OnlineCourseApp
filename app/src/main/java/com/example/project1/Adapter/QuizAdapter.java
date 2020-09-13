package com.example.project1.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Model.MultiChoice;
import com.example.project1.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.CustomViewHolder> {
    private ArrayList<MultiChoice> items;
    private Context context;
    private String LessonID;
    public QuizAdapter(ArrayList<MultiChoice> items, Context context,String lessonID) {
        this.items = items;
        this.LessonID=lessonID;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuizAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.quiz_test_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAdapter.CustomViewHolder holder, int position) {
        holder.title.setText("Câu "+position+": "+items.get(position).getQuestion());
        holder.answer.setText("Đáp án: ");

    }
    boolean flag=false;
    TextView question, ansA, ansB, ansC, ansD;
    ImageView imgView;

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView title,answer;


        public CustomViewHolder(View view) {
            super(view);
        title=view.findViewById(R.id.quizTitle);
        answer=view.findViewById(R.id.quizTestAnswer);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder  mbuilder=new AlertDialog.Builder(context);
                LayoutInflater myInflater = LayoutInflater.from(context);
                View view=myInflater.inflate(R.layout.question_dialog,null);
                question=view.findViewById(R.id.popUpQuestion);
                ansA=view.findViewById(R.id.ansA);
                ansB=view.findViewById(R.id.ansB);
                ansC=view.findViewById(R.id.ansC);
                ansD=view.findViewById(R.id.ansD);

                imgView=view.findViewById(R.id.questionImage);
                mbuilder.setView(view);
                AlertDialog alertDialog=mbuilder.create();
                ansA.setText(items.get(getAdapterPosition()).getA());
                ansB.setText(items.get(getAdapterPosition()).getB());
                ansC.setText(items.get(getAdapterPosition()).getC());
                ansD.setText(items.get(getAdapterPosition()).getD());
                question.setText(items.get(getAdapterPosition()).getQuestion());

                if(items.get(getAdapterPosition()).getImage().contains("."))
                {
                    Picasso.get().load("http://13.68.245.234:9000/upload/lesson/"+items.get(getAdapterPosition()).getImage()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgView);
                    imgView.setVisibility(View.VISIBLE);
                }
                ansA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            answer.setText("A");
                            items.get(getAdapterPosition()).setUserAnswer("A");
                            alertDialog.dismiss();



                    }
                });
                ansB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            answer.setText("B");
                        items.get(getAdapterPosition()).setUserAnswer("B");
                            alertDialog.dismiss();


                    }
                });
                ansC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            answer.setText("C");
                        items.get(getAdapterPosition()).setUserAnswer("C");
                            alertDialog.dismiss();


                    }
                });
                ansD.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            answer.setText("D");
                        items.get(getAdapterPosition()).setUserAnswer("D");
                            alertDialog.dismiss();


                    }
                });

                alertDialog.show();
            }
        });


        }
    }
}
