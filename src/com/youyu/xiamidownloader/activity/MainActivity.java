package com.youyu.xiamidownloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.youyu.xiamidownloader.R;
import com.youyu.xiamidownloader.domain.UserLoginInfo;
import com.youyu.xiamidownloader.util.XiamiAlbumParser;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        UserLoginInfo userLoginInfo = getUserLoginInfo();
        boolean loginEd = !StringUtils.isBlank(userLoginInfo.getToken());
        if (!loginEd) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        WebView webview = (WebView) findViewById(R.id.mainWebView);
        webview.getSettings().setJavaScriptEnabled(true);

        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String getAlbum(final String albumId) {
                String albumResult = null;
                FutureTask<List<String>> futureTask = new FutureTask<List<String>>(new Callable<List<String>>() {
                    @Override
                    public List<String> call() throws Exception {
                        try {
                            return XiamiAlbumParser.getSongListById(albumId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new ArrayList<String>();
                    }
                });
                new Thread(futureTask).run();
                try {
                    List<String> songList = futureTask.get();
                    JSONArray jsonArray = new JSONArray(songList);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("albumList", jsonArray);
                    albumResult = jsonObject.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return StringUtils.isEmpty(albumResult) ? "" : albumResult;
            }
        }, "MainJS");
        webview.loadUrl("file:///android_asset/h5/index.html");
    }

    private UserLoginInfo getUserLoginInfo() {
        SharedPreferences userLoginInfoPre = getSharedPreferences("userLoginInfo", 0);
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setEmail(userLoginInfoPre.getString("email", ""));
        userLoginInfo.setPassword(userLoginInfoPre.getString("password", ""));
        userLoginInfo.setUserId(userLoginInfoPre.getString("userId", ""));
        userLoginInfo.setToken(userLoginInfoPre.getString("token", ""));
        userLoginInfo.setExpire(userLoginInfoPre.getString("expire", ""));
        return userLoginInfo;
    }
}
