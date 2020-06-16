package com.example.project1.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.project1.Model.MultiChoice;
import com.example.project1.R;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class CreateAMultiChoice extends AppCompatActivity {

    Toolbar toolbar;
    Button doneBtn;
    EditText question, ansA, ansB, ansC, ansD, answer;
    EditText timeShowMins, timeShowSecond;
    Button pickImage;
    ImageView imageReview;
    Bitmap bitmap;
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
                int timeresult;
                String m=timeShowMins.getText().toString();
                String s=timeShowSecond.getText().toString();
                int minutes = Integer.parseInt(m);
                int seconds = Integer.parseInt(s);
                timeresult=minutes*60+seconds;
                if(flagPick==false) file=null;
                MultiChoice multiChoice=new MultiChoice("null",ansA.getText().toString(),ansB.getText().toString(),ansC.getText().toString(),ansD.getText().toString(),
                        answer.getText().toString(),question.getText().toString(),null,""+timeresult,file);
                final Intent data = new Intent();
                data.putExtra(EXTRA_DATA, "success");
                data.putExtra("question",multiChoice);

               // data.putExtra("file",file);
                setResult(Activity.RESULT_OK, data);
                finish();
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
        if (resultCode == RESULT_OK && requestCode == 1000){
            //set image to image view

            Uri path=data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageReview.setImageBitmap(bitmap);
                imageReview.setVisibility(View.VISIBLE);
                file = new File(getRealPathFromURI(path));
                flagPick=true;


            } catch (IOException e) {
                e.printStackTrace();
            }



        }

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
