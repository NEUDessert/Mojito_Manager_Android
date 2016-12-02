package com.dessert.mojitomanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Lawrence on 2016/11/30.
 */
public class RegisterDeviceActivity extends Activity {
    private double locX, locY;
    private EditText locationText;
    private EditText deviceIdText;
    private static final int SET_LOCATION = 0;
    private static final int REG_SUCCESS = 1;
    private static final int REG_FAIL = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if(message.what == SET_LOCATION) {
                String location = "(" + locX + ", " + locY + ")";
                locationText.setText(location);
            }
            if(message.what == REG_SUCCESS) {
                new AlertDialog.Builder(RegisterDeviceActivity.this)
                        .setMessage("注册成功！")
                        .setPositiveButton("确定", null)
                        .show();
            }
            if(message.what == REG_FAIL) {
                new AlertDialog.Builder(RegisterDeviceActivity.this)
                        .setMessage("注册失败！")
                        .setPositiveButton("确定", null)
                        .show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
        setContentView(R.layout.layout_register_device);
        locationText = (EditText)findViewById(R.id.location);
        deviceIdText = (EditText)findViewById(R.id.deviceid);
    }
    private void getLocation() {
        final AMapLocationClient mapLocationClient;
        AMapLocationClientOption mapLocationClientOption = new AMapLocationClientOption();
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation != null) {
                    if(aMapLocation.getErrorCode() == 0) {
                        locX = aMapLocation.getLongitude();
                        locY = aMapLocation.getLatitude();
                        Message message = new Message();
                        message.what = SET_LOCATION;
                        handler.sendMessage(message);
                    }
                }
            }
        };
        mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mapLocationClientOption.setOnceLocationLatest(true);
        mapLocationClient = new AMapLocationClient(getApplicationContext());
        mapLocationClient.setLocationListener(mLocationListener);
        mapLocationClient.setLocationOption(mapLocationClientOption);
        mapLocationClient.startLocation();
    }

    public void registerDevice(View view) {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance().getmOkHttpClient();
        RequestBody body = new FormBody.Builder()
                .addEncoded("deviceID", deviceIdText.getText().toString())
                .addEncoded("locX", locX + "")
                .addEncoded("locY", locY + "")
                .build();
        final Request request = new Request.Builder()
                .url("http://localhost:8000")
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            Message message = new Message();
            @Override
            public void onFailure(Call call, IOException e) {
                message.what = REG_FAIL;
                e.printStackTrace();
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    if(result.getString("error").equals("0")) {
                        message.what = REG_SUCCESS;
                    } else {
                        message.what = REG_FAIL;
                    }
                } catch (JSONException e) {
                    message.what = REG_FAIL;
                    e.printStackTrace();
                }
                handler.sendMessage(message);
            }
        });
    }
}

