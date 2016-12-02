package com.dessert.mojitomanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity {
    private OkHttpClient mOkHttpClient;
    private TextView insIDView, insPasswordView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOkHttpClient = OkHttpUtil.getInstance().getmOkHttpClient();
        setContentView(R.layout.activity_main);
        insIDView = (TextView)findViewById(R.id.insID);
        insPasswordView = (TextView)findViewById(R.id.insPassword);
    }

    public void login(View view) {
        String insID = insIDView.getText().toString();
        String insPassword = insPasswordView.getText().toString();
        Log.i("LOGIN", insID + "; " + insPassword);
        final RequestBody body = new FormBody.Builder()
                .addEncoded("institutionId", insID)
                .addEncoded("password", insPassword)
                .build();
        final Request request = new Request.Builder()
                .url(OkHttpUtil.DOMAIN + "institution/institutionLogin.do")
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    Log.i("JSON", result.getString("error"));
                    if(result.getString("error").equals("0")) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, RegisterDeviceActivity.class);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
