package vnu.uet.hanguyen.gfilm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WatchMoviesActivity extends AppCompatActivity implements UniversalVideoView.VideoViewCallback ,Constant{

    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private LinearLayout layoutVideo,layout_content;

    //facebook commments components

    private WebView mWebViewComments;
    private FrameLayout mContainer;
    private ProgressBar progressBar;
    boolean isLoading;
    private WebView mWebviewPop;
    private String postUrl;

    //const facebook comments
    public static final int NUMBER_OF_COMMENTS = 5;

    //dialog loading
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_movies);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải, xin vui lòng đợi ...");
        progressDialog.setCancelable(true); // Check as required
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Intent intent = getIntent();
        final String link_storage_phim = intent.getStringExtra("link_phim");
        final String url_phim = IP_SERVER+"/OnFilm/services/api_get_link_xem_phim.php?link="+link_storage_phim;
        postUrl = intent.getStringExtra("link_comments");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new JSON().execute(url_phim);
            }
        });

        initComponentsFacebookComments();
    }

    private void initComponentsFacebookComments() {
        mWebViewComments = (WebView) findViewById(R.id.commentsView);
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        setLoading(true);
        loadComments();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebViewComments.canGoBack()) {
                        mWebViewComments.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadComments() {
        mWebViewComments.setWebViewClient(new UriWebViewClient());
        mWebViewComments.setWebChromeClient(new UriChromeClient());
        mWebViewComments.getSettings().setJavaScriptEnabled(true);
        mWebViewComments.getSettings().setAppCacheEnabled(true);
        mWebViewComments.getSettings().setDomStorageEnabled(true);
        mWebViewComments.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebViewComments.getSettings().setSupportMultipleWindows(true);
        mWebViewComments.getSettings().setSupportZoom(false);
        mWebViewComments.getSettings().setBuiltInZoomControls(false);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebViewComments.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebViewComments, true);
        }

        // facebook comment widget including the article url
        String html = "<!doctype html> <html lang=\"en\"> <head></head> <body> " +
                "<div id=\"fb-root\"></div> <script>(function(d, s, id) { var js, fjs = d.getElementsByTagName(s)[0]; if (d.getElementById(id)) return; js = d.createElement(s); js.id = id; js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.6\"; fjs.parentNode.insertBefore(js, fjs); }(document, 'script', 'facebook-jssdk'));</script> " +
                "<div class=\"fb-comments\" data-href=\"" + postUrl + "\" " +
                "data-numposts=\"" + NUMBER_OF_COMMENTS + "\" data-order-by=\"reverse_time\">" +
                "</div> </body> </html>";

        mWebViewComments.loadDataWithBaseURL("http://35.187.247.104/OnFilm/comments/", html, "text/html", "UTF-8", null);
        mWebViewComments.setMinimumHeight(200);
    }

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;

        if (isLoading)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);

    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String host = Uri.parse(url).getHost();

            return !host.equals("m.facebook.com");

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String host = Uri.parse(url).getHost();
            setLoading(false);
            if (url.contains("/plugins/close_popup.php?reload")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        mContainer.removeView(mWebviewPop);
                        loadComments();
                    }
                }, 600);
            }

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            setLoading(false);
        }
    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(getApplicationContext());
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.setWebChromeClient(this);
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.getSettings().setDomStorageEnabled(true);
            mWebviewPop.getSettings().setSupportZoom(false);
            mWebviewPop.getSettings().setBuiltInZoomControls(false);
            mWebviewPop.getSettings().setSupportMultipleWindows(true);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
        }
    }

    public void initMedia(String link){
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);

        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);

        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoPath(link);
        mVideoView.start();
        mVideoView.setVideoViewCallback(this);

        layoutVideo = findViewById(R.id.layoutVideo);
        layout_content = findViewById(R.id.layout_content);
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        if(isFullscreen){
            layout_content.setVisibility(LinearLayout.GONE);
        }else{
            layout_content.setVisibility(LinearLayout.VISIBLE);
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {

    }

    class JSON extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params) {
            return docNoiDung_Tu_URL(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            // nhận link xem phim tại đây

            initMedia(s);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView != null){
            String package_name = "tagroup.thangducanh.com.onfilmcinema";
            SharedPreferences prefs = this.getSharedPreferences(package_name,MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            mVideoView.pause();
            int point_pause = mVideoView.getCurrentPosition();
            int point_end = mVideoView.getDuration();
            editor.putInt("point_pause",point_pause);
            editor.putInt("point_end",point_end);
            editor.commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoView != null){
            String package_name = "tagroup.thangducanh.com.onfilmcinema";
            SharedPreferences prefes = this.getSharedPreferences(package_name,MODE_PRIVATE);
            int point_start = prefes.getInt("point_pause",0);
            mVideoView.start();
            mVideoView.seekTo(point_start);
        }
    }

    private String docNoiDung_Tu_URL(String theUrl){
        StringBuilder content = new StringBuilder();
        try    {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)    {
            e.printStackTrace();
        }
        return content.toString();
    }

}
