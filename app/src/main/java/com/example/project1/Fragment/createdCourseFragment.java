package com.example.project1.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Activity.CourseByCategory;
import com.example.project1.Activity.CreateCourseActivity;
import com.example.project1.Activity.HomeActivity;
import com.example.project1.Adapter.MyCreatedCourse;
import com.example.project1.Adapter.courseAdapter;
import com.example.project1.Adapter.topCourseAdapter;
import com.example.project1.Model.courseItem;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class createdCourseFragment extends Fragment {




    ArrayList<courseItem> courseItems = new ArrayList<>();
    com.example.project1.Adapter.MyCreatedCourse courseAdapter;
   

    public createdCourseFragment() {
        // Required empty public constructor
    }

RecyclerView recyclerView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview =inflater.inflate(R.layout.fragment_created_course, container, false);
        recyclerView=rootview.findViewById(R.id.my_created_course);
        courseAdapter=new MyCreatedCourse(courseItems,getActivity());
        courseAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(courseAdapter);
        Load();
        Button createBtn =rootview.findViewById(R.id.createNewCourse);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllCategory();
            }
        });
        return  rootview;
    }
    private void getAllCategory(){
        ArrayList<String> categoriesName = new ArrayList<String>();
        ArrayList<String> categoriesID = new ArrayList<String>();
        IMyService iMyService;
        AlertDialog alertDialog;
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog.show();
        iMyService.getAllCategory().
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {




                        try {

                            String temp=response;

                            //JSONObject jsonObject=new JSONObject(temp);
                           // Toast.makeText(getContext(), "here", Toast.LENGTH_SHORT).show();
                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                String tempName=jo.getString("name");
                                String tempID=jo.getString("_id");

                                categoriesName.add(tempName);
                                categoriesID.add(tempID);
                                flag=true;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }





                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            //Toast.makeText(HomeActivity.this, "size: "+categoriesID.size(), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(getContext(), CreateCourseActivity.class);
                            intent.putStringArrayListExtra("list1",categoriesName);
                            intent.putStringArrayListExtra("list2",categoriesID);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });


    }
boolean flag=false;
    private void Load() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        iMyService.getCourseByIDUser("http://13.68.245.234:9000/course/getby-iduser/"+sharedPreferences.getString("id","")).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {



                        try {

                            String temp=response;

                            //JSONObject jsonObject=new JSONObject(temp);

                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                courseItems.add(new courseItem( "http://13.68.245.234:9000/upload/course_image/"+jo.getString("image"),
                                        jo.getString("name"),"0",jo.getJSONObject("idUser").getString("name"),
                                        Float.valueOf(jo.getJSONObject("vote").getString("EVGVote")),
                                        Float.valueOf(jo.getString("price")),
                                        Float.valueOf(jo.getString("discount")),
                                        Float.valueOf(jo.getJSONObject("vote").getString("totalVote")),jo.getString("goal"),jo.getString("description"),jo.getString("_id"),
                                        jo.getJSONObject("category").getString("name"),
                                        jo.getJSONObject("category").getString("_id"),
                                        jo.getString("ranking"),
                                        jo.getString("created_at")));
                                courseAdapter.notifyDataSetChanged();

                                // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();


                            }
                            flag=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }




                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


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


                        }
                        else
                            Toast.makeText(getContext(), "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }


}
