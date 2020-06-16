package com.example.project1.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Adapter.CommentAdapter;
import com.example.project1.Adapter.OwnLessonAdapter;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.UserComment;
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

import de.hdodenhof.circleimageview.CircleImageView;
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

public class CommentDetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    ArrayList<UserComment> userComments;
    UserComment userComment;
    CircleImageView parentAvatar;
    ImageView contentImg;
    EditText writeCmt;
    TextView cmtUserName, cmtContent, replyBtn;
    LinearLayout previewPic;
    ImageView previewPicImgView,pickImage,postCommentBtn,backBtn;
    Bitmap bitmap;
    File file;
    Lesson lesson;
    boolean flag=false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //set image to image view
        if(data!=null){
        Uri path=data.getData();
        try {

            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
            previewPicImgView.setImageBitmap(bitmap);
            file = new File(getRealPathFromURI(path));
            previewPic.setVisibility(View.VISIBLE);
            flag=true;
        } catch (IOException e) {
            e.printStackTrace();
        }}

    }

    private String getRealPathFromURI(Uri path) {
        Cursor cursor = getContentResolver().query(path, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        recyclerView=findViewById(R.id.chilCommentRecyclerView);
        parentAvatar=findViewById(R.id.parentAvatar);
        contentImg=findViewById(R.id.parentImage);
        cmtUserName=findViewById(R.id.parentUserName);
        cmtContent=findViewById(R.id.parentCommentContent);
        replyBtn=findViewById(R.id.replyBtn);
        writeCmt=findViewById(R.id.writeComment);
        previewPic=findViewById(R.id.previewPictureLayout);
        previewPicImgView=findViewById(R.id.previewCommentPicture);
        pickImage=findViewById(R.id.pickCmtImage);
        postCommentBtn=findViewById(R.id.postComment);
        backBtn=findViewById(R.id.backBtn);
        previewPic.setVisibility(View.GONE);
        userComment= (UserComment) getIntent().getSerializableExtra("comment");
        lesson= (Lesson) getIntent().getSerializableExtra("lesson");
        if(userComment!=null){
        Picasso.get().load(userComment.getAvatar()).placeholder(R.drawable.empty2).error(R.drawable.empty2).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(parentAvatar);
        if(!userComment.getCmtImg().isEmpty())
        Picasso.get().load("http://52.152.163.79:9000/upload/comment_image"+userComment.getCmtImg()).placeholder(R.drawable.empty).error(R.drawable.empty).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(contentImg);
        else contentImg.setVisibility(View.GONE);
        cmtContent.setText(userComment.getContent());
        cmtUserName.setText(userComment.getUserName());}
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeCmt.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(writeCmt, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1000);

            }
        });
        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
      backBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
              CustomIntent.customType(CommentDetailActivity.this,"up-to-bottom");
          }
      });
        userComments=new ArrayList<>();
        commentAdapter=new CommentAdapter(userComments,CommentDetailActivity.this,lesson);
       commentAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(commentAdapter);
        LoadChildComment();




    }
    boolean checkflag=false;
    String cmtTemp="";

    private void postComment() {
        IMyService iMyService;
        AlertDialog alertDialog;
        MultipartBody.Part  part=null;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(flag==true){
            RequestBody fileReqBody =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"),
                            file
                    );
            part= MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
        }

        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.addComment(part,userComment.getID(),lesson.getIdcourse(),writeCmt.getText().toString(),sharedPreferences.getString("id",null),lesson.getID()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {

                        checkflag=true;
                        cmtTemp=response;



                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(CommentDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if(checkflag==true)
                        {
                            userComments.clear();

                            checkflag=false;
                            cmtTemp="";
                            temp="";
                            previewPic.setVisibility(View.GONE);
                            writeCmt.setText("");
                            writeCmt.setFocusable(false);
                            LoadChildComment();

                        }
                        else
                            Toast.makeText(CommentDetailActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    String temp="";

    private void LoadChildComment() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);



        alertDialog= new SpotsDialog.Builder().setContext(CommentDetailActivity.this).build();
        //alertDialog.show();
        iMyService.getListComment("http://52.152.163.79:9000/comment/get-child-comment-by-id-parent/"+userComment.getID()).
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



                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(CommentDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);


                        if(!temp.isEmpty()){
                            try {
                                JSONArray jsonArray=new JSONArray(temp);
                                for(int i =0;i<jsonArray.length();i++)
                                {
                                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                                    String name=jsonObject.getJSONObject("idUser").getString("name");
                                    String img="http://52.152.163.79:9000/upload/user_image/"+jsonObject.getJSONObject("idUser").getString("image");
                                    userComments.add(new UserComment(jsonObject.getString("_id"), img, name, jsonObject.getString("content"), jsonObject.getJSONArray("childComment"),
                                            jsonObject.getString("image"),jsonObject.getString("idParent")));
                                    commentAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(CommentDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }
}
