package com.example.project1.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Retrofit;

public class ForgotPassActivity extends AppCompatActivity {
    EditText Email;
    Button moveOn;
    IMyService iMyService;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        Email=findViewById(R.id.mailForgot);
        moveOn=findViewById(R.id.moveOn);
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        moveOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Email.getText().toString().isEmpty())
                    SendMail();
                else
                    Toast.makeText(ForgotPassActivity.this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void SendMail() {
        alertDialog.show();
        iMyService.forgotPass(Email.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {


                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        //JSONObject jObjError = new JSONObject(e.)

                        Toast.makeText(ForgotPassActivity.this, "Mail không đúng", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(ForgotPassActivity.this, "Mã xác thực được gửi đến mail bạn", Toast.LENGTH_LONG).show();

                        Intent intent=new Intent(ForgotPassActivity.this,ResetPassword.class);
                        intent.putExtra("mail",Email.getText().toString());
                        startActivity(intent);
                        CustomIntent.customType(ForgotPassActivity.this,"bottom-to-up");

                    }
                });
    }
}
