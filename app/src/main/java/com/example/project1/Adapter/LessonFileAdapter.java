package com.example.project1.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.project1.Activity.CreatedCourse;
import com.example.project1.Activity.OwnLessonActivity;
import com.example.project1.Model.Lesson;
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

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class LessonFileAdapter extends RecyclerView.Adapter<LessonFileAdapter.CustomViewHolder> {
    private ArrayList<String> items;
    private Context context;
    private String LessonID;
    public LessonFileAdapter(ArrayList<String> items, Context context,String lessonID) {
        this.items = items;
        this.LessonID=lessonID;
        this.context = context;
    }

    @NonNull
    @Override
    public LessonFileAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LessonFileAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.lesson_file_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LessonFileAdapter.CustomViewHolder holder, int position) {
        String temp=items.get(position);
       holder.textView1.setText(temp);
       if(temp.contains(".pdf"))
       {
           holder.fileicon.setImageResource(R.drawable.pdf_iconn);
       }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog1= new AlertDialog.Builder(context)
                        .setTitle("Xóa file tài liệu")
                        .setMessage("Bạn có chắc muốn xóa file này không ?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeAt(position);

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
    public void removeAt(int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(context).build();
        alertDialog.show();
        iMyService.deleteFile("http://52.152.163.79:9000/lesson/delete-lesson-file/"+LessonID+"/"+items.get(position), sharedPreferences.getString("token",null)).
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
        private ImageView fileicon;
        private LinearLayout deleteBtn;
        public CustomViewHolder(View view) {
            super(view);
            textView1=view.findViewById(R.id.lessonFileName);
          fileicon=view.findViewById(R.id.fileImageIcon);
          deleteBtn=view.findViewById(R.id.deleteFile);

        }
    }
}
