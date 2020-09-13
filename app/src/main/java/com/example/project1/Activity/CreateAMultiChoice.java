package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.project1.Model.MultiChoice;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

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

public class CreateAMultiChoice extends AppCompatActivity {

    Toolbar toolbar;
    Button doneBtn;
    EditText question, ansA, ansB, ansC, ansD, answer;
    EditText timeShowMins, timeShowSecond;
    Button pickImage;
    ImageView imageReview;
    Bitmap bitmap;
    MultiChoice multiChoice;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_amulti_choice);
        toolbar=findViewById(R.id.createAMultiChoiceTB);
        doneBtn=findViewById(R.id.doneBtn);
        question=findViewById(R.id.question);
        ansA=findViewById(R.id.AnswerA);
        ansB=findViewById(R.id.AnswerB);
        ansC=findViewById(R.id.AnswerC);
        ansD=findViewById(R.id.AnswerD);
        answer=findViewById(R.id.Answer);
        pickImage=findViewById(R.id.pickImageBtn);
        imageReview=findViewById(R.id.quizImage);
        timeShowMins=findViewById(R.id.timeShowMins);
        timeShowSecond=findViewById(R.id.timeShowSecond);
        ActionToolBar();
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, 1000);
                    }
                    else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else {
                    //system os is less then marshmallow
                    pickImageFromGallery();
                }
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(question.getText().toString().isEmpty()==false&&flagPick==true){

                final Intent data = new Intent();
                data.putExtra(EXTRA_DATA, "success");
                data.putExtra("question",multiChoice);
                if(flagPick)
                data.putExtra("file",file);
                setResult(Activity.RESULT_OK, data);
                finish();}
                else if(question.getText().toString().isEmpty()==false&&flagPick==false){
                    int timeresult;
                    String m=timeShowMins.getText().toString();
                    String s=timeShowSecond.getText().toString();
                    int minutes = Integer.parseInt(m);
                    int seconds = Integer.parseInt(s);
                    timeresult=minutes*60+seconds;
                    if(flagPick==false) file=null;
                    multiChoice =new MultiChoice("null",ansA.getText().toString(),ansB.getText().toString(),ansC.getText().toString(),ansD.getText().toString(),
                            answer.getText().toString(),question.getText().toString(),"",""+timeresult,file);
                    final Intent data = new Intent();
                    data.putExtra(EXTRA_DATA, "success");
                    data.putExtra("question",multiChoice);
                    if(flagPick)
                        data.putExtra("file",file);
                    setResult(Activity.RESULT_OK, data);
                    finish();

                }
                else Toasty.error(CreateAMultiChoice.this,"Không có gì để thêm").show();
            }
        });
    }
    boolean flagPick=false;
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1000) {
            //set image to image view

            Uri path = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageReview.setImageBitmap(bitmap);
                imageReview.setVisibility(View.VISIBLE);
                file = new File(getRealPathFromURI(path));
                flagPick = true;
                if(flagPick){

                    UploadImage();
                }else{
                    int timeresult;
                    String m=timeShowMins.getText().toString();
                    String s=timeShowSecond.getText().toString();
                    int minutes = Integer.parseInt(m);
                    int seconds = Integer.parseInt(s);
                    timeresult=minutes*60+seconds;
                    if(flagPick==false) file=null;
                    multiChoice =new MultiChoice("null",ansA.getText().toString(),ansB.getText().toString(),ansC.getText().toString(),ansD.getText().toString(),
                            answer.getText().toString(),question.getText().toString(),null,""+timeresult,file);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
    String temp="";
    String imgName="";
    Boolean imgFlag=false;
    private void UploadImage() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        if(file!=null){
            RequestBody fileReqBody =
                    RequestBody.create(
                            MediaType.parse("image/jpg"),
                            file
                    );
            MultipartBody.Part part = MultipartBody.Part.createFormData("image",file.getName(), fileReqBody);
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
                            alertDialog.dismiss();

                            Toast.makeText(CreateAMultiChoice.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onComplete() {
                            alertDialog.dismiss();

                            if(imgFlag==true)
                            {
                                try {
                                    int timeresult;
                                    String m=timeShowMins.getText().toString();
                                    String s=timeShowSecond.getText().toString();
                                    int minutes = Integer.parseInt(m);
                                    int seconds = Integer.parseInt(s);
                                    timeresult=minutes*60+seconds;
                                    JSONObject jsonObject=new JSONObject(temp);
                                    imgName=jsonObject.getString("image");
                                    // sendMultiChoiceArrayList.get(i).setImage(jsonObject.getString("image"));
                                    multiChoice=new MultiChoice("null",ansA.getText().toString(),ansB.getText().toString(),ansC.getText().toString(),ansD.getText().toString(),
                                            answer.getText().toString(),question.getText().toString(),imgName,""+timeresult,file);
                                    // Toast.makeText(AddTestQuizActivity.this, multiChoice.getQuestion(), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                            else
                                Toast.makeText(CreateAMultiChoice.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                        }
                    });}

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
