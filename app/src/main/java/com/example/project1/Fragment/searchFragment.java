package com.example.project1.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.project1.Activity.SearchResultActivity;
import com.example.project1.Adapter.categoryRVAdapter;
import com.example.project1.Adapter.categorySearchAdapter;
import com.example.project1.Model.category_item;
import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import maes.tech.intentanim.CustomIntent;
import retrofit2.Retrofit;


public class searchFragment extends Fragment {


ChipGroup chipGroup;

    ArrayList<category_item> items = new ArrayList<>();
    categoryRVAdapter adapter;
    RecyclerView recyclerView;
    SearchView searchView;
    public searchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_search, container, false);
        chipGroup=rootView.findViewById(R.id.chipGroup);
        searchView=rootView.findViewById(R.id.searchViewItem);
        ArrayList<String> suggest = new ArrayList<String>();
        suggest.add("lap trinh");
        suggest.add("thpt");
        suggest.add("java");
        suggest.add("adobe");
        suggest.add("javascript");
        suggest.add("toan");
        suggest.add("van");
        suggest.add("am nhac");
        suggest.add("ngoai ngu");
        suggest.add("tieng anh");
        suggest.add("tieng nhat");

        adapter = new categoryRVAdapter(items,getActivity());
       recyclerView = rootView.findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        LoadAllCategory();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent=new Intent(getContext(), SearchResultActivity.class);
                intent.putExtra("keyWord",searchView.getQuery().toString());
                startActivityForResult(intent,1234);
                CustomIntent.customType(getContext(),"left-to-right");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        
    for(int i=0;i<suggest.size();i++)
      {
          addChip(suggest.get(i));
      }
   return rootView;
    }

    private void addChip(String s) {

        // Create a Chip from Layout.
        Chip newChip = (Chip)getLayoutInflater().inflate(R.layout.layout_chip_entry, chipGroup, false);
        newChip.setTextSize(15);
        newChip.setPadding(5,20,5,20);
        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(getContext(), SearchResultActivity.class);
            intent.putExtra("keyWord",newChip.getText());
           startActivityForResult(intent,1234);
           CustomIntent.customType(getContext(),"left-to-right");
//                AlertDialog.Builder  mbuilder=new AlertDialog.Builder(getContext());
//                View view=getLayoutInflater().inflate(R.layout.create_rate_dialog,null);
//
//                mbuilder.setView(view);
//                AlertDialog alertDialog=mbuilder.create();
//                alertDialog.show();
                //Toasty.success(getContext(), "This is an error toast.", Toast.LENGTH_SHORT, true).show();

            }
        });
        newChip.setChipText(s);
        newChip.setText(s);

        chipGroup.addView(newChip);


        //
        // Other methods:
        //
        // newChip.setCloseIconVisible(true);
        // newChip.setCloseIconResource(R.drawable.your_icon);
        // newChip.setChipIconResource(R.drawable.your_icon);
        // newChip.setChipBackgroundColorResource(R.color.red);
        // newChip.setTextAppearanceResource(R.style.ChipTextStyle);
        // newChip.setElevation(15);



    }
    boolean flag_category=false;
    private void LoadAllCategory() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
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
                            //Toast.makeText(getContext(), "here", Toast.LENGTH_SHORT).show();
                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                String tempName=jo.getString("name");
                                String tempID=jo.getString("_id");
                                String img=jo.getString("image");
                                items.add(new category_item(tempName,tempID,"http://149.28.24.98:9000/upload/category/"+img));
                                adapter.notifyDataSetChanged();

                                flag_category=true;

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
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

                        if(flag_category==true)
                        {

                        }
                        else
                            Toast.makeText(getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });
    }


}
