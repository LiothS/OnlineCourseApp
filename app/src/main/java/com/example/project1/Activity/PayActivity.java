package com.example.project1.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.R;
import com.example.project1.Retrofit.IMyService;
import com.example.project1.Retrofit.RetrofitClient;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentAuthConfig;
import com.stripe.android.Stripe;

import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.CardException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.exception.StripeException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class PayActivity extends AppCompatActivity {
    CardInputWidget cardInputWidget;
    TextInputEditText emailEditText, nameEditText;
    Button payBtn;

    SharedPreferences sharedPreferences;
    JSONArray cartArray=new JSONArray();
    JSONObject sendJO=new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        cardInputWidget=findViewById(R.id.cardInput);
        emailEditText=findViewById(R.id.payEmail);
        nameEditText=findViewById(R.id.payName);
        payBtn=findViewById(R.id.payBtn);
        emailEditText.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);
        cardInputWidget.setPostalCodeEnabled(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            cartArray= new JSONArray(sharedPreferences.getString("cartArray", "[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray cart=new JSONArray();
        for(int i=0;i<cartArray.length();i++)
        {
            JSONObject jo=new JSONObject();
            try {
                jo.put("_id",cartArray.getJSONObject(i).getString("courseID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cart.put(jo);
        }
        try {
            sendJO.put("cart",cart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            sendJO.put("idUser",sharedPreferences.getString("id",null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card=cardInputWidget.getCard();
                Stripe stripe=new Stripe(PayActivity.this,"pk_test_y8urHXEikr7ysm3tk7uRilcp00aTSdh57w");

                stripe.createCardToken(
                        card,
                        new ApiResultCallback<Token>() {
                            public void onSuccess(@NonNull Token token) {
                                JSONObject tokenJo=new JSONObject();
                               JSONObject cardJo=new JSONObject();
                                try {
                                    cardJo.put("id",token.getCard().getId());
                                    cardJo.put("object","card");
                                    cardJo.put("address_city","");
                                    cardJo.put("address_country","");
                                    cardJo.put("address_line1","");
                                    cardJo.put("address_line1_check","");
                                    cardJo.put("address_line2","");
                                    cardJo.put("address_state","");
                                    cardJo.put("address_zip","");
                                    cardJo.put("address_zip_check","");
                                    cardJo.put("brand","Visa");
                                    cardJo.put("country","US");
                                    cardJo.put("cvc_check",token.getCard().getCvcCheck());
                                    cardJo.put("dynamic_last4",null);
                                    cardJo.put("exp_month",token.getCard().getExpMonth());
                                    cardJo.put("exp_year",token.getCard().getExpYear());
                                    cardJo.put("funding",token.getCard().getFunding());
                                    cardJo.put("last4",token.getCard().getLast4());
                                    cardJo.put("metadata",token.getCard().getMetadata());
                                    cardJo.put("name","caohoangtu1357@gmail.com");
                                    cardJo.put("tokenization_method",token.getCard().getTokenizationMethod());


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    tokenJo.put("card",cardJo);
                                    tokenJo.put("id",token.getId());
                                    tokenJo.put("object","token");
                                    tokenJo.put("client_ip",null);
                                    tokenJo.put("created",token.getCreated());
                                    tokenJo.put("email","caohoangtu1357@gmail.com");
                                    tokenJo.put("livemode",token.getLivemode());
                                    tokenJo.put("type",token.getType());
                                    tokenJo.put("used",token.getUsed());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    sendJO.put("token",tokenJo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                emailEditText.setVisibility(View.GONE);
                                nameEditText.setVisibility(View.GONE);

                                Pay();

                            }
                            public void onError(@NonNull Exception error) {


                            }
                        }
                );
            }
        });


    }
    String resultTemp="";
    boolean flag=false;
    private void Pay() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), sendJO.toString());
        iMyService.pay(body).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {


                        flag=true;
                        resultTemp=response;

                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(PayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            final Intent data = new Intent();


                            data.putExtra(EXTRA_DATA, "success");
                            data.putExtra("isPaid",true);

                            setResult(Activity.RESULT_OK, data);
                            finish();
                            Toasty.success(PayActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();


                        }
                        else
                            Toast.makeText(PayActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }

}
