package com.example.project1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;

import java.util.List;

public class fileItem extends RecyclerView.Adapter<fileItem.ViewHolder> {
    private Context context;
    public List<String> fileNameList;
    public  List<String> fileDoneList;

    public fileItem(List<String> fileNameList, List<String> fileDoneList, Context context) {
        this.fileNameList = fileNameList;
        this.fileDoneList = fileDoneList;
        this.context=context;
    }

    public List<String> getFileNameList() {
        return fileNameList;
    }

    public void setFileNameList(List<String> fileNameList) {
        this.fileNameList = fileNameList;
    }

    public List<String> getFileDoneList() {
        return fileDoneList;
    }

    public void setFileDoneList(List<String> fileDoneList) {
        this.fileDoneList = fileDoneList;
    }

    @NonNull
    @Override
    public fileItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new  fileItem.ViewHolder(LayoutInflater.from(context).inflate(R.layout.file_update_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull fileItem.ViewHolder holder, int position) {

        String fileName=fileNameList.get(position);
        holder.textView.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }
    public  class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public  ViewHolder(View item){
            super(item);
            imageView=item.findViewById(R.id.fileicon);
            textView=item.findViewById(R.id.filename);
        }
    }
}
