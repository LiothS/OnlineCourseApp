package com.example.project1.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.Activity.CreateCourseActivity;
import com.example.project1.Activity.HomeActivity;
import com.example.project1.Activity.LoginActivity;
import com.example.project1.Activity.UserInfoActivity;
import com.example.project1.Activity.UserPasswordActivity;
import com.example.project1.Activity.UsserAvatarActivity;
import com.example.project1.Model.UserAccount;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Retrofit;


public class accountFragment extends Fragment {

    private CircleImageView circleImageView;
    TextView Name,Email;
    String URLDefault="http://13.68.245.234:9000/upload/user_image/";
    UserAccount userAccount;

    public accountFragment() {
        // Required empty public constructor
    }

    public accountFragment(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView= inflater.inflate(R.layout.fragment_account, container, false);
        circleImageView=rootView.findViewById(R.id.accountFrag_user_avatar);
        String avurl=URLDefault+userAccount.getAva();
        Picasso.get().load(avurl).placeholder(R.drawable.useravatar).error(R.drawable.useravatar).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
        //Cập nhật thông tin cá nhân
        Name=rootView.findViewById(R.id.accountFrag_user_name);
       Name.setText(userAccount.getHoten());
         Email=rootView.findViewById(R.id.accountFrag_account);
        Email.setText(userAccount.getMail());
        TextView ThongTinCaNhan=rootView.findViewById(R.id.tvThongTinCaNhan);
        ThongTinCaNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),UserInfoActivity.class);
                intent.putExtra("userAcc", userAccount);
                startActivityForResult(intent,1);
                CustomIntent.customType(getContext(),"bottom-to-up");

            }
        });
        //Cập nhật ảnh đại diện
        TextView CapNhatAnhDaiDien=rootView.findViewById(R.id.tvAnhDaiDien);

        CapNhatAnhDaiDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UsserAvatarActivity.class);
                intent.putExtra("userAcc", userAccount);
                startActivityForResult(intent,2);
                CustomIntent.customType(getContext(),"bottom-to-up");
            }
        });
        //Cập nhật mật khẩu
        TextView CapNhatMatKhau=rootView.findViewById(R.id.tvBaoMat);
        CapNhatMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UserPasswordActivity.class);
                intent.putExtra("userAcc", userAccount);
                startActivityForResult(intent,0);
                CustomIntent.customType(getContext(),"bottom-to-up");

            }
        });
        //Đăng xuất
        TextView Logout=rootView.findViewById(R.id.logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(getContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc muốn đăng xuất")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               Logout();

                            }
                        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();




            }
        });


        return rootView;
    }
 private  boolean flag=false;
    private void Logout() {

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.userLogout(userAccount.getToken()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {




                       if(response.contains("success")) flag=true;
                       else flag=false;
                       // Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();



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

                        if(flag==true)
                        {
                            SharedPreferences sharedPreferences;
                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("name");
                            editor.remove("phone");
                            editor.remove("image");
                            editor.remove("image");
                            editor.remove("email");
                            editor.remove("gender");
                            editor.remove("description");
                            editor.remove("address");
                            editor.remove("token");
                            editor.remove(("cartArray"));
                            editor.commit();
                            Intent intent =new Intent(getContext(),LoginActivity.class);
                            startActivity(intent);

                        }
                        else
                            Toast.makeText(getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {


            if(resultCode == Activity.RESULT_OK) {

                userAccount = (UserAccount) data.getSerializableExtra("usernewAcc");
                ((HomeActivity)getActivity()).userAccount=userAccount;
                SharedPreferences sharedPreferences;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("password",userAccount.getMatkhau());
                editor.apply();


            } else {

            }
        }
        if(requestCode == 1) {


            if(resultCode == Activity.RESULT_OK) {

                userAccount = (UserAccount) data.getSerializableExtra("usernewAcc");
                Name.setText(userAccount.getHoten());
                ((HomeActivity)getActivity()).userAccount=userAccount;
                SharedPreferences sharedPreferences;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name",userAccount.getHoten());
                editor.putString("description",userAccount.getMota());
                editor.putString("gender",userAccount.getGioitinh());
                editor.putString("phone",userAccount.getSdt());
                editor.putString("address",userAccount.getDiachia());
                editor.putString("gender",userAccount.getGioitinh());


                editor.apply();

            } else {

            }
        }
        if(requestCode == 2) {


            if(resultCode == Activity.RESULT_OK) {

                userAccount = (UserAccount) data.getSerializableExtra("usernewAcc");
              //  Toast.makeText(getContext(), userAccount.getAva(), Toast.LENGTH_SHORT).show();
                Picasso.get().load(URLDefault+userAccount.getAva()).placeholder(R.drawable.useravatar).error(R.drawable.useravatar).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
                ((HomeActivity)getActivity()).userAccount=userAccount;
                SharedPreferences sharedPreferences;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("image",userAccount.getAva());

                editor.apply();
            } else {

            }
        }
    }
}
