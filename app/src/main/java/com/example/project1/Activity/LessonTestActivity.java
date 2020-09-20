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
import android.widget.Button;
import android.widget.Toast;

import com.example.project1.Adapter.OwnMultiChoiceAdapter;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.MultiChoice;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class LessonTestActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button addQuestionBtn, updateBtn;
    RecyclerView recyclerView;
    OwnMultiChoiceAdapter ownMultiChoiceAdapter;
    Lesson lesson;


    boolean flag=false, flagAdd=false;
    SharedPreferences sharedPreferences;

    ArrayList<MultiChoice> multiChoiceArrayList=new ArrayList<>();
    ArrayList<MultiChoice> sendMultiChoiceArrayList=new ArrayList<>();
    ArrayList<File> files=new ArrayList<>();
    ArrayList<String> imageNames=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_test);
        AnhXa();
        ActionToolBar();

        lesson= (Lesson) getIntent().getSerializableExtra("lesson");
        multiChoiceArrayList=lesson.getQuizTest();
        ownMultiChoiceAdapter=new OwnMultiChoiceAdapter(multiChoiceArrayList,this,lesson.getID());

        ownMultiChoiceAdapter.setHasStableIds(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(ownMultiChoiceAdapter);
        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LessonTestActivity.this,AddTestQuizActivity.class);
                startActivityForResult(intent,1);
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiChoiceArrayList.size()>0)
                 Update();

                else Toasty.error(LessonTestActivity.this,"Không có gì để cập nhật").show();

            }
        });

    }

    private void Update() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();


        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<multiChoiceArrayList.size();i++)
        {
            JSONObject jo=new JSONObject();
            try {
                jo.put("A",multiChoiceArrayList.get(i).getA());
                jo.put("B",multiChoiceArrayList.get(i).getB());
                jo.put("C",multiChoiceArrayList.get(i).getC());
                jo.put("D",multiChoiceArrayList.get(i).getD());
                jo.put("answer",multiChoiceArrayList.get(i).getAnswer());
                jo.put("question",multiChoiceArrayList.get(i).getQuestion());
               // Toast.makeText(this,multiChoiceArrayList.get(i).getImage() , Toast.LENGTH_SHORT).show();
                if(multiChoiceArrayList.get(i).getImage().isEmpty()==false)
                    jo.put("image",multiChoiceArrayList.get(i).getImage());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jo);

        }

        JSONObject sendJo=new JSONObject();
        try {
            sendJo.put("multipleChoices",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(MediaType.parse("application/json"), sendJo.toString());

        iMyService. updateMultipleChoice("http://149.28.24.98:9000/lesson/add-list-multiple-choice/"+lesson.getID(),body,sharedPreferences.getString("token",null)).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {


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
                        Toast.makeText(LessonTestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toasty.success(LessonTestActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();
                            data.putExtra(EXTRA_DATA, "success");
                            data.putExtra("lesson",lesson);

                            setResult(Activity.RESULT_OK, data);
                            finish();

                        }
                        else
                            Toast.makeText(LessonTestActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    String temp="";

    boolean imgFlag=false;
    private void UploadImage() {

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
       // Toast.makeText(this, ""+files.size(), Toast.LENGTH_SHORT).show();
        for( int i=0;i<sendMultiChoiceArrayList.size();i++)
        {
            if(sendMultiChoiceArrayList.get(i).getFile()!=null){
                RequestBody fileReqBody =
                        RequestBody.create(
                                MediaType.parse("image/jpg"),
                                sendMultiChoiceArrayList.get(i).getFile()
                        );
                MultipartBody.Part part = MultipartBody.Part.createFormData("image", sendMultiChoiceArrayList.get(i).getFile().getName(), fileReqBody);
                // RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");


                iMyService.popUpImage(part,sharedPreferences.getString("token",null)).
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
                                imgFlag=true;

                            }

                            @Override
                            public void onError(Throwable e) {

                                Toast.makeText(LessonTestActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onComplete() {


                                if(imgFlag==true)
                                {
                                    try {
                                        JSONObject jsonObject=new JSONObject(temp);
                                        imageNames.add(jsonObject.getString("image"));
                                        // sendMultiChoiceArrayList.get(i).setImage(jsonObject.getString("image"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    flag=false;

                                }
                                else
                                    Toast.makeText(LessonTestActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                            }
                        });}

        }




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK)
        {
            multiChoiceArrayList.add((MultiChoice) data.getSerializableExtra("question"));
            sendMultiChoiceArrayList.add((MultiChoice) data.getSerializableExtra("question"));
                 files.add((File) data.getSerializableExtra("file"));
            ownMultiChoiceAdapter.notifyDataSetChanged();
            flagAdd=true;
        }
    }

    private void AnhXa() {
        toolbar=findViewById(R.id.lessonTestTB);
        addQuestionBtn=findViewById(R.id.addAChoiceForTest);
        updateBtn=findViewById(R.id.updateTest);
        recyclerView=findViewById(R.id.lessonTestRecycleView);
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
        });}
}
