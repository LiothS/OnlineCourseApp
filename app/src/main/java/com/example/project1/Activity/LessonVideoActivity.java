package com.example.project1.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Model.Lesson;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import java.io.File;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class LessonVideoActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button pickVideo, deleteVideo, updateVideo;
    TextView videoName;
    CardView cardView;
    Lesson lesson;
    boolean flag=false, flagVideo=false;
    File videoFile;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_video);
        toolbar=findViewById(R.id.lessonVideoTB);
        pickVideo=findViewById(R.id.addVideo);
        deleteVideo=findViewById(R.id.deleteVideo);
        updateVideo=findViewById(R.id.videoUpdate);
        videoName=findViewById(R.id.lessonVideoName);
        cardView=findViewById(R.id.videoContain);
        ActionToolBar();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lesson= (Lesson) getIntent().getSerializableExtra("lesson");
        videoName.setText(lesson.getVideo());
        pickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent,"Select Video"),1);
            }
        });
        updateVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagVideo==false)
                    Toast.makeText(LessonVideoActivity.this, "Không có gì để cập nhật", Toast.LENGTH_SHORT).show();
                else Update();
            }
        });
        deleteVideo.setVisibility(View.GONE);

    }

    private void Update() {

        //RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file);
        RequestBody fileReqBody =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        videoFile
                );

        MultipartBody.Part part = MultipartBody.Part.createFormData("videos", videoFile.getName(), fileReqBody);
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.addLessonFile("http://13.68.245.234:9000/lesson/add-video/"+lesson.getID(),part,sharedPreferences.getString("token",null)).
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
                        Toast.makeText(LessonVideoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(LessonVideoActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();
                            data.putExtra(EXTRA_DATA, "success");
                            data.putExtra("lesson",lesson);

                            setResult(Activity.RESULT_OK, data);
                            finish();

                        }
                        else
                            Toast.makeText(LessonVideoActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK&&requestCode==1) {
            flagVideo=true;
            Uri uri=data.getData();

            String fileName=getFileName(uri);
            videoName.setText(fileName);

            videoFile=new File(getRealPathFromURI(uri));
        }
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private String getRealPathFromURI(Uri path) {
        Cursor cursor = getContentResolver().query(path, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
        });}
}
