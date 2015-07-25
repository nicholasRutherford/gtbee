package com.beeminder.gtbee.auth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.beeminder.gtbee.R;

import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;

/* Uses Scribe, but it turned out to be completely uneeded. Can switch to a simpler
    HTTP request function if you like.
 */
public class OauthActivity extends Activity {

    public static final String PREF_ACCESS_TOKEN = "com.beeminder.gtbee.access_token";
    public static final String PREF_NAME = "com.beeminder.gtbee.prefs";
    public static final String PREF_FREEBIES = "com.beeminder.gtbee.freebies";

    private static final String CALLBACK_URL = "http://localhost";

    private WebView mWebView;
    private OAuthService mOauthService;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("oauth", "OAuth started!");
        super.onCreate(savedInstanceState);

        mOauthService = new ServiceBuilder()
                .provider(BeeminderApi.class)
                .apiKey("ij0m0ojifr509tuq52936c4hgm8nw17")
                .apiSecret("5ij45fi98di0qh71ry773e1mxzk8hmz")
                .callback(CALLBACK_URL)
                .build();

        setContentView(R.layout.activity_oauth);


        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);

        startAuthorize();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return mOauthService.getAuthorizationUrl(null);
            }

            @Override
            protected void onPostExecute(String url) {
                mWebView.loadUrl(url);
            }
        }).execute();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.v("oauth", url);
            if ((url != null) && (url.startsWith(CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                Uri uri = Uri.parse(url);

                String accessToken = uri.getQueryParameter("access_token");

                SharedPreferences settings = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(PREF_ACCESS_TOKEN, accessToken);
                editor.putInt(PREF_FREEBIES, 5);
                editor.commit();
                finish();

/*
                        (new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.beeminder.com/api/v1/users/me.json" + "?access_token=" + params[0]);
                                Response response = request.send();
                                return response.getBody();
                            }

                            @Override
                            protected void onPostExecute(String results) {
                                // AccessToken is passed here! Do what you want!

                                Log.v("oauth", results);
                                finish();
                            }

                        }).execute(accessToken);*/
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };
}
