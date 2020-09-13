package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Adapter.fileItem;
import com.example.project1.MainActivity;
import com.example.project1.Model.FileUtils;
import com.example.project1.Model.courseItem;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
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

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class CreateLesson extends AppCompatActivity {
    EditText lessonName, lessonQuiz,lessonOrder;
    Button pickPDF, pickDoc, pickVideo, createLesson;
    RecyclerView recyclerView;
    List<String> fileNameList;
    List<String> fileDoneList;
    fileItem fileItem;
    Toolbar toolbar;
    Uri videoUri;
    String temmp;
    LinearLayout linearLayout;
    TextView videoName;
    String name, quiz, order;
    Boolean flagFile=false, flagVideo=false,updateFlag=false;
    IMyService iMyService;
    AlertDialog alertDialog;
    List<MultipartBody.Part> parts = new ArrayList<>();
    File videoFile;
    SharedPreferences sharedPreferences;
    courseItem courseItem;String temp="";
    String fileString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);
        AnhXa();
        ActionBar();
        MultipartBody.Part fileToUpload1;
         courseItem = (com.example.project1.Model.courseItem) getIntent().getSerializableExtra("course");
        fileDoneList=new ArrayList<>();
        fileNameList=new ArrayList<>();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        iMyService=retrofitClient.create(IMyService.class);
        fileItem=new fileItem(fileNameList,fileDoneList,CreateLesson.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(fileItem);

        linearLayout.setVisibility(View.GONE);
        pickPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("application/pdf");

                startActivityForResult(Intent.createChooser(intent,"Chọn file pdf"),1);
            }
        });
        pickDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(intent,"Chọn file doc"),2);
            }
        });
        pickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent,"Select Video"),3);
            }
        });
        createLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(KiemTra()) CreateNewLesson();
            }
        });
    }

    private boolean KiemTra() {
        boolean valid=false;
        name=lessonName.getText().toString();
      //  quiz=lessonQuiz.getText().toString();
        order=lessonOrder.getText().toString();
        if(name.isEmpty()||order.isEmpty())
        {
            valid=false;
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        else valid=true;
//        if(flagVideo==false||flagFile==false)
//        {
//            valid=false;
//            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
//        }
//        else valid=true;
        return valid;
    }
    boolean flag1=false;
    private void CreateNewLesson() {
       RequestBody fileReqBody =
               RequestBody.create(
                     MediaType.parse("multipart/form-data"),
                      videoFile
              );

        MultipartBody.Part part = MultipartBody.Part.createFormData("videos", videoFile.getName(), fileReqBody);


        alertDialog.show();
        iMyService.createLesson(name,order,quiz,courseItem.getID(),part,parts,sharedPreferences.getString("token",null)).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {


                        temmp=response;
                        flag1=true;


                        }





                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(CreateLesson.this,e.toString(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if( flag1==true)
                        {
                            Toasty.success(CreateLesson.this, "Tạo thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();


                            data.putExtra(EXTRA_DATA, "success");

                            setResult(Activity.RESULT_OK, data);


                            finish();
                        }
                        else
                            Toast.makeText(CreateLesson.this, "Đã có lỗi xảy ra: "+ temmp, Toast.LENGTH_SHORT).show();
                       // lessonQuiz.setText(temmp);

                    }
                });
       // Toast.makeText(this, "size: "+ parts.size(), Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onBackPressed() {


        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
    private void ActionBar() {
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK&&requestCode==1) {
            flagFile=true;
          if(data.getClipData()!=null){
            int total=data.getClipData().getItemCount();
            for(int i=0;i<total;i++)
            {
                if(fileNameList.size()==5)
                {
                    Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();
                    break;
                }

                Uri uri=data.getClipData().getItemAt(i).getUri();
                fileNameList.add(getFileName(uri));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    fileString = getFilePathFromURI(this,uri);
                }
                File file=new File(fileString);
                RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)), file);
                RequestBody PDFreq = RequestBody.create(MediaType.parse("application/pdf"),file.getName());
                MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),requestBody);
                parts.add(part);
                fileItem.notifyDataSetChanged();
            }
          }
          else{
              if(fileNameList.size()>=5)
              {
                  Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();

              }else{
                  Uri uri=data.getData();
                  fileNameList.add(getFileName(uri));

                  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                      fileString = getFilePathFromURI(this,uri);
                  }
                  File file=new File(fileString);
                  RequestBody PDFreq = RequestBody.create(MediaType.parse("application/pdf"),file.getName());
                  MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),PDFreq);
                  parts.add(part);
                  fileItem.notifyDataSetChanged();}


          }
        }
        if (resultCode == RESULT_OK&&requestCode==2) {
            flagFile=true;
            if(data.getClipData()!=null){
                int total=data.getClipData().getItemCount();
                for(int i=0;i<total;i++)
                {
                    if(fileNameList.size()==5)
                    {
                        Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Uri uri=data.getClipData().getItemAt(i).getUri();
                    fileNameList.add(getFileName(uri));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        fileString = getFilePathFromURI(this,uri);
                    }
                    File file=new File(fileString);
                    RequestBody PDFreq = RequestBody.create(MediaType.parse("multipart/form-data"),file.getName());
                    MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),PDFreq);
                    parts.add(part);
                    fileItem.notifyDataSetChanged();}
                }

            else{
                if(fileNameList.size()>=5)
                {
                    Toast.makeText(this, "Tối đa 5 file", Toast.LENGTH_SHORT).show();

                }else{
                Uri uri=data.getData();
                    fileNameList.add(getFileName(uri));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        fileString = getFilePathFromURI(this,uri);
                    }
                    File file=new File(fileString);
                    RequestBody PDFreq = RequestBody.create(MediaType.parse("multipart/form-data"),file.getName());
                    MultipartBody.Part part = MultipartBody.Part.createFormData("docs",file.getName(),PDFreq);
                    parts.add(part);
                    fileItem.notifyDataSetChanged();}
            }
        }

        if (resultCode == RESULT_OK&&requestCode==3) {
            flagVideo=true;
            Uri uri=data.getData();
            videoUri=uri;
            String fileName=getFileName(uri);
            videoName.setText(fileName);
            linearLayout.setVisibility(View.VISIBLE);
            videoFile=new File(getRealPathFromURI(uri));
        }

        if(parts.size()!=0) recyclerView.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);

    }
    private String getRealPathFromURI(Uri path) {
        Cursor cursor = getContentResolver().query(path, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[1024 * 2];

        BufferedInputStream in = new BufferedInputStream(input, 1024 * 2);
        BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 2);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, 1024 * 2)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }


    private void AnhXa() {
        lessonName=findViewById(R.id.lessonName);
      //  lessonQuiz=findViewById(R.id.lessonQuiz);
        pickPDF=findViewById(R.id.chooseFilePDF);
        pickDoc=findViewById(R.id.chooseFileDoc);
        pickVideo=findViewById(R.id.chooseVideo);
        createLesson=findViewById(R.id.createLesson);
        recyclerView=findViewById(R.id.fileList);
        toolbar=findViewById(R.id.createLessonToolbar);
        linearLayout=findViewById(R.id.choosenVideo);
        videoName=findViewById(R.id.vidName);
        lessonOrder=findViewById(R.id.lessonOrder);
        
    }
}
