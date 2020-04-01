package com.example.project1.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserPasswordActivity extends AppCompatActivity {

    Toolbar userPassTB;
    EditText OldPass, newPass, confirmPass;
    Button UpdateBtn;
    IMyService iMyService;

    AlertDialog alertDialog;
    boolean flag=false;
    UserAccount userAccount=new UserAccount();
    String oldPassord="", newPassword="",confirmPassword="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_password);
        userAccount= (UserAccount) getIntent().getSerializableExtra("userAcc");
        AnhXa();

        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        ActionToolBar();
    }

    private void ActionToolBar() {
        setSupportActionBar(userPassTB);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userPassTB.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userPassTB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(KiemTra()) changePass();
            }
        });
    }

    private void changePass() {
       UpdateBtn.setClickable(false);
        UpdateBtn.setEnabled(false);

        alertDialog.show();
        iMyService.changePass(oldPassord,newPassword,userAccount.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> response) {

                        if(response.isSuccessful()){


                            if(response.body().toString().contains("success"))
                            {
                                String responeString=response.body().toString();
                                try {
                                    JSONObject jo = new JSONObject(responeString);
                                    userAccount.setMatkhau(newPassword);
                                    flag=true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                            else{
                                flag=false;
                            }}


                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(UserPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        flag=false;

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
                        { Toast.makeText(UserPasswordActivity.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();

                            // Truyền data vào intent
                            data.putExtra("usernewAcc", userAccount);

                            // Đặt resultCode là Activity.RESULT_OK to
                            // thể hiện đã thành công và có chứa kết quả trả về
                            setResult(Activity.RESULT_OK, data);

                            // gọi hàm finish() để đóng Activity hiện tại và trở về MainActivity.
                            finish();
                        }
                        else
                            Toast.makeText(UserPasswordActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                            UpdateBtn.setEnabled(true);
                            UpdateBtn.setClickable(true);
                    }
                });

    }

    private boolean KiemTra() {
        boolean valid=true;
        oldPassord=OldPass.getText().toString();
        newPassword=newPass.getText().toString();
        confirmPassword=confirmPass.getText().toString();
        if(oldPassord.isEmpty()||!oldPassord.equals(userAccount.getMatkhau()))
        {
            valid=false;
            Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
            return valid;
        }
        else
        {
            valid=true;
        }
        if(newPassword.isEmpty() || newPassword.length() <8 || newPassword.length()>16)
        {
            valid=false;
            Toast.makeText(this, "Mật khẩu mới phải từ 8 đến 16 ký tự", Toast.LENGTH_SHORT).show();
            return valid;
        }
        else{
            valid=true;
        }
        if(!confirmPassword.equals(newPassword))
        {
            valid=false;
            Toast.makeText(this, "Xác nhận mật khẩu không khóp", Toast.LENGTH_SHORT).show();
            return valid;


        }
        else{
            valid=true;
        }
        return valid;
    }

    private void AnhXa() {
        userPassTB=findViewById(R.id.userPasswordTB);
        OldPass=findViewById(R.id.userPassword);
        newPass=findViewById(R.id.newPass);
        confirmPass=findViewById(R.id.confirmPass);
        UpdateBtn=findViewById(R.id.updatePassBtn);

    }
}
