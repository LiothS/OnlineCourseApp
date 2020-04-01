package com.example.project1.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project1.Model.UserAccount;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisActivity extends AppCompatActivity {

    EditText TaiKhoanText, NgaySinhText,SdtText, HoTenText, MatKhauText, XacNhanText,DiaChiText,GioiTinhText,MoTaText;
    TextView logText;
    Button RegButton;
    String myDateOfBirth;
    String[] temp;
    String name = "";
    String username = "";
    String mobile = "";
    String password = "";
    String DateOfBirth ="";
    public static final String URL = "http://192.168.28.2:9000/register";
    String reEnterPassword = "";
    String DiaChi="",MoTa="",GioiTinh="",token="";
    UserAccount userAccount;
    private DatePickerDialog.OnDateSetListener birthdayListener;
    CompositeDisposable compositeDisposable =new CompositeDisposable();
    IMyService iMyService;
    Boolean flag=true;
    AlertDialog alertDialog;
    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);
        AnhXa();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        iMyService=retrofitClient.create(IMyService.class);

        logText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisActivity.this, LoginActivity.class);
                startActivity(intent);
                CustomIntent.customType(RegisActivity.this,"right-to-left");
            }
        });
        NgaySinhText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        RegisActivity.this,
                        android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth,birthdayListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        birthdayListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;


                String date = day +"/"+month+"/"+year;
                NgaySinhText.setText(date);
            }
        };
        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (KiemTra()) {

                    Regis();
                }

            }
        });
    }

    private void Regis() {
        RegButton.setClickable(false);
        RegButton.setEnabled(false);

        alertDialog.show();
       iMyService.registerUser(name,username,password,mobile,DiaChi,MoTa,GioiTinh)
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
                          userAccount=new UserAccount(name,"",mobile,"",username,response.headers().get("auth-token"),GioiTinh,MoTa,DiaChi,password,jo.getString("_id"));
                          flag=true;
                     } catch (JSONException e) {
                          e.printStackTrace();
                       }

                   }
                   else{
                       Toast.makeText(RegisActivity.this, "Mail đã tồn tại", Toast.LENGTH_SHORT).show();
                       flag=false;
                   }
               }
               else {Toast.makeText(RegisActivity.this, "Mail đã tồn tại", Toast.LENGTH_SHORT).show();
                   flag=false;}




           }

           @Override
           public void onError(Throwable e) {
               new android.os.Handler().postDelayed(
                       new Runnable() {
                           public void run() {
                               alertDialog.dismiss();

                           }
                       }, 500);
                       Toast.makeText(RegisActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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


                if(flag==true) {
                    Intent intent = new Intent(RegisActivity.this, ActiveAccountActivity.class);
                    intent.putExtra("userAcc", userAccount);
                    startActivity(intent);
                    CustomIntent.customType(RegisActivity.this, "right-to-left");
                }
                else {
                    Toast.makeText(RegisActivity.this, "Mail đã tồn tai", Toast.LENGTH_SHORT).show();
                    RegButton.setClickable(true);
                    RegButton.setEnabled(true);
                }

           }
       });



    }

    private boolean KiemTra() {
        boolean valid = true;
         name = HoTenText.getText().toString();
         username = TaiKhoanText.getText().toString().trim();
         mobile = SdtText.getText().toString().trim();
        password = MatKhauText.getText().toString().trim();
         DateOfBirth = NgaySinhText.getText().toString();
        reEnterPassword = XacNhanText.getText().toString().trim();
        DiaChi=DiaChiText.getText().toString();
        MoTa=MoTaText.getText().toString();
        GioiTinh=GioiTinhText.getText().toString();
        if(name.isEmpty()||name.length()<3 || name.length()>40)
        {
            HoTenText.setError("Tên từ 3 đến 40 ký tự");
            valid = false;
        } else {
            HoTenText.setError(null);
        }

        if(username.isEmpty() || username.length() < 3 || username.length() >40 )
        {
            TaiKhoanText.setError("Từ 6 đến 40 ký tự");
            valid = false;
        } else {
            TaiKhoanText.setError(null);
        }

        if(DateOfBirth.isEmpty())
        {
            NgaySinhText.setError("Vui lòng nhập ngày sinh");
            valid = false;
        }else
        {
            temp=DateOfBirth.split("/");
            myDateOfBirth=temp[2]+"/"+temp[1]+"/"+temp[0];
            NgaySinhText.setError(null);
        }

        if(mobile.isEmpty())
        {
            SdtText.setError("Nhập số điện thoại không hợp lệ ");
            valid = false;
        } else {
            SdtText.setError(null);
        }

        if(password.isEmpty() || password.length() <8 || password.length()>16 )
        {
            MatKhauText.setError("Mật khẩu có 8 đến 16 ký tự");
            valid = false;
        } else {
            MatKhauText.setError(null);
        }

        if(reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || reEnterPassword.length()>16 || !reEnterPassword.equals(password) )
        {
            XacNhanText.setError("Mật khẩu không khớp");
            valid = false;
        } else{
            XacNhanText.setError(null);
        }

        return valid;


    }

    private void AnhXa() {
        TaiKhoanText=findViewById(R.id.taikhoanText);
        NgaySinhText=findViewById(R.id.NgaySinhText);
        SdtText=findViewById(R.id.sdtText);
        HoTenText=findViewById(R.id.HoTennText);
        MatKhauText=findViewById(R.id.matkahuText);
        XacNhanText=findViewById(R.id.xacnhanText);
        logText=findViewById(R.id.logText);
        RegButton=findViewById(R.id.regisBtn);
        GioiTinhText=findViewById(R.id.gioitinhText);
        DiaChiText=findViewById(R.id.diachiText);
        MoTaText=findViewById(R.id.motaText);
    }

    @Override
    public void finish() {
        super.finish();

        CustomIntent.customType(RegisActivity.this,"right-to-left");
    }
}
