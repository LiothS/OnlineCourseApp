package com.example.project1.Adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class DownloadFileAdapter extends RecyclerView.Adapter<DownloadFileAdapter.CustomViewHolder>  {
    private ArrayList<String> items;
    private Context context;
    private String LessonID;
    public DownloadFileAdapter(ArrayList<String> items, Context context,String lessonID) {
        this.items = items;
        this.LessonID=lessonID;
        this.context = context;
    }

    @NonNull
    @Override
    public DownloadFileAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DownloadFileAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.down_docs_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadFileAdapter.CustomViewHolder holder, int position) {
        String temp=items.get(position);
        holder.textView1.setText(temp);
        if(temp.contains(".pdf"))
        {
            holder.fileicon.setImageResource(R.drawable.pdf_iconn);
        }

    }



    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private ImageView fileicon;

        public CustomViewHolder(View view) {
            super(view);
            textView1=view.findViewById(R.id.downFileName);
            fileicon=view.findViewById(R.id.downFileIcon);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse("https://udemy-online-courses.herokuapp.com/upload/lesson/"+ items.get(getAdapterPosition()));
                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    downloadmanager.enqueue(request);
                    Toast.makeText(context, "Đang tải xuống...", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
