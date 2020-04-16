package com.example.project1.Activity;

import androidx.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Model.UserAccount;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateCourseActivity extends AppCompatActivity {

    EditText courseName,courseGoal,courseDescription,coursePrice,courseDiscount;
    ImageView courseImage;
    Button pickImageBtn,createCourseBtn;
    Toolbar toolbar;
    Bitmap bitmap;
    File file;
    Spinner spinner;
    String name,goal,description,price,discount;
    IMyService iMyService;
    AlertDialog alertDialog;
    boolean flag=false,flag2=false;
    String sendID;
    String sendCategoryName;
    ArrayList<String> categoryName;
    ArrayList<String> categoryID;
    SharedPreferences sharedPreferences;
    UserAccount userAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        AnhXa();
        ActionToolBar();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        categoryID=getIntent().getExtras().getStringArrayList("list2");
        categoryName=getIntent().getExtras().getStringArrayList("list1");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userAccount= new UserAccount();

        userAccount.setToken(sharedPreferences.getString("token","default"));


    sendID=categoryID.get(0);
    sendCategoryName=categoryName.get(0);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(CreateCourseActivity.this,android.R.layout.simple_spinner_item,categoryName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(17);
                sendID=categoryID.get(position);
                sendCategoryName=categoryName.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        pickImageBtn.setOnClickListener(new View.OnClickListener() {
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
        createCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(KiemTra()) createCourse();
            }
        });
    }

    private void createCourse() {
        RequestBody fileReqBody =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
       // RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        alertDialog.show();
        iMyService.createCourse(name,goal,description,sendID,price,discount,part,userAccount.getToken()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {






                            String temp=response;

                            //JSONObject jsonObject=new JSONObject(temp);

                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            Toast.makeText(CreateCourseActivity.this, jsonObject.getJSONObject("newCourse").getString("discount"), Toast.LENGTH_SHORT).show();
                            flag=true;
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
                        Toast.makeText(CreateCourseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(CreateCourseActivity.this, "Tạo thành công", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(CreateCourseActivity.this,HomeActivity.class);
                            startActivity(intent);
                            CustomIntent.customType(CreateCourseActivity.this, "left-to-right");
                        }
                        else
                            Toast.makeText(CreateCourseActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });


    }


    private boolean KiemTra() {
        boolean valid=true;
        name=courseName.getText().toString();
        goal=courseGoal.getText().toString();
        description=courseDescription.getText().toString();
        price=coursePrice.getText().toString();
        discount=courseDiscount.getText().toString();
        if(name.isEmpty()||goal.isEmpty()||description.isEmpty()||price.isEmpty()||discount.isEmpty()||flag2==false)
        {
            valid=false;
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin khóa học", Toast.LENGTH_SHORT).show();
        }
        if(isNumeric(price)==false||isNumeric(discount)==false)
        {
            valid=false;
            Toast.makeText(this, "Vui lòng nhập giá và giảm giá của khóa học bằng số", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:{


                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission was granted
                    pickImageFromGallery();
                }
                else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
            case 100:{
                //  Toast.makeText(this, "asd: "+ PackageManager.PERMISSION_GRANTED, Toast.LENGTH_SHORT).show();
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1001);
                }
                else
                {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1000){
            //set image to image view

            Uri path=data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                courseImage.setImageBitmap(bitmap);
                file = new File(getRealPathFromURI(path));
                flag2=true;
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

    private void AnhXa() {
        courseName=findViewById(R.id.courseName);
        courseGoal=findViewById(R.id.courseGoal);
        courseDescription=findViewById(R.id.courseDescription);
        coursePrice=findViewById(R.id.coursePrice);
        courseDiscount=findViewById(R.id.courseDiscount);
        courseImage=findViewById(R.id.courseImage);
        pickImageBtn=findViewById(R.id.chooseCourseImage);
        createCourseBtn=findViewById(R.id.createCourse);
        toolbar=findViewById(R.id.createCourseTB);
        spinner=findViewById(R.id.categories_spinner);
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
