package com.dessert.mojitomanager;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lawrence on 2016/11/29.
 */
public class OkHttpUtil {
    private OkHttpClient mOkHttpClient;
    private static OkHttpUtil mOkHttpUtil;
    public final static String DOMAIN = "http://192.168.50.183:8082/Mojito/";
    private OkHttpUtil() {
        mOkHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                }).build();
    }
    public static OkHttpUtil getInstance() {
        if(mOkHttpUtil == null) {
            mOkHttpUtil = new OkHttpUtil();
        }
        return mOkHttpUtil;
    }
    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }
}
