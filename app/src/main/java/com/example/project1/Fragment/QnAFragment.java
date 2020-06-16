package com.example.project1.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.Activity.CommentDetailActivity;
import com.example.project1.Activity.LessonDetailActivity;
import com.example.project1.Adapter.CommentAdapter;
import com.example.project1.Adapter.ParentCommentAdapter;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.UserComment;
import com.example.project1.Model.courseItem;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

import static android.app.Activity.RESULT_OK;

public class QnAFragment extends Fragment {
    Lesson lesson;
    public QnAFragment(Lesson lesson) {
        this.lesson=lesson;
    }
    View view;
    Button btn;
    RecyclerView recyclerView;
    ParentCommentAdapter parentCommentAdapter;
    ArrayList<UserComment> items=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        view=inflater.inflate(R.layout.qna_fragment,container,false);
        btn=view.findViewById(R.id.createComment);
        recyclerView=view.findViewById(R.id.lessonComment);
       parentCommentAdapter=new ParentCommentAdapter(items,getContext(),lesson);
        parentCommentAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(parentCommentAdapter);
        LoadComment();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), lesson.getIdcourse(), Toast.LENGTH_SHORT).show();
                ShowDialog();
            }
        });
        return view;

    }
String temp="";
    private void LoadComment() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getListComment("http://52.152.163.79:9000/comment/get-parent-comment-by-lesson/"+lesson.getIdcourse()+"/"+lesson.getID()+"/0/0").
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
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


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
                                   items.add(new UserComment(jsonObject.getString("_id"), img, name, jsonObject.getString("content"), jsonObject.getJSONArray("childComment"),
                                           jsonObject.getString("image"),jsonObject.getString("idParent")));
                                   parentCommentAdapter.notifyDataSetChanged();
                              }
                          } catch (JSONException e) {
                              e.printStackTrace();
                              Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                          }
                      }

                    }
                });
    }

    Button doneBtn, pickImgBtn;
    EditText editText;
    ImageView img;
    private void ShowDialog() {
        AlertDialog.Builder  mbuilder=new AlertDialog.Builder(getContext());
        View view=getLayoutInflater().inflate(R.layout.create_a_comment,null);
        doneBtn=view.findViewById(R.id.addComment);
        pickImgBtn=view.findViewById(R.id.pickCommentImage);
        editText=view.findViewById(R.id.newCommentContent);
        img=view.findViewById(R.id.newCommentImage);
        img.setVisibility(View.GONE);
        mbuilder.setView(view);
        AlertDialog alertDialog=mbuilder.create();
        pickImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddComment();
                alertDialog.dismiss();
                flag=false;
            }
        });

        alertDialog.show();

    }
    String cmtTemp="";
    boolean checkflag=false;
    private void AddComment() {
        IMyService iMyService;
        AlertDialog alertDialog;
        MultipartBody.Part  part=null;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(flag==true){
        RequestBody fileReqBody =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        file
                );
        part= MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
        }

        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.addComment(part,null,lesson.getIdcourse(),editText.getText().toString(),sharedPreferences.getString("id",null),lesson.getID()).
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
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            items.clear();
                            cmtTemp="";
                            temp="";
                            LoadComment();


                        }
                        else
                            Toast.makeText(getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    Bitmap bitmap;
    File file;
    boolean flag=false;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1000){
            //set image to image view

            Uri path=data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
               img.setImageBitmap(bitmap);
                file = new File(getRealPathFromURI(path));
                img.setVisibility(View.VISIBLE);
                flag=true;
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }
    private String getRealPathFromURI(Uri path) {
        Cursor cursor = getActivity().getContentResolver().query(path, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
