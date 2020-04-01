package com.example.project1.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.project1.Fragment.accountFragment;
import com.example.project1.Fragment.featuredFragment;
import com.example.project1.Fragment.mycoursesFragment;
import com.example.project1.Fragment.searchFragment;
import com.example.project1.Fragment.cartFragment;
import com.example.project1.Model.UserAccount;
import com.example.project1.Model.category_item;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Retrofit;

import static android.view.View.GONE;

public class HomeActivity extends AppCompatActivity {

   BottomNavigationView bottomNav;
   Toolbar homeTB;
   SearchView searchView;
    IMyService iMyService;
    AlertDialog alertDialog;
   ArrayAdapter<String> suggestionAdapter;
    SharedPreferences sharedPreferences;
    Spinner spinner;
    ArrayList<String> categoriesName = new ArrayList<String>();
    ArrayList<String> categoriesID = new ArrayList<String>();
    boolean flag=false;
  public  UserAccount userAccount=new UserAccount();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AnhXa();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userAccount= new UserAccount();
        userAccount.setHoten(sharedPreferences.getString("name","default"));
        userAccount.setSdt(sharedPreferences.getString("phone","default"));
        userAccount.setMail(sharedPreferences.getString("email","default"));
        userAccount.setAva(sharedPreferences.getString("image","default"));
        userAccount.setMota(sharedPreferences.getString("description","default"));
        userAccount.setGioitinh(sharedPreferences.getString("gender","default"));
        userAccount.setDiachia(sharedPreferences.getString("address","default"));
        userAccount.setToken(sharedPreferences.getString("token","default"));
        userAccount.setMatkhau(sharedPreferences.getString("password","default"));
        bottomNav.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new featuredFragment()).commit();
        suggestionAdapter=new ArrayAdapter<>(HomeActivity.this,R.layout.suggestion);
        homeTB.setVisibility(GONE);
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
                ((TextView) parent.getChildAt(0)).setText("");
               if(text.equals("Create new course"))
               {
                   categoriesID.clear();
                   categoriesName.clear();
                  getAllCategory();

              }




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
           Fragment fragment=new Fragment();
           switch(menuItem.getItemId())
           {
               case R.id.feature:
                    homeTB.setVisibility(GONE);
                   searchView.setVisibility(GONE);
                   fragment=new featuredFragment();
                   break;
               case R.id.search_frag:
                   homeTB.setVisibility(View.VISIBLE);
                   fragment=new searchFragment();
                    searchView.setVisibility(View.VISIBLE);

                   homeTB.setTitle("");
                   homeTB.setTitleTextColor(-1);
                   break;
               case R.id.my_course_frag:
                   homeTB.setVisibility(View.VISIBLE);
                   fragment=new mycoursesFragment();
                   homeTB.setTitle("Courses");

                   spinner.setVisibility(View.VISIBLE);
                   searchView.setVisibility(GONE);
                   homeTB.setTitleTextColor(-1);
                   break;
               case R.id.cart_frag:
                   homeTB.setVisibility(View.VISIBLE);
                   fragment=new cartFragment();
                   homeTB.setTitle("Cart");
                   spinner.setVisibility(GONE);
                   searchView.setVisibility(GONE);
                   homeTB.setTitleTextColor(-1);
                   break;
               case R.id.account_frag:
                   homeTB.setVisibility(View.VISIBLE);
                  // Toast.makeText(HomeActivity.this, userAccount.getAva(), Toast.LENGTH_SHORT).show();
                   fragment=new accountFragment(userAccount);
                   homeTB.setTitle("Tài Khoản");
                   searchView.setVisibility(GONE);
                   spinner.setVisibility(GONE);
                   homeTB.setTitleTextColor(-1);
                   break;
           }
           getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
           return true;

        }
    };
    private void AnhXa() {
        bottomNav=findViewById(R.id.bottomNav);
        homeTB=findViewById(R.id.homeTB);
        searchView=findViewById(R.id.searchView);
        spinner=findViewById(R.id.spinner1);

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();

        System.exit(0);
    }

    private void getAllCategory(){
        alertDialog.show();
        iMyService.getAllCategory().
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {




                        try {

                            String temp=response;

                            //JSONObject jsonObject=new JSONObject(temp);
                            Toast.makeText(HomeActivity.this, "here", Toast.LENGTH_SHORT).show();
                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                String tempName=jo.getString("name");
                                String tempID=jo.getString("_id");

                                categoriesName.add(tempName);
                                categoriesID.add(tempID);
                               flag=true;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(HomeActivity.this, "size: "+categoriesID.size(), Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(HomeActivity.this,CreateCourseActivity.class);
                            intent.putStringArrayListExtra("list1",categoriesName);
                            intent.putStringArrayListExtra("list2",categoriesID);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(HomeActivity.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });


    }



}
