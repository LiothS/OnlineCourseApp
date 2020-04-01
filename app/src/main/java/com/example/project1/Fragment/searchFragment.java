package com.example.project1.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project1.Adapter.categoryRVAdapter;
import com.example.project1.Adapter.categorySearchAdapter;
import com.example.project1.Model.category_item;
import com.example.project1.R;

import java.util.ArrayList;


public class searchFragment extends Fragment {






    public searchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_search, container, false);
        ArrayList<category_item> items = new ArrayList<>();
        categorySearchAdapter adapter = new categorySearchAdapter(items,getActivity());
        RecyclerView recyclerView = rootView.findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
// let's create 10 random items
        String[] list={"Development","Business","IT & Software","Office Productivity","Personal Development","Design","Marketing","Lifestyle","Photography","Fitness","Music","Teaching","Writing","Language"};
        for (int i = 0; i < list.length; i++) {
            items.add(new category_item( list[i]));
            adapter.notifyDataSetChanged();
        }
        return rootView;
    }





}
