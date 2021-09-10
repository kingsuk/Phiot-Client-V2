package com.phiot.phiot_client.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Api;
import com.phiot.phiot_client.DatasetActivity;
import com.phiot.phiot_client.LoginActivity;
import com.phiot.phiot_client.MainActivity;
import com.phiot.phiot_client.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Helper.ApiHelper;
import Helper.ProjectConfig;
import Helper.VolleyCallback;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    LinearLayout llDevices;

    SwipeRefreshLayout swiperefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        llDevices = (LinearLayout) fragView.findViewById(R.id.llDevices);

        swiperefresh = fragView.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(this);

        swiperefresh.setRefreshing(true);
        BindView();

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String fragment);
    }

    public void BindView()
    {
        llDevices.removeAllViews();
        ApiHelper.Call(getContext(),"device/GetAllDevicesByUser","", new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try
                {

                    JSONArray responseArray = new JSONArray(result);
                    for(int i=0;i<responseArray.length();i++)
                    {
                        JSONObject jsonObject= responseArray.getJSONObject(i);

                        final View list_device = getLayoutInflater().inflate(R.layout.list_device, null, false);

                        TextView tvDeviceName = list_device.findViewById(R.id.tvDeviceName);
                        tvDeviceName.setText(jsonObject.getString("deviceName"));
                        tvDeviceName.setTag(jsonObject.getString("id"));

                        TextView tvDeviceToken = list_device.findViewById(R.id.tvDeviceToken);
                        tvDeviceToken.setTag(jsonObject.getString("device_token"));
                        tvDeviceToken.setText(jsonObject.getString("device_token"));


                        ImageView ivDelete = list_device.findViewById(R.id.ivDelete);
                        ivDelete.setTag(jsonObject.getString("id"));
                        ivDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {

                                new AlertDialog.Builder(getContext())
                                        .setTitle("Delete device.")
                                        .setMessage("Are you sure you want to delete ?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                //Toast.makeText(getContext(), "Yaay", Toast.LENGTH_SHORT).show();
                                                String dataDeviceId = "id="+view.findViewById(R.id.ivDelete).getTag().toString();
                                                ApiHelper.Call(getContext(), "device/DeleteDeviceByDeviceAndUserId?", dataDeviceId, new VolleyCallback() {
                                                    @Override
                                                    public void onSuccessResponse(String result) {
                                                        try
                                                        {

                                                            JSONObject jsonObject1 = new JSONObject(result);
                                                            ProjectConfig.StaticToast(getContext(),jsonObject1.getString("statusMessage"));
                                                            BindView();
                                                        }
                                                        catch (Exception e)
                                                        {
                                                            ProjectConfig.StaticToast(getContext(),"Something went wrong while sending request ot device.");
                                                            ProjectConfig.StaticLog(result);
                                                        }
                                                    }
                                                });
                                            }})
                                        .setNegativeButton(android.R.string.no, null).show();
                            }
                        });

                        ImageView ivDeviceTypeImage = list_device.findViewById(R.id.ivDeviceTypeImage);
                        if(jsonObject.getInt("device_type_id")==1)
                        {
                            ivDeviceTypeImage.setImageResource(R.drawable.devicetype1);
                        }
                        else if(jsonObject.getInt("device_type_id")==2)
                        {
                            ivDeviceTypeImage.setImageResource(R.drawable.devicetype2);
                        }

                        list_device.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getContext(), DatasetActivity.class);
                                i.putExtra("ds_deviceId",view.findViewById(R.id.tvDeviceName).getTag().toString());
                                i.putExtra("token",view.findViewById(R.id.tvDeviceToken).getTag().toString());
                                getContext().startActivity(i);
                            }
                        });

                        llDevices.addView(list_device);

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



}
