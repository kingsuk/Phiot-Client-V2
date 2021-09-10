package com.phiot.phiot_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Helper.ApiHelper;
import Helper.ProjectConfig;
import Helper.VolleyCallback;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!ProjectConfig.ValidateEmail(etEmail.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Invalid Email!",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(etPassword.getText().length()<6)
                {
                    Toast.makeText(getApplicationContext(),"Password must be at least 6 characters long.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String data = "email="+etEmail.getText()+"&password="+etPassword.getText();

                ApiHelper.Call(getApplicationContext(), "auth/AuthAttempt?", data, new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(result);
                            String token = jsonObject.getString("token");
                            String email = jsonObject.getString("email");

                            SharedPreferences.Editor editor = getSharedPreferences(ProjectConfig.SharedPreferenceName, MODE_PRIVATE).edit();
                            editor.putString("token", token);
                            editor.putString("email",email);

                            if(editor.commit())
                            {
                                Toast.makeText(getApplication(),"Login Successful.",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getApplication(),"Some problem occoured, You may have to login again when you launch the app!",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }

                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                            ProjectConfig.StaticLog(result);
                        }
                    }
                });
            }
        });




    }


}
