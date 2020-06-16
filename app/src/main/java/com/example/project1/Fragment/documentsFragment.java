package com.example.project1.Fragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Adapter.DownloadFileAdapter;
import com.example.project1.Adapter.LessonFileAdapter;
import com.example.project1.Model.Lesson;
import com.example.project1.R;

public class documentsFragment extends Fragment {
    public documentsFragment() {
    }
    View view;

    public documentsFragment(Lesson lesson) {
        this.lesson = lesson;
    }

    RecyclerView recyclerView;
    Lesson lesson;
    DownloadFileAdapter downloadFileAdapter;
    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.docs_fragment,container,false);
        recyclerView=view.findViewById(R.id.docsRecyclerView);
        downloadFileAdapter=new  DownloadFileAdapter(lesson.getFile(),getContext(),lesson.getID());
        downloadFileAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter( downloadFileAdapter);
        return view;
    }
}
