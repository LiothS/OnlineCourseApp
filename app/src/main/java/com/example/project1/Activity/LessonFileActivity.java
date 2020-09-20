package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Adapter.LessonFileAdapter;
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
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import static android.nfc.NfcAdapter.EXTRA_DATA;
import static com.example.project1.Activity.CreateLesson.copy;

public class LessonFileActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    Lesson lesson;
    String fileString;
    LessonFileAdapter lessonFileAdapter;
    Button pickFilePDF, updateBtn,pickFileDocx;
    boolean flagfile=false, flag=false;
    SharedPreferences sharedPreferences;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_file);
        AnhXa();
        ActionToolBar();
        tv=findViewById(R.id.result);
        lesson= (Lesson) getIntent().getSerializableExtra("lesson");
        lessonFileAdapter=new LessonFileAdapter(lesson.getFile(),this,lesson.getID());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(lessonFileAdapter);
        lessonFileAdapter.notifyDataSetChanged();
        pickFilePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    intent.setType("application/pdf");

                    startActivityForResult(Intent.createChooser(intent, "Chọn file pdf"), 1);

            }
        });
        pickFileDocx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent();

                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(Intent.createChooser(intent, "Chọn file doc"), 2);

            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagfile==true) Update();
                else Toast.makeText(LessonFileActivity.this, "Không có gì để cập nhật", Toast.LENGTH_SHORT).show();

            }
        });
    }
    String temp="";
    private void Update() {
        File file=new File(fileString);
        //RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file);
        RequestBody req = RequestBody.create(MediaType.parse("multipart/form-data"),file.getName());
        MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),req);
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.addLessonFile("http://149.28.24.98:9000/lesson/add-docs/"+lesson.getID(),part,sharedPreferences.getString("token",null)).
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
                            temp=response;

                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(LessonFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            try {
                                JSONObject jsonObject=new JSONObject(temp);

                                JSONArray jsonArray=jsonObject.getJSONArray("doc");
                                String addFile=jsonArray.getString(jsonArray.length()-1);
                                lesson.getFile().add(addFile);
                                lessonFileAdapter.notifyDataSetChanged();
                                Toast.makeText(LessonFileActivity.this,"Thêm thành công" , Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        else
                            Toast.makeText(LessonFileActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK&&requestCode==1) {

            if(data.getClipData()!=null){
                int total=data.getClipData().getItemCount();
                for(int i=0;i<total;i++)
                {
                    if(lesson.getFile().size()==5)
                    {
                        Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    flagfile=true;
                    Uri uri=data.getClipData().getItemAt(i).getUri();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        fileString = getFilePathFromURI(this,uri);
                    }



                    Update();

                }
            }
            else{
                if(lesson.getFile().size()>=5)
                {
                    Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();

                }else{
                    Uri uri=data.getData();

                    flagfile=true;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        fileString = getFilePathFromURI(this,uri);
                    }
                    File file=new File(fileString);
                    RequestBody PDFreq = RequestBody.create(MediaType.parse("application/pdf"),file.getName());
                    MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),PDFreq);

                    Update();

                }


            }
        }
        if (resultCode == RESULT_OK&&requestCode==2) {

            if(data.getClipData()!=null){
                int total=data.getClipData().getItemCount();
                for(int i=0;i<total;i++)
                {
                    if(lesson.getFile().size()==5)
                    {
                        Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    flagfile=true;
                    Uri uri=data.getClipData().getItemAt(i).getUri();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        fileString = getFilePathFromURI(this,uri);
                    }
                    File file=new File(fileString);
                    RequestBody PDFreq = RequestBody.create(MediaType.parse("multipart/form-data"),file.getName());
                    MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),PDFreq);


                    Update();

                }
            }

            else{
                if(lesson.getFile().size()>=5)
                {
                    Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();

                }else{
                    flagfile=true;
                    Uri uri=data.getData();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        fileString = getFilePathFromURI(this,uri);
                    }
                    File file=new File(fileString);
                    RequestBody PDFreq = RequestBody.create(MediaType.parse("multipart/form-data"),file.getName());
                    MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),PDFreq);


                    Update();

                }
            }
        }




        super.onActivityResult(requestCode, resultCode, data);

    }

    String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/demonuts_upload_gallery");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(wallpaperDirectory + File.separator + fileName);
            // create folder if not exists

            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    private void AnhXa() {
        toolbar=findViewById(R.id.lessonFileTB);
        recyclerView=findViewById(R.id.fileRecycleView);
        pickFilePDF=findViewById(R.id.pickFilePDF);
        pickFileDocx=findViewById(R.id.pickFileDocx);
        updateBtn=findViewById(R.id.fileUpdate);
    }
}
