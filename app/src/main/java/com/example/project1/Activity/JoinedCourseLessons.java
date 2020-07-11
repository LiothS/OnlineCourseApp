package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.example.project1.Adapter.JoinedLessonAdapter;
import com.example.project1.Adapter.OwnLessonAdapter;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.MultiChoice;
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

public class JoinedCourseLessons extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

   JoinedLessonAdapter joinedLessonAdapter;
    ArrayList<Lesson> lessons;
    com.example.project1.Model.courseItem courseItem;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_course_lessons);
        AnhXa();
        ActionToolBar();
        courseItem = (com.example.project1.Model.courseItem) getIntent().getSerializableExtra("course");
        lessons=new ArrayList<>();
        joinedLessonAdapter=new  JoinedLessonAdapter(lessons,this);
        joinedLessonAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter( joinedLessonAdapter);
        GetLesson();
    }

    private void GetLesson() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.getLesson("http://52.152.163.79:9000/lesson/get-lesson-by-id-course/"+courseItem.getID(),sharedPreferences.getString("token",null)).
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
                                JSONArray ja1=jo.getJSONArray("doc");
                                ArrayList<String> files=new ArrayList<>();
                                for(int j=0;j<ja1.length();j++)
                                {
                                    files.add(ja1.getString(j));
                                }
                                ArrayList<MultiChoice> multiChoices=new ArrayList<>();
                                JSONArray ja2=jo.getJSONArray("popupQuestion");
                                for(int x=0;x<ja2.length();x++)
                                {

                                    JSONObject jo1=ja2.getJSONObject(x);
                                    String img="";
                                    try {
                                        img=jo1.getString("image");
                                    }
                                    catch(Exception e) {
                                        img="";
                                        //Toast.makeText(JoinedCourseLessons.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(JoinedCourseLessons.this, img, Toast.LENGTH_SHORT).show();
                                    multiChoices.add(new MultiChoice(jo1.getString("_id"),
                                            jo1.getString("A"),
                                            jo1.getString("B"),
                                            jo1.getString("C"),
                                            jo1.getString("D"),
                                            jo1.getString("answer"),
                                            jo1.getString("question"),
                                            img,
                                            jo1.getString("timeShow")));
                                }
                                //multiChoices
                                ArrayList<MultiChoice> quiz=new ArrayList<>();
                                JSONArray ja3=jo.getJSONArray("multipleChoices");
                                for(int x=0;x<ja3.length();x++)
                                {

                                    JSONObject jo2=ja3.getJSONObject(x);
                                    String img1="";
                                    try {
                                        img1=jo2.getString("image");
                                    }
                                    catch(Exception e) {
                                        img1="";
                                    }
                                    quiz.add(new MultiChoice(jo2.getString("_id"),
                                            jo2.getString("A"),
                                            jo2.getString("B"),
                                            jo2.getString("C"),
                                            jo2.getString("D"),
                                            jo2.getString("answer"),
                                            jo2.getString("question"),
                                            img1,
                                            ""));
                                }
                                lessons.add(new Lesson(jo.getString("_id"),
                                        jo.getString("video"),
                                        jo.getString("idCourse"),
                                        jo.getString("title"),
                                        jo.getString("order"),
                                        files,
                                        multiChoices,quiz));
                               joinedLessonAdapter.notifyDataSetChanged();



                            }
                            flag=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JoinedCourseLessons.this, e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(JoinedCourseLessons.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(JoinedCourseLessons.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void AnhXa() {
        toolbar=findViewById(R.id.joineLessonTB);
        recyclerView=findViewById(R.id.joinedCourseLessons);

    }
    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
