package com.bws.harsley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bws.harsley.Adapter.CalenderAdapter;
import com.bws.harsley.Commons.Common;
import com.bws.harsley.Models.CalenderModel;
import com.bws.harsley.Utils.DatabaseHelper;
import com.bws.harsley.Utils.PreferenceConnector;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MyCalenderActivity extends AppCompatActivity {
    TextView textJob_header, textFullName;
    private RecyclerView recyclerView;
    List<CalenderModel> arrayCalenderLis;
    private RecyclerView.Adapter adapter;
    ImageView imv_calenderPrev, imv_calenderNext;

    DatabaseHelper db = DatabaseHelper.getInstance(this);
    int year;
    int currentMonth = 0;
    AsyncHttpClient client;
    ProgressDialog pDialog;
    public String mycaledarUrl = Common.base_URL + "Calender";
    String asynchResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calender);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH) + 1;

        initview();
        clickEvent();
    }


    //    call customerDetails API using AsyncHttpClient
    private void myCalendar() throws JSONException, UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", PreferenceConnector.readString(this,"userid",""));
        jsonObject.put("rolename", PreferenceConnector.readString(this,"rolename",""));
        invokeCalendar(jsonObject);
    }

    private void invokeCalendar(JSONObject jsonObject) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(jsonObject.toString());
        client = new AsyncHttpClient();
        client.setTimeout(3000);
        client.post(this, mycaledarUrl, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                pDialog.setMessage("Please wait...");
                pDialog.show();
                pDialog.setCancelable(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                asynchResult = new String(String.valueOf(response));
                String status, message;
                if (asynchResult.isEmpty()) {
                    pDialog.dismiss();
                    new SweetAlertDialog(MyCalenderActivity.this, SweetAlertDialog.WARNING_TYPE)
                           // .setTitleText("Oops...")
                            .setContentText("Server not responding try again.")
                            .show();

                } else {

                    try {
                        JSONObject jsonObject1 = new JSONObject(asynchResult);
                        status = jsonObject1.getString("status");
                        message = jsonObject1.getString("message");

                        if (status.equals("SUCCESS")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                CalenderModel calenderModel = new CalenderModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                calenderModel.setStartDate(jsonObject.getString("startDate"));
                                calenderModel.setCompanyName(jsonObject.getString("companyName"));
                                calenderModel.setServiceName(jsonObject.getString("serviceName"));
                                arrayCalenderLis.add(calenderModel);
                            }
                            adapter = new CalenderAdapter(arrayCalenderLis);
                            recyclerView.setAdapter(adapter);
                        } else {
                            pDialog.dismiss();
                            new SweetAlertDialog(MyCalenderActivity.this, SweetAlertDialog.ERROR_TYPE)
                                   // .setTitleText("Oops...")
                                    .setContentText(message)
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Status code", String.valueOf(+statusCode));
            }
        });
    }

    private void initview() {
        imv_calenderPrev = (ImageView) findViewById(R.id.imv_calenderPrev);
        imv_calenderNext = (ImageView) findViewById(R.id.imv_calenderNext);
        textJob_header = (TextView) findViewById(R.id.textJob_header);
        textJob_header.setText("My Calendar");
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pDialog = new ProgressDialog(MyCalenderActivity.this);

        textFullName = (TextView) findViewById(R.id.textFullName);
        textFullName.setText("Hi  " + PreferenceConnector.readString(MyCalenderActivity.this, "fullName", ""));

        arrayCalenderLis = new ArrayList<CalenderModel>();

        //use for recycleview devider
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.line_divider);
        recyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        try {
            myCalendar();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void clickEvent() {
        findViewById(R.id.imv_Shutdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteAllUserDtails();
                Intent i = new Intent(MyCalenderActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });


        imv_calenderNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentMonth <= 12 && currentMonth > 1) {
                    currentMonth = currentMonth + 1;
                    String nextmonth1 = String.valueOf(currentMonth);
                    Log.d("rtyui", nextmonth1);
                }
            }
        });


        imv_calenderPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentMonth <= 12 && currentMonth > 1) {
                    currentMonth = currentMonth - 1;
                    Log.d("prev==", String.valueOf(currentMonth));
                }
            }
        });

        findViewById(R.id.textBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
