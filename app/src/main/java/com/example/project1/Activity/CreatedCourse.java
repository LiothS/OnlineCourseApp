package com.example.project1.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.project1.Model.courseItem;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class CreatedCourse extends AppCompatActivity {
    com.example.project1.Model.courseItem courseItem;
    EditText courseName,courseGoal,courseDescription,coursePrice,courseDiscount;
    ImageView courseImage;
    Button pickImage, courseUpdate, courseLesson, courseDelete;
    Toolbar toolbar;
    Bitmap bitmap;
    ArrayList<String> categoriesName=new ArrayList<String>();
    ArrayList<String> categoriesID=new ArrayList<String>();
    File file;
    Spinner spinner;
    String name,goal,description,price,discount;
    IMyService iMyService;
    AlertDialog alertDialog;
    boolean flag=false,flag2=false,flag3=false,flag4=false;
    String sendID;
    String sendCategoryName;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_course);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       courseItem= (com.example.project1.Model.courseItem) getIntent().getSerializableExtra("course");
       AnhXa();
       ActionToolBar();
       categoriesName.add(courseItem.getCategoryName());
       categoriesID.add(courseItem.getCategoryID());
       getAllCategory();
       courseName.setText(courseItem.getTitle());
       courseGoal.setText(courseItem.getGoal());
       courseDescription.setText(courseItem.getDesription());
       coursePrice.setText(""+(int)courseItem.getPrice());
       courseDiscount.setText(""+(int)courseItem.getDiscount());
        Picasso.get().load(courseItem.getUrl()).placeholder(R.drawable.empty).error(R.drawable.empty).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(courseImage);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(17);
                sendID=categoriesID.get(position);
                sendCategoryName=categoriesName.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        courseUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KiemTra())
                   if(flag2==true) update();
                   else updateWithoutImage();

            }
        });
        courseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog1= new AlertDialog.Builder(CreatedCourse.this)
                        .setTitle("Xóa khóa học")
                        .setMessage("Bạn có chắc muốn xóa khóa học này không ?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               deleteCourse();

                            }
                        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog1.show();
            }
        });
        courseLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreatedCourse.this,LessonManageActivity.class);
                intent.putExtra("course",courseItem);
                startActivity(intent);

            }
        });
    }

    private void deleteCourse() {
        alertDialog.show();
        iMyService.deleteCourse("http://52.152.163.79:9000/course/delete/"+courseItem.getID(),sharedPreferences.getString("token","")).
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



                            flag4=true;
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
                        Toast.makeText(CreatedCourse.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if(flag4==true)
                        {
                            Toast.makeText(CreatedCourse.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(CreatedCourse.this,HomeActivity.class);
                            startActivity(intent);
                            CustomIntent.customType(CreatedCourse.this, "left-to-right");
                        }
                        else
                            Toast.makeText(CreatedCourse.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateWithoutImage() {

        alertDialog.show();
        iMyService.courseUpdate1("http://52.152.163.79:9000/course/update/"+courseItem.getID(),name,goal,description,sendID,price,discount,sharedPreferences.getString("token","")).
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



                            flag3=true;
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
                        Toast.makeText(CreatedCourse.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if(flag3==true)
                        {
                            Toast.makeText(CreatedCourse.this, "Cập nhật thành công ", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(CreatedCourse.this,HomeActivity.class);
                            startActivity(intent);
                            CustomIntent.customType(CreatedCourse.this, "left-to-right");
                        }
                        else
                            Toast.makeText(CreatedCourse.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

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
        if(name.isEmpty()||goal.isEmpty()||description.isEmpty()||price.isEmpty()||discount.isEmpty())
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

    private void update() {
        MultipartBody.Part part=null;
        if(flag2==true){

        RequestBody fileReqBody =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );
        part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);}
        // RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        alertDialog.show();
        iMyService.courseUpdate("http://52.152.163.79:9000/course/update/"+courseItem.getID(),part,name,goal,description,sendID,price,discount,sharedPreferences.getString("token","")).
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
                           // Toast.makeText(CreatedCourse.this, jsonObject.getString("name"), Toast.LENGTH_SHORT).show();
                            flag3=true;
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
                        Toast.makeText(CreatedCourse.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if(flag3==true)
                        {
                            Toast.makeText(CreatedCourse.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(CreatedCourse.this,HomeActivity.class);
                            startActivity(intent);
                            CustomIntent.customType(CreatedCourse.this, "left-to-right");
                        }
                        else
                            Toast.makeText(CreatedCourse.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });
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
    private void getAllCategory(){
        alertDialog.show();
        iMyService.getAllCategory().
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
                           // Toast.makeText(CreatedCourse.this, "here", Toast.LENGTH_SHORT).show();
                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                String tempName=jo.getString("name");
                                String tempID=jo.getString("_id");
                                if(!categoriesName.get(0).equals(tempName)) {
                                    categoriesName.add(tempName);
                                    categoriesID.add(tempID);
                                }
                                flag=true;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CreatedCourse.this, e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CreatedCourse.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            sendID=categoriesID.get(0);
                            sendCategoryName=categoriesName.get(0);

                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(CreatedCourse.this,android.R.layout.simple_spinner_item,categoriesName);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                            spinner.setPrompt(courseItem.getCategoryName());

                        }
                        else
                            Toast.makeText(CreatedCourse.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        categoriesName.add(" ");
                        categoriesID.add(" ");
                        sendID=categoriesID.get(0);
                        sendCategoryName=categoriesName.get(0);
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(CreatedCourse.this,android.R.layout.simple_spinner_item,categoriesName);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                    }
                });


    }

    private void AnhXa() {
        courseName=findViewById(R.id.courseName);
        courseGoal=findViewById(R.id.courseGoal);
        courseDescription=findViewById(R.id.courseDescription);
        coursePrice=findViewById(R.id.coursePrice);
        courseDiscount=findViewById(R.id.courseDiscount);
        courseImage=findViewById(R.id.courseImage);
        pickImage=findViewById(R.id.chooseCourseImage);
        courseUpdate=findViewById(R.id.updateCourse);
        courseLesson=findViewById(R.id.lessonOfCourse);
        courseDelete=findViewById(R.id.deleteCourse);
        toolbar=findViewById(R.id.createdCourseTB);
        spinner=findViewById(R.id.categories_spinner2);
    }
}
