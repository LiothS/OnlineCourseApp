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

public class ResetPassword extends AppCompatActivity {
    EditText resetToken,newPass,confirmPass;
    Button resetPass;
    IMyService iMyService;
    AlertDialog alertDialog;
    String mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mail=getIntent().getStringExtra("mail");
        resetToken=findViewById(R.id.resetToken);
        newPass=findViewById(R.id.newResetPass);
        resetPass=findViewById(R.id.Reset);
        confirmPass=findViewById(R.id.confirmResetPass);
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(KiemTra()) Reset();
            }
        });
    }

    private boolean KiemTra() {
        boolean valid=true;
        String enterPass=newPass.getText().toString();
        String reeneterPass=confirmPass.getText().toString();

        if(resetToken.getText().toString().isEmpty())
        {
            valid=false;
            resetToken.setError("Nhập mã xác nhận");
        }
        else
        resetPass.setError(null);
        if(newPass.getText().toString().isEmpty())
        {
            valid=false;
            resetToken.setError("Nhập mật khẩu");
        }
        else  resetPass.setError(null);
        if(!enterPass.equals(reeneterPass))
        {
            valid=false;
            confirmPass.setError("Mật khẩu không khớp");
        }
        else  resetPass.setError(null);
        return  valid;

    }

    private void Reset() {
        alertDialog.show();
        iMyService.resetPass(mail,resetToken.getText().toString(),newPass.getText().toString())
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

                        Toast.makeText(ResetPassword.this, "Mã xác nhận không đúng", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {

                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(ResetPassword.this, "Tạo lại mật khẩu thành công", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(ResetPassword.this,LoginActivity.class);

                        startActivity(intent);
                        CustomIntent.customType(ResetPassword.this,"bottom-to-up");

                    }
                });
    }
}
