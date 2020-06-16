package com.example.project1.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.project1.Adapter.CartCourseAdapter;
import com.example.project1.Model.courseItem;
import com.example.project1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class cartFragment extends Fragment {


    RecyclerView recyclerView;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    CartCourseAdapter cartCourseAdapter;


    public cartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = rootView.findViewById(R.id.cartRecyclerView);
        cartCourseAdapter=new CartCourseAdapter(courseItems,getContext());
        recyclerView.setAdapter(cartCourseAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));

        LoadCourseInCart();

        return  rootView;
    }

    private void LoadCourseInCart() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        JSONArray cartArray;
        try {
           // Toast.makeText(getContext(), "here", Toast.LENGTH_SHORT).show();
            cartArray= new JSONArray(sharedPreferences.getString("cartArray", "[]"));

            for(int i=0;i<cartArray.length();i++){
                JSONObject jo=cartArray.getJSONObject(i);
                courseItems.add(new courseItem(jo.getString("courseImage"),jo.getString("title"),jo.getString("author"),jo.getString("courseID")));
            }
            cartCourseAdapter.notifyDataSetChanged();




        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


}
