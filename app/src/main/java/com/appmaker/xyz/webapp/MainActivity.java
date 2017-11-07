package com.appmaker.xyz.webapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


import java.io.File;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class MainActivity extends ActionBarActivity {
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    boolean isError= false;
    WebView myWebView;
    public SmoothProgressBar smoothProgressBar;
    CircularProgressBar progDet;
    private long lastBackPressTime = 0;
    private final static int FILECHOOSER_RESULTCODE=1;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    ValueCallback<Uri[]> mfilePathCallback;
    public static final int REQUEST_SELECT_FILE = 100;
    //String url = "http://mobilerewards.io/mobile/index.php?id=b51d224b833c4b0aaaa0b165ec6c6a0f";
    String url =Settings.URL;
    String TAG = "WV";
    private View mCustomView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    RelativeLayout rlErrorContainer;
    Button retryButton;
    Context c;
    String retryUrl = url;
    private Uri mCapturedImageURI = null;
    private InterstitialAd mInterstitialAd;

    //  SwipeRefreshLayout swipeContainer;
    //private WebView mWebviewPop;
   // private FrameLayout mContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG,"onCreate called");
        c = getApplicationContext();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        if(Settings.ADMOBNEABLED){
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-8406133744734358~1572786723");

           // mAdView.setVisibility(View.VISIBLE);
           // mAdView.setAdUnitId(c.getString(R.string.banner_ad_unit_id));
           // AdRequest adRequest = new AdRequest.Builder().build();
            //mAdView.loadAd(adRequest);
            if(Settings.INTERSITILIAL_ADS) {
                mInterstitialAd = new InterstitialAd(this);
                mInterstitialAd.setAdUnitId("ca-app-pub-8406133744734358/6633541718");
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        Log.e("Ads", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                        Log.e("Ads", "onAdFailedToLoad");
                    }

                    @Override
                    public void onAdOpened() {
                        // Code to be executed when the ad is displayed.
                        Log.e("Ads", "onAdOpened");
                    }

                    @Override
                    public void onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                        Log.e("Ads", "onAdLeftApplication");
                    }

                    @Override
                    public void onAdClosed() {
                        // Code to be executed when when the interstitial ad is closed.
                        Log.e("Ads", "onAdClosed");
                    }
                });
            }
            }else {
            //mAdView.destroy();
        }
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        if(Settings.ENABLE_TOOLBAR)
            appBarLayout.setVisibility(View.VISIBLE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(c.getResources().getColor(R.color.titleColor));

        //black
       // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        //white
       // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        //red
      //  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));
   //  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2196F3")));
       // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

          // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03A9F4")));
        progDet = (CircularProgressBar) findViewById(R.id.progDet);
        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.progressBarView);
        rlErrorContainer = (RelativeLayout) findViewById(R.id.errorCont);
        rlErrorContainer.setVisibility(View.GONE);
        myWebView = (WebView) findViewById(R.id.webview);
        customViewContainer = (FrameLayout) findViewById(R.id.customViewContainer);
        retryButton = (Button) findViewById(R.id.retryButton);
      //  mContainer = (FrameLayout) findViewById(R.id.webCont);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWebView.clearView();
            myWebView.loadUrl(retryUrl);
                Log.e(TAG,"isnide retry click loading url  = "+retryUrl);

            }
        });
        if(getIntent().hasExtra("url")) {
            url  = getIntent().getStringExtra("url");
            Log.e(TAG,"getExtra. function  url  = "+url);

            if(myWebView!=null){
                myWebView.clearView();
                myWebView.loadUrl(url);
            }
        }
     /*   swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeEventI);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String currentUrl = myWebView.getUrl();
                myWebView.loadUrl(currentUrl);
            }
        });*/
       // new FinestWebView.Builder(this).show(url);
        //myWebView.loadUrl("http://www.mystmobile.com/instarefill/#home");
        myWebView.setInitialScale(0);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setAllowContentAccess(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        myWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
     //   myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setDatabaseEnabled(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setGeolocationEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.loadUrl(url);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewhandler());
        myWebView.setWebChromeClient(new WebChromeClient() {

            private Bitmap mDefaultVideoPoster;
            private View mVideoProgressView;

            /*@Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            /*    mWebviewPop = new WebView(c);
                mWebviewPop.setVerticalScrollBarEnabled(false);
                mWebviewPop.setHorizontalScrollBarEnabled(false);
                mWebviewPop.setWebViewClient(new WebViewhandler());
                mWebviewPop.getSettings().setJavaScriptEnabled(true);
                mWebviewPop.getSettings().setSavePassword(false);
                mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mContainer.addView(mWebviewPop);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(mWebviewPop);
                resultMsg.sendToTarget();
                return true;*/
          //  }*/


            @Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
            }

            @Override
            public void onShowCustomView(View view,CustomViewCallback callback) {

                // if a view already exists then immediately terminate the new one
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomView = view;
                myWebView.setVisibility(View.GONE);
                customViewContainer.setVisibility(View.VISIBLE);
                customViewContainer.addView(view);
                customViewCallback = callback;
            }

            @Override
            public View getVideoLoadingProgressView() {

                if (mVideoProgressView == null) {
                    LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                    mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
                }
                return mVideoProgressView;
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
                if (mCustomView == null)
                    return;

                myWebView.setVisibility(View.VISIBLE);
                customViewContainer.setVisibility(View.GONE);

                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);

                // Remove the custom view from its container.
                customViewContainer.removeView(mCustomView);
                customViewCallback.onCustomViewHidden();

                mCustomView = null;
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.e("TAG","onShowFileChooser click");

                if (ContextCompat.checkSelfPermission(c,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG","onShowFileChooser permission granted ");




                    mfilePathCallback = filePathCallback;

                    try{

                        // Create AndroidExampleFolder at sdcard

                        File imageStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(
                                        Environment.DIRECTORY_PICTURES)
                                , "AndroidExampleFolder");

                        if (!imageStorageDir.exists()) {
                            // Create AndroidExampleFolder at sdcard
                            imageStorageDir.mkdirs();
                        }

                        // Create camera captured image file path and name
                        File file = new File(
                                imageStorageDir + File.separator + "IMG_"
                                        + String.valueOf(System.currentTimeMillis())
                                        + ".jpg");

                        mCapturedImageURI = Uri.fromFile(file);

                        // Camera capture image intent
                        final Intent captureIntent = new Intent(
                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        i.setType("image/*");

                        // Create file chooser intent
                        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                        // Set camera intent to file chooser
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                                , new Parcelable[] { captureIntent });

                        // On select image call onActivityResult method of activity
                        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                    }
                    catch(Exception e){
                        Toast.makeText(getBaseContext(), "Exception:"+e,
                                Toast.LENGTH_LONG).show();
                        return false;
                    }







                   /*
                    if (uploadMessage != null) {
                        uploadMessage.onReceiveValue(null);
                        uploadMessage = null;
                    }

                    uploadMessage = filePathCallback;

                    Intent intent = fileChooserParams.createIntent();
                    try {
                        startActivityForResult(intent, REQUEST_SELECT_FILE);
                    } catch (ActivityNotFoundException e) {
                        uploadMessage = null;
                        Log.e(TAG, "");
                        return false;
                    }
                    */

                }else{
                    Log.e("TAG","onShowFileChooser no permissions ");

                    askPermission();
                    return false;

                }
                return true;
            }


            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                Log.e(TAG, "inside open file chooser first");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                Log.e(TAG, "inside open file chooser second");
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                Log.e(TAG, "inside open file chooser third " + uploadMsg + " acce = " + acceptType + " capture = " + capture);
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                Log.d("tag","inside geolocation permision pop origin = "+origin);
                callback.invoke(origin, true, false);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d("tag","inside jsAlert"+ message);
                // This shows the dialog box.  This can be commented out for dev
                AlertDialog.Builder alertBldr = new AlertDialog.Builder(c);
                alertBldr.setMessage(message);
                alertBldr.setTitle("Alert");
                alertBldr.show();
                result.confirm();
                return true;
            }


        });

        myWebView.setDownloadListener(new DownloadListener() {

            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Log.e("tag","inside download listener");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }




    public void askPermission(){
        if (ContextCompat.checkSelfPermission(c,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("TAG","ask Permission method. no permission yet");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            Log.e("TAG","ask Permission method.  permission already granted");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        Log.e(TAG,"inside on activyt result req code = "+requestCode+" result code  = "+resultCode+" result ok code is  = "+ Activity.RESULT_OK+" file chooser result code is = "+FILECHOOSER_RESULTCODE);
     /*   if(requestCode==FILECHOOSER_RESULTCODE)
        {
           // if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                    : intent.getData();
            Log.e(TAG, "uri got is = " + result.toString()+"mUpload messafe = "+mUploadMessage);

//            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
            Log.e(TAG, "uri got is = " + result.toString());
        }*/
     /*   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Log.e(TAG," lollipop device  REQUEST_SELECT_FILE = "+REQUEST_SELECT_FILE);
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                Log.e(TAG,"upoad message  = "+uploadMessage);

                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            Log.e(TAG,"inside filechooser for lower sdk");
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
            Log.e(TAG,"inside lower sdk uri = "+result.toString());

            mUploadMessage.onReceiveValue(result);
            Log.e(TAG,"inside lower sdk msg = "+mUploadMessage);
            mUploadMessage = null;
        }
        else
            Log.e(TAG,"failed to upload file");*/


        if(requestCode==FILECHOOSER_RESULTCODE)
        {

            if (null == this.mfilePathCallback) {
                return;

            }

            Uri result=null;

            try{
                if (resultCode != RESULT_OK) {

                    result = null;

                } else {

                    // retrieve from the private variable if the intent is null
                    result = intent == null ? mCapturedImageURI : intent.getData();
                }
            }
            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "activity :"+e,
                        Toast.LENGTH_LONG).show();
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mfilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                mfilePathCallback = null;
            }else{
                Uri resultnew = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                Log.e(TAG,"inside lower sdk uri = "+result.toString());

                mUploadMessage.onReceiveValue(resultnew);
                Log.e(TAG,"inside lower sdk msg = "+mUploadMessage);
                mUploadMessage = null;
            }

        }

    }

    public class WebViewhandler extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e("tag", "webview load  error url = " + failingUrl+" desc = "+description);
            rlErrorContainer.setVisibility(View.VISIBLE);
            retryUrl = failingUrl;

            if(mInterstitialAd!=null)
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            //isError = true;
           // myWebView.loadUrl(failingUrl);
        }



        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setMessage("SSL certificate invalid, do you want to continue.?");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //Toast.makeText(getApplicationContext(), "Finished", Toast.LENGTH_LONG).show();
          //  smoothProgressBar.setVisibility(View.INVISIBLE);
          //  smoothProgressBar.progressiveStop();
            //progDet.setVisibility(View.GONE);
            //myWebView.setVisibility(View.VISIBLE);
           // rlErrorContainer.setVisibility(View.GONE);


            stopProgressLoading();
            if(mInterstitialAd!=null)
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            Log.e("tag","on page finished");
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //smoothProgressBar.applyStyle(R.style.GNowProgressBar);
            startProgressLoading();
            rlErrorContainer.setVisibility(View.GONE);
            Log.e("tag", "on page started");
           // smoothProgressBar.setSmoothProgressDrawableSpeed(2);
            //smoothProgressBar.setSmoothProgressDrawableProgressiveStartSpeed(2);
            //smoothProgressBar.setSmoothProgressDrawableProgressiveStopSpeed(8);
            //Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_LONG).show();
           // smoothProgressBar.setVisibility(View.VISIBLE);
           // smoothProgressBar.progressiveStart();
            if(mInterstitialAd!=null)
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.e("tag", "webview load  error");
            rlErrorContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("tag","url overrride url  = "+ url);
            if(mInterstitialAd!=null) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.e("ad", "ad not loaded;   ");
                }
            }else{
                Log.e("ad", "ad is null;   ");

            }
            if (Uri.parse(url).getScheme().equals("market")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    Activity host = (Activity) view.getContext();
                    host.startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    // Google Play app is not installed, you may want to open the app store link
                    Uri uri = Uri.parse(url);
                    view.loadUrl("http://play.google.com/store/apps/" + uri.getHost() + "?" + uri.getQuery());
                    return false;
                }

            } else if (url.startsWith("tel:")) {
                Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(tel);
                return true;
            }
            else if (url.startsWith("mailto:")) {
                String body = "Enter your Question, Enquiry or Feedback below:\n\n";
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.setType("application/octet-stream");
                mail.putExtra(Intent.EXTRA_EMAIL, new String[]{"email address"});
                mail.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                mail.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(mail);
                return true;
            }


        /*    String host = Uri.parse(url).getHost();
            Log.e("tag","url overrride url host = "+ host);
            //Toast.makeText(MainActivity.this, host,
            //Toast.LENGTH_SHORT).show();
            if (host.equals(Settings.URL)) {
                // This is my web site, so do not override; let my WebView load
                // the page
                Log.e("tag","url override url webviewpop = ");

                if (mWebviewPop != null) {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop = null;
                }
                return false;
            }

            if (host.contains("m.facebook.com") || host.contains("facebook.co")
                    || host.contains("google.co")
                    || host.contains("www.facebook.com")
                    || host.contains(".google.com")
                    || host.contains(".google.co")
                    || host.contains("accounts.google.com")
                    || host.contains("accounts.google.co.in")
                    || host.contains("www.accounts.google.com")
                    || host.contains("www.twitter.com")
                    || host.contains("secure.payu.in")
                    || host.contains("https://secure.payu.in")
                    || host.contains("oauth.googleusercontent.com")
                    || host.contains("content.googleapis.com")
                    || host.contains("ssl.gstatic.com")) {
                Log.e("tag","should url override.  social urls = "+host);
                return false;
            }

*/

            return false;
        }


    }


    @Override
    public void onBackPressed() {
        Toast toast = null;
        if(!myWebView.canGoBack()){
            if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
              //  toast = Toast.makeText(this, "Press again to close", Toast.LENGTH_LONG);
            //    toast.show();
                showSnackBarwithMessage("Press back again to exit!",true);
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                Log.e("wv","inside else sec");

                super.onBackPressed();
            }
        }else{
            if(myWebView!=null ){
                myWebView.goBack();

                Log.e("wv","inside go back pressed");
            }
        }
    }

    public void showSnackBarwithMessage(String msg,boolean boo){

        Snackbar snackbar = Snackbar
                .make(myWebView, msg, Snackbar.LENGTH_LONG);
        if(boo){
            snackbar.setAction("EXIT", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.exit(0);
                }
            });
        }

        snackbar.setActionTextColor(Color.RED);


                snackbar.show();
    }


    public void startProgressLoading(){
        if(Settings.PROGRESS_TYPE==0){
        progDet.setVisibility(View.VISIBLE);}
        else if(Settings.PROGRESS_TYPE==1){
            smoothProgressBar.setSmoothProgressDrawableSpeed(2);
            smoothProgressBar.setSmoothProgressDrawableProgressiveStartSpeed(2);
            smoothProgressBar.setSmoothProgressDrawableProgressiveStopSpeed(8);
             smoothProgressBar.setVisibility(View.VISIBLE);
            smoothProgressBar.progressiveStart();
        }
    }

    public void stopProgressLoading(){
       // if(swipeContainer.isRefreshing()){
      //      swipeContainer.setRefreshing(false);
      //  }
        if(Settings.PROGRESS_TYPE==0){
            progDet.setVisibility(View.GONE);}
        else if(Settings.PROGRESS_TYPE==1){
            smoothProgressBar.setVisibility(View.INVISIBLE);
              smoothProgressBar.progressiveStop();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        myWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume called ");

        myWebView.onResume();
        if(myWebView!=null){
            if(getIntent().hasExtra("url")) {
                url  = getIntent().getStringExtra("url");
                Log.e(TAG,"getExtra. function  url  = "+url);

                if(myWebView!=null){
                    myWebView.clearView();
                    myWebView.loadUrl(url);
                }
            }
        }else{
            Log.e(TAG,"webview null onResume. ");

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        myWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        myWebView.destroy();
        myWebView = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
