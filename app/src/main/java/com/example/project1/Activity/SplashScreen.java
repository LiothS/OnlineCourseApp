package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project1.R;

public class SplashScreen extends AppCompatActivity {

    Animation topAni,botAni;
    ImageView imgView;
    TextView name,slogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        topAni= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        botAni= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        imgView=findViewById(R.id.splash_img);
        name=findViewById(R.id.splash_name);
        slogan=findViewById(R.id.splash_slogan);
        imgView.setAnimation(topAni);
       name.setAnimation(botAni);
       slogan.setAnimation(botAni);
       new Handler().postDelayed(new Runnable() {
           @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
           @Override
           public void run() {
               Intent intent=new Intent(SplashScreen.this,LoginActivity.class);
               Pair[] pairs=new Pair[2];
               pairs[0]=new Pair<View,String>(imgView,"logo_image");
               pairs[1]=new Pair<View,String>(name,"logo_text");
               ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,pairs);
               startActivity(intent,options.toBundle());
               finish();

           }
       },3000);

    }
}
