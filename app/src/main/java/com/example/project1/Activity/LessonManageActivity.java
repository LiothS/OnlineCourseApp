package com.example.project1.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

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
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LessonManageActivity extends AppCompatActivity {
    Button createLesson;
    Toolbar toolbar;
    RecyclerView recyclerView;
    OwnLessonAdapter ownLessonAdapter;
    ArrayList<Lesson> lessons;
    courseItem courseItem;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_manage);
        createLesson=findViewById(R.id.createNewLesson);
        toolbar=findViewById(R.id.lessonManageToolbar);
        recyclerView=findViewById(R.id.my_created_lesson);
        lessons=new ArrayList<>();
        ownLessonAdapter=new OwnLessonAdapter(lessons,this);
        ownLessonAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(ownLessonAdapter);
        ActionToolBar();
       courseItem = (com.example.project1.Model.courseItem) getIntent().getSerializableExtra("course");
        GetLesson();
        createLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LessonManageActivity.this,CreateLesson.class);
                intent.putExtra("course",courseItem);
                startActivityForResult(intent,1899);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK&&requestCode==1899)
        {
            lessons.clear();
            ownLessonAdapter.notifyDataSetChanged();
            if(data!=null) GetLesson();
        }
        if(resultCode == RESULT_OK&&requestCode==1900)
        {
            lessons.clear();
            ownLessonAdapter.notifyDataSetChanged();
            if(data!=null) GetLesson();
        }
    }

    private void GetLesson() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.getLesson("http://52.152.163.79:9000/lesson/get-lesson-by-id-course/"+courseItem.getID()).
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
                                    }
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
                                lessons.add(new Lesson(jo.getString("_id"),
                                        jo.getString("video"),
                                        jo.getString("idCourse"),
                                        jo.getString("title"),
                                        jo.getString("order"),
                                        files,
                                        multiChoices));
                                ownLessonAdapter.notifyDataSetChanged();



                            }
                            flag=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LessonManageActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(LessonManageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(LessonManageActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
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
