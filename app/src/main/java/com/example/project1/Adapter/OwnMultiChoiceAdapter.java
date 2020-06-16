package com.example.project1.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Model.MultiChoice;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class OwnMultiChoiceAdapter extends RecyclerView.Adapter<OwnMultiChoiceAdapter.CustomViewHolder> {
    private ArrayList<MultiChoice> items;
    private Context context;
    private String LessonID;
    public OwnMultiChoiceAdapter(ArrayList<MultiChoice> items, Context context,String lessonID) {
        this.items = items;
        this.LessonID=lessonID;
        this.context = context;
    }

    @NonNull
    @Override
    public OwnMultiChoiceAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OwnMultiChoiceAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.own_choice_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OwnMultiChoiceAdapter.CustomViewHolder holder, int position) {
        String temp=items.get(position).getQuestion();
        holder.textView1.setText(temp);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog1= new AlertDialog.Builder(context)
                        .setTitle("Xóa câu hỏi")
                        .setMessage("Bạn có chắc muốn xóa câu hỏi này không ?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                RemoveItem(position);

                            }
                        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.show();

            }
        });


    }
    boolean flag=false;
    private void RemoveItem(int position) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(context).build();
        alertDialog.show();
        iMyService.deleteFile("https://udemy-online-courses.herokuapp.com/lesson/delete-a-multiple-choice/"+LessonID+"/"+items.get(position).getId(), sharedPreferences.getString("token",null)).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {

                        flag = true;



                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        flag=true;


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if(flag==true)
                        {
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();





                        }
                        else
                            Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });

        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());


    }


    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;

        private LinearLayout deleteBtn;
        public CustomViewHolder(View view) {
            super(view);
            textView1=view.findViewById(R.id.questionName);

            deleteBtn=view.findViewById(R.id.deleteQuestion);

        }
    }
}
