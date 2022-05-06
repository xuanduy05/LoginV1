package com.example.loginv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etname, ethobby, etusername, etpassword;
    private Button btnregister;
    private TextView tvlogin;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceHelper = new PreferenceHelper(this);
        if(preferenceHelper.getIsLogin()){
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }
        etname = findViewById(R.id.etname);
        ethobby = findViewById(R.id.ethobby);
        etusername = findViewById(R.id.etusername);
        etpassword = findViewById(R.id.etpassword);

        btnregister = findViewById(R.id.btnregister);
        tvlogin = findViewById(R.id.tvlogin);
        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerMe();
            }
        });
    }

    private void registerMe() {
        final String name = etname.getText().toString();
        final String hobby = ethobby.getText().toString();
        final String username = etusername.getText().toString();
        final String password = etpassword.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RegisterInterface.REGIURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RegisterInterface api = retrofit.create(RegisterInterface.class);

        Call<String> call = api.getUserRegi(name,hobby,username,password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Responsestring", response.body().toString());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        String jsonresponse = response.body().toString();
                        parseRegData(jsonresponse);

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseRegData(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.optString("status").equals("true")){

                saveInfo(response);

                Toast.makeText(MainActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                this.finish();
            }else {

                try {
                    Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

    }

    private void saveInfo(String response) {
        preferenceHelper.putIsLogin(true);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putName(dataobj.getString("name"));
                    preferenceHelper.putHobby(dataobj.getString("hobby"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}