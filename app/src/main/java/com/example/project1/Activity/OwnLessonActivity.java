package com.example.project1.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import retrofit2.Retrofit;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class OwnLessonActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText lessonTitle,lessonOrder;
   Button updateBtn,multiChoiceBtn, videoBtn, deleteBtn, fileBtn,quizBtn;
    Lesson lesson;
    boolean flag=false;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_own_lesson);
        AnhXa();
        ActionBar();
        lesson= (Lesson) getIntent().getSerializableExtra("lesson");
        lessonTitle.setText(lesson.getTitle());
        lessonOrder.setText(lesson.getOrder());
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KiemTra()) Update();
            }
        });
        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OwnLessonActivity.this,LessonFileActivity.class);
                intent.putExtra("lesson",lesson);
                startActivityForResult(intent,3000);
            }
        });
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OwnLessonActivity.this,LessonVideoActivity.class);
                intent.putExtra("lesson",lesson);
                startActivityForResult(intent,3001);
            }
        });
        multiChoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OwnLessonActivity.this,LessonChoicesActivity.class);
                intent.putExtra("lesson",lesson);
                startActivityForResult(intent,3002);
            }
        });
        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OwnLessonActivity.this,LessonTestActivity.class);
                intent.putExtra("lesson",lesson);
                startActivityForResult(intent,3002);
            }
        });
        deleteBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3000&&resultCode==RESULT_OK)
        {
            lesson= (Lesson) data.getSerializableExtra("lesson");
        }
    }

    private boolean KiemTra() {
        boolean valid=false;
        if(lessonTitle.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Vui lòng nhập tên bài giảng", Toast.LENGTH_SHORT).show();
            return false;
        }else valid=true;
        if(lessonOrder.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Vui lòng nhập thứ tự", Toast.LENGTH_SHORT).show();
            return false;

        }else valid=true;
        if(isNumeric(lessonOrder.getText().toString())==false)
        {
            Toast.makeText(this, "Vui lòng nhập thứ tự là số", Toast.LENGTH_SHORT).show();
            return false;
        } else valid=true;
        return valid;


    }
    private void ActionBar() {
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

    private void Update() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.updateLessonInfo("http://13.68.245.234:9000/lesson/update-lesson/"+lesson.getID(),lesson.getIdcourse(),lessonOrder.getText().toString(), lessonTitle.getText().toString(),
                sharedPreferences.getString("token",null)).
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
                        Toast.makeText(OwnLessonActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(OwnLessonActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();


                            data.putExtra(EXTRA_DATA, "success");

                            setResult(Activity.RESULT_OK, data);


                            finish();

                        }
                        else
                            Toast.makeText(OwnLessonActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    @Override
    public void onBackPressed() {


        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    private void AnhXa() {
        toolbar=findViewById(R.id.ownLessonTB);
        lessonTitle=findViewById(R.id.lessonName);
        lessonOrder=findViewById(R.id.ownLessonOrder);
        updateBtn=findViewById(R.id.updateLesson);
        multiChoiceBtn=findViewById(R.id.multipleChoiceManage);
        videoBtn=findViewById(R.id.videoManage);
       deleteBtn=findViewById(R.id.deleteLesson);
        fileBtn=findViewById(R.id.fileManage);
        quizBtn=findViewById(R.id.quizManagement);
    }
}
