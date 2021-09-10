package com.phiot.phiot_client.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.phiot.phiot_client.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import Helper.ApiHelper;
import Helper.ProjectConfig;
import Helper.VolleyCallback;

import static android.content.ContentValues.TAG;
import static android.content.Context.WIFI_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetupDevice.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SetupDevice extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    LinearLayout llScans;
    Button btnScan;
    SwipeRefreshLayout swiperefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_setup_device, container, false);

        llScans = (LinearLayout) fragView.findViewById(R.id.llScans);

        swiperefresh = fragView.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(this);

        swiperefresh.setRefreshing(true);
        BindView();

        btnScan = fragView.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BindView();
            }
        });


        return fragView;
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

    public void BindView()
    {
        llScans.removeAllViews();
        ApiHelper.CallWithCustomBaseUrl(getContext(),ProjectConfig.PhiOT_Base_Url,"wifiscan","",new VolleyCallback(){

            @Override
            public void onSuccessResponse(String result) {
                try
                {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0;i<jsonArray.length();i++)
                    {
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);

                        final View list_scan = getLayoutInflater().inflate(R.layout.row, null, false);
                        TextView tvSsid = list_scan.findViewById(R.id.tvSsid);
                        tvSsid.setText(jsonObject.getString("ssid"));
                        list_scan.setTag(jsonObject.getString("ssid"));

                        TextView tvEncryptionType = list_scan.findViewById(R.id.tvEncryptionType);
                        tvEncryptionType.setText(jsonObject.getString("encryptionType"));

                        TextView tvRssi = list_scan.findViewById(R.id.tvRssi);
                        tvRssi.setText(jsonObject.getString("rssi"));

                        list_scan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Create custom dialog object
                                final Dialog dialog = new Dialog(getContext());
                                dialog.setContentView(R.layout.wifi_credentials);

                                TextView tvSsid = dialog.findViewById(R.id.tvSsid);
                                tvSsid.setText(view.getTag().toString());

                                final EditText etPassword = dialog.findViewById(R.id.etPassword);

                                final Button btnConnectToWifi = dialog.findViewById(R.id.btnConnectToWifi);
                                btnConnectToWifi.setTag(jsonObject);

                                btnConnectToWifi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try
                                        {
                                            btnConnectToWifi.setText("Loading..");
                                            btnConnectToWifi.setEnabled(false);
                                            JSONObject innorJsonObject = new JSONObject(view.getTag().toString());
                                            String data = "ssid="+innorJsonObject.getString("ssid")+"&password="+etPassword.getText().toString();

                                            ApiHelper.CallWithCustomBaseUrl(getContext(), ProjectConfig.PhiOT_Base_Url, "wificonnect?", data, new VolleyCallback() {
                                                @Override
                                                public void onSuccessResponse(String result) {
                                                    ProjectConfig.StaticToast(getContext(),result);
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                        catch (Exception e)
                                        {
                                            ProjectConfig.StaticToast(getContext(),"Something went wrong while fetching wifi information.");
                                            ProjectConfig.StaticLog(e.getMessage());
                                        }

                                    }
                                });

                                dialog.show();
                            }
                        });

                        llScans.addView(list_scan);
                    }
                }
                catch (Exception e)
                {
                    ProjectConfig.StaticToast(getContext(),"Something went wrong while fetching device information.");
                    ProjectConfig.StaticLog(result);
                }
                finally {
                    stopSwipAnimation();
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




}


