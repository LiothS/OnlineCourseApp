package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Model.courseItem;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.view.View.GONE;

public class CourseDetail extends AppCompatActivity {

    ImageView courseImage;
    TextView courseName,courseGoal,courseEnroll,courseRank,courseAuthor
            ,courseUpdateTime,coursePrice,courseOldPrice,courseJoinBtn
            ,videoCount,fileCount, quizCount,courseDescription
            ,totalVote;
    RatingBar ratingBar;
    Button addToCart, writeComment;
    courseItem courseItem;
    IMyService iMyService;
    AlertDialog alertDialog;
    boolean flag=false;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        AnhXa();
        courseItem= (com.example.project1.Model.courseItem) getIntent().getSerializableExtra("course");
        setUp();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        courseJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinCourse();
            }
        });
    }

    private void setUp() {
        Picasso.get().load(courseItem.getUrl()).placeholder(R.drawable.empty).error(R.drawable.empty).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(courseImage);
        courseName.setText(courseItem.getTitle());
        courseGoal.setText(courseItem.getGoal());
        courseRank.setText("Xếp hạng "+courseItem.getRanking());
        courseAuthor.setText(courseItem.getAuthor());
        courseUpdateTime.setText("Cập nhật lúc "+courseItem.getUpdateTime());
        NumberFormat formatter = new DecimalFormat("#,###");
        double price=(double)courseItem.getPrice();
        double discount=(double)courseItem.getDiscount();
        if(price==0) {
            coursePrice.setText("Miễn phí");
            courseJoinBtn.setText("Tham gia ngay");
            courseOldPrice.setVisibility(GONE);
        }else if(price!=0&&discount==0)
        {
            String formattedNumber1 = formatter.format(price);
            coursePrice.setText(formattedNumber1);
            courseOldPrice.setVisibility(GONE);
        }else if(price!=0&&discount!=0)
        {
            String oldPrice = formatter.format(price);
            price=price-(price*discount)/100;
            String newprice = formatter.format(price);
            coursePrice.setText(newprice+ "đ");
            courseOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            courseOldPrice.setText(oldPrice+"");
        }
        courseDescription.setText(courseItem.getDesription());
        totalVote.setText("("+(int)courseItem.getTotalVote()+")");
        ratingBar.setRating(courseItem.getRating());

    }

    private void joinCourse() {
        alertDialog.show();
        iMyService.joinCourse(sharedPreferences.getString("id",null),courseItem.getID()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(Response<String> response) {

                        if(response.isSuccessful()){
                        Toast.makeText(CourseDetail.this, response.body(), Toast.LENGTH_SHORT).show();


                        //JSONObject jsonObject=new JSONObject(temp);

                       flag=true;}



                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(CourseDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(CourseDetail.this, "Tham gia thành công", Toast.LENGTH_SHORT).show();
                            flag=false;

                        }
                        else
                            Toast.makeText(CourseDetail.this, "Bạn đã tham gia khóa hoc này rồi", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void AnhXa() {
        courseImage=findViewById(R.id.courseDetailImage);
        courseName=findViewById(R.id.courseDetailName);
        courseGoal=findViewById(R.id.courseDetailGoal);
        courseEnroll=findViewById(R.id.courseEnroll);
        courseRank=findViewById(R.id.coursRank);
        courseAuthor=findViewById(R.id.courseAuthor);
        courseUpdateTime=findViewById(R.id.updateTime);
        coursePrice=findViewById(R.id.ccoursePrice);
        courseOldPrice=findViewById(R.id.courseOldPrice);
        courseJoinBtn=findViewById(R.id.joinCourse);
        videoCount=findViewById(R.id.videoNumber);
        fileCount=findViewById(R.id.fileNumber);
        quizCount=findViewById(R.id.quizNumber);
        courseDescription=findViewById(R.id.authorsDescription);
        totalVote=findViewById(R.id.totalVoteRating);
        addToCart=findViewById(R.id.addToCart);
        writeComment=findViewById(R.id.writeComment);
        ratingBar=findViewById(R.id.courseDetailRating);

    }
}
