package com.example.project1.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.project1.Adapter.ViewPagerAdapter;
import com.example.project1.Fragment.QnAFragment;
import com.example.project1.Fragment.documentsFragment;
import com.example.project1.Fragment.quizFragment;
import com.example.project1.Model.Lesson;
import com.example.project1.Model.MultiChoice;
import com.example.project1.R;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LessonDetailActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    Lesson lesson;
    TabLayout tabLayout;
    ViewPager viewPager;
    long count=0;
    TextView question, ansA, ansB, ansC, ansD;
    ImageView imgView;
    ArrayList<MultiChoice> multiChoiceArrayList=new ArrayList<>();
    Handler handler=new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        lesson= (Lesson) getIntent().getSerializableExtra("lesson");
        multiChoiceArrayList=lesson.getMultiChoice();
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.AddFragment(new documentsFragment(lesson),"Tài liệu");
       // viewPagerAdapter.AddFragment(new quizFragment(),"Bài tập");
        viewPagerAdapter.AddFragment(new QnAFragment(lesson),"Thảo luận");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        playerView=findViewById(R.id.exoVideoView);
        videoInit();
        if(multiChoiceArrayList.size()>0)
        trackVideo.run();

    }
    private void pausePlayer(){
       simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
    }
    private void startPlayer(){
        simpleExoPlayer.setPlayWhenReady(true);
       simpleExoPlayer.getPlaybackState();
    }
    private void ShowQuestionDialog(){
        AlertDialog.Builder  mbuilder=new AlertDialog.Builder(LessonDetailActivity.this);
        View view=getLayoutInflater().inflate(R.layout.question_dialog,null);
        question=view.findViewById(R.id.popUpQuestion);
        ansA=view.findViewById(R.id.ansA);
        ansB=view.findViewById(R.id.ansB);
        ansC=view.findViewById(R.id.ansC);
        ansD=view.findViewById(R.id.ansD);

        imgView=view.findViewById(R.id.questionImage);
    mbuilder.setView(view);
        AlertDialog alertDialog=mbuilder.create();
        ansA.setText(multiChoiceArrayList.get(index).getA());
        ansB.setText(multiChoiceArrayList.get(index).getB());
        ansC.setText(multiChoiceArrayList.get(index).getC());
        ansD.setText(multiChoiceArrayList.get(index).getD());
        question.setText(multiChoiceArrayList.get(index).getQuestion());
        Toast.makeText(this,multiChoiceArrayList.get(index).getImage() , Toast.LENGTH_SHORT).show();
        if(multiChoiceArrayList.get(index).getImage().contains("."))
        {
            Picasso.get().load("http://52.152.163.79:9000/upload/lesson/"+multiChoiceArrayList.get(index).getImage()).placeholder(R.drawable.empty2).error(R.drawable.empty2).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgView);
            imgView.setVisibility(View.VISIBLE);
        }
        ansA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiChoiceArrayList.get(index).getAnswer().equals("A"))
                {
                    alertDialog.dismiss();
                    index++;
                    doingQuiz=false;
                    startPlayer();
                }
                else{
                    Toast.makeText(LessonDetailActivity.this, "Chưa chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ansB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiChoiceArrayList.get(index).getAnswer().equals("B"))
                {
                    alertDialog.dismiss();
                    index++;
                    doingQuiz=false;
                    startPlayer();
                }
                else{
                    Toast.makeText(LessonDetailActivity.this, "Chưa chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ansC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiChoiceArrayList.get(index).getAnswer().equals("C"))
                {
                    alertDialog.dismiss();
                    index++;
                    doingQuiz=false;

                    startPlayer();
                }
                else{
                    Toast.makeText(LessonDetailActivity.this, "Chưa chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ansD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiChoiceArrayList.get(index).getAnswer().equals("D"))
                {
                    alertDialog.dismiss();
                    index++;
                    doingQuiz=false;
                    startPlayer();
                }
                else{
                    Toast.makeText(LessonDetailActivity.this, "Chưa chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });

       alertDialog.show();


    }
    private MediaSource buildMediaSource(final Uri uri) {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        HttpDataSource.BaseFactory myDSFactory = new HttpDataSource.BaseFactory() {
            @Override
            protected HttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties defaultRequestProperties) {
                byte[] toEncrypt = sharedPreferences.getString("token",null).getBytes();
                String encoded =  Base64.encodeToString(toEncrypt, Base64.DEFAULT).trim();
                DefaultHttpDataSourceFactory dsf = new DefaultHttpDataSourceFactory("exoplayer-codelab");
                HttpDataSource ds = dsf.createDataSource();
                ds.setRequestProperty("auth-token", encoded);
                return ds;
            }
        };
        ExtractorMediaSource.Factory emf = new ExtractorMediaSource.Factory(myDSFactory);
        return emf.createMediaSource(uri);
    }
    boolean doingQuiz=false;
    private void videoInit() {
       // Toast.makeText(this, lesson.getVideo(), Toast.LENGTH_SHORT).show();
        simpleExoPlayer= ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(simpleExoPlayer);
        DataSource.Factory datasource=new DefaultDataSourceFactory(this, Util.getUserAgent(this,"lesson_video"));
        MediaSource videoSource=new ExtractorMediaSource.Factory(datasource).createMediaSource(Uri.parse("http://52.152.163.79:9000/upload/lesson/"+lesson.getVideo()));

        simpleExoPlayer.prepare(videoSource);
        simpleExoPlayer.setPlayWhenReady(true);

    }
    int index=0;
    private Runnable trackVideo= new Runnable() {
        @Override
        public void run() {
           // Toast.makeText(LessonDetailActivity.this, simpleExoPlayer.getCurrentPosition()+"", Toast.LENGTH_SHORT).show();
            int result=-999;
            if(index<multiChoiceArrayList.size()){
               result =(int)simpleExoPlayer.getCurrentPosition()/1000-Integer.parseInt(multiChoiceArrayList.get(index).getTimeShow());
            }

             if(result>0&&doingQuiz==false) {

                pausePlayer();
                 //Toast.makeText(LessonDetailActivity.this, "Dùng", Toast.LENGTH_SHORT).show();
                 doingQuiz=true;
                ShowQuestionDialog();

             }

            handler.postDelayed(this,1000);
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
        handler.removeCallbacks(trackVideo);
    }
}
