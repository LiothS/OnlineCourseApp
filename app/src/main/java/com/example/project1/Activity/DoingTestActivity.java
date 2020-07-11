package com.example.project1.Activity;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Adapter.OwnMultiChoiceAdapter;
import com.example.project1.Adapter.QuizAdapter;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.MultiChoice;
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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class DoingTestActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button submitBtn;
    Lesson lesson;
    ArrayList<MultiChoice> multiChoiceArrayList=new ArrayList<>();
    QuizAdapter quizAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_test);
        toolbar=findViewById(R.id.doingTestTB);
        recyclerView=findViewById(R.id.quizTestRV);
        submitBtn=findViewById(R.id.submitBtn);
        ActionToolBar();
        lesson= (Lesson) getIntent().getSerializableExtra("lesson");

       quizAdapter=new QuizAdapter(multiChoiceArrayList,this,lesson.getID());

        quizAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(quizAdapter);
        for(int i=0;i<multiChoiceArrayList.size();i++)
            Toast.makeText(this, multiChoiceArrayList.get(i).getUserAnswer(), Toast.LENGTH_SHORT).show();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Submit();
            }
        });
        GetQuiz();


    }

    private void GetQuiz() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();

        iMyService.getListComment("http://52.152.163.79:9000/lesson/get-multiple-choice-for-test/"+lesson.getID()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {

                        temp=response;
                        try {
                            JSONArray JA =new JSONArray(response);
                            JSONObject jo=JA.getJSONObject(0);
                            JSONArray jsonArray= jo.getJSONArray("multipleChoices");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jo1=jsonArray.getJSONObject(i);
                                String img1="";
                                try {
                                    img1=jo1.getString("image");
                                }
                                catch(Exception e) {
                                    img1="";
                                }
                                multiChoiceArrayList.add(new MultiChoice(jo1.getString("_id"),
                                        jo1.getString("A"),
                                        jo1.getString("B"),
                                        jo1.getString("C"),
                                        jo1.getString("D"),
                                        "",
                                        jo1.getString("question"),
                                        img1,""));

                            }
                            quizAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();

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
                        Toast.makeText(DoingTestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);



                    }
                });

    }

    boolean flag=false;
    String temp="";
    private void Submit() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        JSONObject sendJo=new JSONObject();
        try {
            sendJo.put("idLesson",lesson.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<multiChoiceArrayList.size();i++)
        {
            JSONObject jo=new JSONObject();
            try {
                jo.put("_id",multiChoiceArrayList.get(i).getId());
                jo.put("answer",multiChoiceArrayList.get(i).getUserAnswer());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jo);

        }
        try {
            sendJo.put("test",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), sendJo.toString());
        iMyService. submitTest(body,sharedPreferences.getString("token",null)).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {

                        temp=response;
                        flag=true;


                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(DoingTestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            TextView tv=findViewById(R.id.temp);
                            tv.setText(temp);

                        }
                        else
                            Toast.makeText(DoingTestActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

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
                final Intent data = new Intent();


                data.putExtra(EXTRA_DATA, "success");
                data.putExtra("lesson",lesson);

                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }
}
