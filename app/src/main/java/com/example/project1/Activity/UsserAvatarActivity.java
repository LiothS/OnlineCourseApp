package com.example.project1.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.example.project1.Model.UserAccount;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UsserAvatarActivity extends AppCompatActivity {
    Toolbar userAvaTB;
    Button ChonAnhThuVien, ChupAnhMoi,updateAva;
    CircleImageView circleImageView;
    File file;
    Bitmap bitmap;
    UserAccount userAccount=new UserAccount();
    IMyService iMyService;
    boolean flag=false,flag2=false;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usser_avatar);
        AnhXa();
        ActionToolBar();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        userAccount= (UserAccount) getIntent().getSerializableExtra("userAcc");
        String avurl="https://udemy-online-courses.herokuapp.com/upload/user_image/";
        Picasso.get().load(avurl+userAccount.getAva()).placeholder(R.drawable.useravatar).error(R.drawable.useravatar).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
        ChonAnhThuVien.setOnClickListener(new View.OnClickListener() {
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
        ChupAnhMoi.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //show popup for runtime permission
                    requestPermissions(permissions, 100);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 100);
                }
            }
        });
        updateAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag2==true) changeAvatar();
            }
        });

    }
    public static String between(String start, String end, String input) {
        int startIndex = input.indexOf(start);
        int endIndex = input.lastIndexOf(end);
        if(startIndex == -1 || endIndex == -1) return input;
        else return input.substring(startIndex + start.length(), endIndex + end.length()).trim();
    }

    private void changeAvatar() {
        updateAva.setClickable(false);
        updateAva.setEnabled(false);
        RequestBody fileReqBody =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        alertDialog.show();
        iMyService.changeAva(part,userAccount.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> response) {


                        if(response.isSuccessful()){


                            if(response.message().equals("OK"))
                            {
                                String responeString=response.body().toString();
                                String start="\"image\"";
                                String end="\"gender\"";
                                String avaname=between(start,end,responeString);
                                String [] arr=avaname.split("\"");
                                 userAccount.setAva(arr[1]);
                                 flag=true;


                            }
                            else{
                                flag=false;
                            }}


                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(UsserAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        flag=false;

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
                        { Toast.makeText(UsserAvatarActivity.this, "Cập nhật ảnh thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();

                            data.putExtra("usernewAcc", userAccount);

                            setResult(Activity.RESULT_OK, data);

                            finish();
                        }
                        else
                            Toast.makeText(UsserAvatarActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();

                    }
                });
        updateAva.setEnabled(true);
        updateAva.setClickable(true);
    }


    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }

    private void ActionToolBar() {
        setSupportActionBar(userAvaTB);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userAvaTB.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userAvaTB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                circleImageView.setImageBitmap(bitmap);
                file = new File(getRealPathFromURI(path));
                flag2=true;
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
        else if(resultCode == RESULT_OK && requestCode == 1001)
        {



                bitmap= (Bitmap) data.getExtras().get("data");

            Uri path = getImageUri(UsserAvatarActivity.this, bitmap);
                circleImageView.setImageBitmap(bitmap);
                file = new File(getRealPathFromURI(path));
                flag2=true;

        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void AnhXa() {
        userAvaTB=findViewById(R.id.userAvaTB);
        circleImageView=findViewById(R.id.userAvatar);
        ChonAnhThuVien=findViewById(R.id.ThuVienAnh);
        ChupAnhMoi=findViewById(R.id.ChupAnhMoi);
        updateAva=findViewById(R.id.updateAva);
    }
}