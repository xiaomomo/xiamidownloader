package com.youyu.xiamidownloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.youyu.xiamidownloader.R;
import com.youyu.xiamidownloader.domain.UserLoginInfo;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: youyu.lqj
 * Date: 14-2-11
 * Time: 下午4:51
 */
public class LoginActivity extends Activity {

    private static final String doubanLoginUrl = "http://www.douban.com/j/app/login";
    private ProgressBar pbLogin;
    private WebView webview;
    private Toast failLoginToast;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        webview = (WebView) findViewById(R.id.loginWebView);
        pbLogin = (ProgressBar) findViewById(R.id.pbLogin);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void loginDouban(String email, String password) {
                new LoginTask(email, password).execute();
            }
        }, "LoginServiceForJs");
        webview.loadUrl("file:///android_asset/h5/html/login.html");
        failLoginToast = Toast.makeText(this, "登录失败，重新登录", Toast.LENGTH_SHORT);

    }

    private UserLoginInfo getParamsByLogin(HttpClient client, String email, String password)
            throws IOException, JSONException {
        HttpPost post = new HttpPost(doubanLoginUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("version", "608"));
        params.add(new BasicNameValuePair("client",
                "s:mobile|y:android 4.1.1|f:608|m:Google|d:-1178839463|e:google_galaxy_nexus"));
        params.add(new BasicNameValuePair("app_name", "radio_android"));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("from", "android_608_Google"));
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response
                .getEntity().getContent()));
        StringBuilder sbLogin = new StringBuilder();
        String loginLine = "";
        while ((loginLine = rd.readLine()) != null) {
            sbLogin.append(loginLine);
        }
        if (!sbLogin.toString().contains("token")) {
            return null;
        }
        Document document = Jsoup.parse(sbLogin.toString());
        String body = document.body().html();
        String bodyJson = body.replace("&quot;", " ");
        JSONObject jsonObject = new JSONObject(bodyJson);
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setToken(jsonObject.getString("token"));
        userLoginInfo.setUserId(jsonObject.getString("user_id"));
        userLoginInfo.setExpire(jsonObject.getString("expire"));
        userLoginInfo.setEmail(email);
        userLoginInfo.setPassword(password);
        return userLoginInfo;
    }

    private void saveUserLoginInfo(UserLoginInfo userLoginInfo) {
        SharedPreferences userLoginInfoPre = getSharedPreferences("userLoginInfo", 0);
        SharedPreferences.Editor editor = userLoginInfoPre.edit();
        editor.putString("email", userLoginInfo.getEmail());
        editor.putString("password", userLoginInfo.getPassword());
        editor.putString("userId", userLoginInfo.getUserId());
        editor.putString("token", userLoginInfo.getToken());
        editor.putString("expire", userLoginInfo.getExpire());
        editor.commit();
    }

    private class LoginTask extends AsyncTask<URL, Integer, Integer> {
        private String email;
        private String password;

        public LoginTask(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected Integer doInBackground(URL... params) {
            HttpClient client = new DefaultHttpClient();
            UserLoginInfo userLoginInfo = null;
            try {
                publishProgress(0);
                userLoginInfo = getParamsByLogin(client, email, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null == userLoginInfo) {
                publishProgress(1, -1);
            } else {
                saveUserLoginInfo(userLoginInfo);
                publishProgress(1, 1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] == 0) {
                webview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                pbLogin.setVisibility(View.VISIBLE);
            } else if (values[0] == 1) {
                webview.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                pbLogin.setVisibility(View.INVISIBLE);
                if (values[1] == -1) {
                    //登录失败
                    failLoginToast.show();
                } else {
                    //FIXME 要再登录一次百度
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        }

    }

}


