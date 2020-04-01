package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Model.UserAccount;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    TextView regisTextView,forgotPassword;
    EditText tkEditText, mkEditText;
    CompositeDisposable compositeDisposable =new CompositeDisposable();
    IMyService iMyService;
    String TaiKhoan, MatKhau;
    AlertDialog alertDialog;
    UserAccount userAccount;
    SharedPreferences sharedPreferences;
    boolean flag=false;
    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         //Toast.makeText(this, sharedPreferences.getString("name","")+sharedPreferences.getInt("using",0), Toast.LENGTH_SHORT).show();

        if (sharedPreferences.getString("name","").length()>0)
        {
            Intent intent =new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_login);


        anhxa();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        regisTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisActivity.class);
                startActivity(intent);
                CustomIntent.customType(LoginActivity.this,"left-to-right");
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(KiemTra()) login();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgotPassActivity.class);
                startActivity(intent);
                CustomIntent.customType(LoginActivity.this,"left-to-right");
            }
        });
    }

    private void login() {
        loginBtn.setClickable(false);
        loginBtn.setEnabled(false);

        alertDialog.show();
        iMyService.loginUser(TaiKhoan,MatKhau)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> response) {
                        if(response.isSuccessful()){


                        if(response.body().toString().contains("name"))
                        {
                            String responeString=response.body().toString();
                            try {
                                JSONObject jo=new JSONObject(responeString);
                                userAccount=new UserAccount(jo.getString("name"),"",
                                                            jo.getString("phone"),
                                                            jo.getString("image"),
                                                            jo.getString("email"),
                                                            response.headers().get("Auth-token"),
                                                            jo.getString("gender"),
                                                            jo.getString("description"),
                                                            jo.getString("address"),
                                                           MatKhau,
                                                            jo.getString("_id")
                                                                );

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("name",userAccount.getHoten());
                                editor.putString("phone",userAccount.getSdt());
                                editor.putString("image",userAccount.getAva());
                                editor.putString("email",userAccount.getMail());
                                editor.putString("token",userAccount.getToken());
                                editor.putString("gender",userAccount.getGioitinh());
                                editor.putString("description",userAccount.getMota());
                                editor.putString("address",userAccount.getDiachia());
                                editor.putString("password",MatKhau);
                                editor.commit();
                                flag=true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        else{
                            flag=false;
                        }}
                        else{
                            flag=false;
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loginBtn.setClickable(true);
                        loginBtn.setEnabled(true);

                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);


                        if(flag==true) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("userAcc", userAccount);
                            intent.putExtra("change",0);
                            startActivity(intent);
                            CustomIntent.customType(LoginActivity.this, "right-to-left");
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            loginBtn.setClickable(true);
                            loginBtn.setEnabled(true);
                        }

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();

        System.exit(0);
    }
    private boolean KiemTra() {
        Boolean valid=true;
        TaiKhoan=tkEditText.getText().toString();
        MatKhau=mkEditText.getText().toString();
        if(TaiKhoan.isEmpty() ||TaiKhoan.length() < 3 || TaiKhoan.length() >40 )
        {
            tkEditText.setError("Từ 6 đến 40 ký tự");
            valid = false;
        } else {
            tkEditText.setError(null);
        }
        if(MatKhau.isEmpty() || MatKhau.length() <8 ||MatKhau.length()>16 )
        {
            mkEditText.setError("Mật khẩu có 8 đến 16 ký tự");
            valid = false;
        } else {
            mkEditText.setError(null);
        }
        return valid;

    }

    private void anhxa() {
        loginBtn=findViewById(R.id.loginBtn);
        regisTextView=findViewById(R.id.regText);
        tkEditText=findViewById(R.id.TaiKhoanEditText);
        mkEditText=findViewById(R.id.MatKhauEditText);
        forgotPassword=findViewById(R.id.forgotPass);
    }
}
