package com.example.project1.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Adapter.JoinedLessonAdapter;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.MultiChoice;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class JoinedCourseLessons extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView nameOfCourse;
   JoinedLessonAdapter joinedLessonAdapter;
    ArrayList<Lesson> lessons;
    ImageView imageView;
    ProgressBar progressBar;
    TextView percent;
    float sendProgress=100;
    int phantram=0;
    com.example.project1.Model.courseItem courseItem;
    float percentCpl=0;
    boolean flag=false;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_course_lessons);
        AnhXa();
        imageView=findViewById(R.id.joinCourseLessonImg);
        progressBar=findViewById(R.id.circularProgressbarLesson);
        percent=findViewById(R.id.completeValueLesson);
        nameOfCourse=findViewById(R.id.joinedCourseNameInLesson);
     textView =findViewById(R.id.abcd);
     textView.setVisibility(View.GONE);
        ActionToolBar();
        courseItem = (com.example.project1.Model.courseItem) getIntent().getSerializableExtra("course");
        nameOfCourse.setText(courseItem.getTitle());
        GetProgress(1);



        progressBar.setProgress(phantram);
        percent.setText(String.valueOf(phantram));
        Picasso.get().load(courseItem.getUrl()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView);
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
        iMyService.getLesson("http://13.68.245.234:9000/lesson/get-lesson-by-id-course/"+courseItem.getID(),sharedPreferences.getString("token",null)).
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
                            temp2=response;
                            textView.setText(response);


                            //JSONObject jsonObject=new JSONObject(temp);

                            JSONArray ja=new JSONArray(response);
                            //JSONArray jsonArray=jsonObject.getJSONArray("");
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
                                   // Toast.makeText(JoinedCourseLessons.this, img, Toast.LENGTH_SHORT).show();
                                    multiChoices.add(new MultiChoice(jo1.getString("_id"),
                                            jo1.getString("A"),
                                            jo1.getString("B"),
                                            jo1.getString("C"),
                                            jo1.getString("D"),
                                            jo1.getString("answer"),
                                            jo1.getString("question"),
                                            img,
                                            String.valueOf(jo1.getInt("timeShow"))));
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
                                Boolean check=false;
                                try {
                                    check=jo.getBoolean("isCompleted");
                                }
                                catch(Exception e) {
                                   check=false;
                                }


                                lessons.add(new Lesson(jo.getString("_id"),
                                        jo.getString("video"),
                                        jo.getString("idCourse"),
                                        jo.getString("title"),
                                        jo.getString("order"),
                                        files,
                                        multiChoices,quiz,check));
                               joinedLessonAdapter.notifyDataSetChanged();



                            }
                            flag=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JoinedCourseLessons.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                           // Toast.makeText(JoinedCourseLessons.this, temp2, Toast.LENGTH_LONG).show();

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
                final Intent data = new Intent();
                data.putExtra(EXTRA_DATA, "success");
                setResult(Activity.RESULT_OK, data);

                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        final Intent data = new Intent();
        data.putExtra(EXTRA_DATA, "success");
        setResult(Activity.RESULT_OK, data);
       // Toast.makeText(this, "asdasd", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1900) {


            if(resultCode == Activity.RESULT_OK) {
                lessons.clear();

               GetProgress(1);

               GetLesson();



            } else {

            }
        }
    }

    String temp1="";
    String temp2="";
    private void GetProgress(int i) {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(JoinedCourseLessons.this).build();
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(JoinedCourseLessons.this);

        iMyService.getListComment("http://13.68.245.234:9000/join/get-progress-course-join-by-idUser-and-idCourse/"+sharedPreferences.getString("id",null)+"/"+courseItem.getID()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {
                        //Toast.makeText(JoinedCourseLessons.this, response, Toast.LENGTH_SHORT).show();
                        temp1=response;


                    }

                    @Override
                    public void onError(Throwable e) {




                    }

                    @Override
                    public void onComplete() {
                        try {
                            JSONObject jo=new JSONObject(temp1);
                            phantram=jo.getInt("percentCompleted");
                           // Toast.makeText(JoinedCourseLessons.this, ""+phantram, Toast.LENGTH_SHORT).show();
                            progressBar.setProgress(phantram);
                            percent.setText(String.valueOf(phantram)+"%");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JoinedCourseLessons.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                        }



                    }
                });

    }
}
