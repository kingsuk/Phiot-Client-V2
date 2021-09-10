package com.phiot.phiot_client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import Helper.ApiHelper;
import Helper.ProjectConfig;
import Helper.VolleyCallback;

public class DatasetActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    LinearLayout llDatasets;
    String token;
    SwipeRefreshLayout swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataset);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        token = getIntent().getStringExtra("token");
        llDatasets = (LinearLayout) findViewById(R.id.llDatasets);

        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(this);



        String dataDeviceId = "deviceId="+getIntent().getStringExtra("ds_deviceId");
        ApiHelper.Call(getApplicationContext(), "device/GetDeviceInfoByDeviceId?", dataDeviceId, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                //ProjectConfig.StaticToast(getApplicationContext(),result);
                try
                {
                    //ProjectConfig.StaticToast(getApplicationContext(),result);
                    JSONObject jsonObject = new JSONObject(result);

                    TextView tvDeviceName = findViewById(R.id.tvDeviceName);
                    tvDeviceName.setText(jsonObject.getString("deviceName"));

                    TextView tvDeviceToken = findViewById(R.id.tvDeviceToken);
                    tvDeviceToken.setText(jsonObject.getString("device_token"));

                    TextView tvNoOfCallLeft = findViewById(R.id.tvNoOfCallLeft);

                    int CallsLeft = jsonObject.getInt("apiCallsPerDay") - jsonObject.getInt("logCountToday");

                    tvNoOfCallLeft.setText(Integer.toString(CallsLeft));
                    //ProjectConfig.StaticToast(getApplicationContext(),jsonObject.getString("device_type_id"));
                    ImageView ivDeviceType = findViewById(R.id.ivDeviceType);
                    if(jsonObject.getString("device_type_id")=="1")
                    {

                        ivDeviceType.setImageResource(R.drawable.devicetype1_transparent);
                    }
                    else if(jsonObject.getString("device_type_id")=="2")
                    {
                        ivDeviceType.setImageResource(R.drawable.devicetype2);
                    }
                }
                catch (Exception e)
                {
                    ProjectConfig.StaticToast(getApplicationContext(),"Something went wrong while fetching device information.");
                    ProjectConfig.StaticLog(result);
                    ProjectConfig.StaticLog(e.getMessage());
                }
            }
        });

        swiperefresh.setRefreshing(true);
        BindView();
    }

    public void BindView()
    {
        llDatasets.removeAllViews();

        String data = "ds_deviceId="+getIntent().getStringExtra("ds_deviceId");

        ApiHelper.Call(getApplicationContext(), "Dataset/GetAllDatasetByUserIdAndDeviceId?", data, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                //ProjectConfig.StaticToast(getApplicationContext(),result);
                try
                {
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        final View list_dataset = getLayoutInflater().inflate(R.layout.list_dataset, null, false);

                        TextView tvDatasetName = list_dataset.findViewById(R.id.tvDatasetName);
                        tvDatasetName.setText(jsonObject.getString("ds_name"));

                        final Button btnOnButton = list_dataset.findViewById(R.id.btnOnButton);
                        btnOnButton.setTag(jsonObject.getString("jsonData"));
                        btnOnButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ButtonOnClick(view);
                            }
                        });

                        Button btnOffButton = list_dataset.findViewById(R.id.btnOffButton);
                        btnOffButton.setTag(jsonObject.getString("reverseJsonData"));
                        btnOffButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ButtonOnClick(view);
                            }
                        });

                        ImageView ivDeleteButton = list_dataset.findViewById(R.id.ivDeleteButton);
                        ivDeleteButton.setTag(jsonObject.getString("ds_id"));
                        ivDeleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {

                                new AlertDialog.Builder(DatasetActivity.this)
                                        .setTitle("Delete dataset.")
                                        .setMessage("Are you sure you want to delete ?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //Toast.makeText(getContext(), "Yaay", Toast.LENGTH_SHORT).show();
                                                String dataDataSetId = "ds_id="+view.findViewById(R.id.ivDeleteButton).getTag().toString();
                                                ApiHelper.Call(getApplicationContext(), "Dataset/DeleteDatasetByDsIdAndUserId?", dataDataSetId, new VolleyCallback() {
                                                    @Override
                                                    public void onSuccessResponse(String result) {
                                                        try
                                                        {

                                                            JSONObject jsonObject1 = new JSONObject(result);
                                                            ProjectConfig.StaticToast(getApplicationContext(),jsonObject1.getString("statusMessage"));
                                                            BindView();
                                                        }
                                                        catch (Exception e)
                                                        {
                                                            ProjectConfig.StaticToast(getApplicationContext(),"Something went wrong while sending request ot device.");
                                                            ProjectConfig.StaticLog(result);
                                                        }
                                                    }
                                                });
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();

                            }
                        });

                        llDatasets.addView(list_dataset);

                    }
                }
                catch (Exception e)
                {
                    ProjectConfig.StaticToast(getApplicationContext(),"Something went wrong while fetching dataset information.");
                    ProjectConfig.StaticLog(result);
                }
                finally {
                    stopSwipAnimation();
                }
            }
        });
    }

    public void ButtonOnClick(View view)
    {
        final TextView tvNoOfCallLeft = findViewById(R.id.tvNoOfCallLeft);

        if(Integer.parseInt(tvNoOfCallLeft.getText().toString()) <= 0)
        {
            ProjectConfig.StaticToast(getApplicationContext(),"No more calls left!");
            return;
        }
        String data = "token="+token+"&message="+view.findViewById(view.getId()).getTag().toString();
        ApiHelper.Call(getApplicationContext(), "publish/sendToDevice?", data, new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                //ProjectConfig.StaticToast(getApplicationContext(),result);
                try
                {

                    JSONObject jsonObject1 = new JSONObject(result);
                    ProjectConfig.StaticToast(getApplicationContext(),jsonObject1.getString("statusMessage"));

                    tvNoOfCallLeft.setText(Integer.toString(Integer.parseInt(tvNoOfCallLeft.getText().toString())-1));
                }
                catch (Exception e)
                {
                    ProjectConfig.StaticToast(getApplicationContext(),"Something went wrong while sending request ot device.");
                    ProjectConfig.StaticLog(result);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        BindView();
    }

    public void stopSwipAnimation(){
        if(swiperefresh.isRefreshing())
        {
            swiperefresh.setRefreshing(false);
        }
    }
}
