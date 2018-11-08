package pub.js.bridge.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pub.js.bridge.R;
import pub.js.bridge.constant.Extras;
import pub.js.bridge.constant.Servers;
import pub.js.bridge.util.ToastUtil;
import pub.js.bridge.util.ToolbarOptions;
import pub.js.bridge.webview.BIMAWebView;
import pub.js.bridge.webview.JSMessage;
import pub.js.bridge.webview.WebViewConfig;

/**
 */
public class WebViewFragment extends TitleFragment {

    public static final int REQUEST_CODE_FILE = 0x12;

    private String url;

    public BIMAWebView webView;

    private View loadFail;

    private ProgressBar loadProgress;

    private String title;

    private ValueCallback<Uri[]> valueCallback;

    private WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            WebViewFragment.this.title = title;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            webView.loadJsBridge(newProgress);
            if (newProgress == 100) {
                //setTitle(webView.getTitle());// set default
                loadProgress.setVisibility(View.GONE);
            } else {
                loadProgress.setVisibility(View.GONE);
                loadProgress.setProgress(newProgress);
            }
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            valueCallback = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            boolean hasType = false;

            if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null &&
                fileChooserParams.getAcceptTypes().length > 0) {
                for (String acceptType : fileChooserParams.getAcceptTypes()) {
                    if (!TextUtils.isEmpty(acceptType)) {
                        intent.setType(acceptType);
                        hasType = true;
                        break;
                    }
                }
            }
            if (!hasType) {
                intent.setType("image/*");
            }
            PackageManager pm = getActivity().getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(intent, -1);

            if (infos == null || infos.size() == 0) {
                if (valueCallback != null) {
                    valueCallback.onReceiveValue(null);
                }
                valueCallback = null;
                ToastUtil.showLongToast(getActivity(), "文件选择失败");
            }
            startActivityForResult(intent, REQUEST_CODE_FILE);
            return true;
        }
    };

    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadFail.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            loadFail.setVisibility(View.VISIBLE);
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            loadFail.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            loadFail.setVisibility(View.VISIBLE);
        }
    };

    public WebViewFragment() {
    }

    public static WebViewFragment newInstance(String url) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(Extras.URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public static WebViewFragment newInstance(Bundle args) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(Extras.URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        setViewsListener();
        initTitle();
        initWebView();
        loadUrl();
    }

    private void findViews() {
        webView = getView().findViewById(R.id.web_view);
        loadFail = getView().findViewById(R.id.load_fail);
        loadProgress = getView().findViewById(R.id.webview_content_progress);
    }

    private void setViewsListener() {
        loadFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void initTitle() {
        ToolbarOptions options = new ToolbarOptions();
        setToolBar(R.id.tool_bar, options);
        getLeftAction().setVisibility(View.GONE);
    }

    @Override
    public void onRightAction() {
        webView.loadUrl(Servers.getMainUrl());
    }

    BIMAWebView.JsCallback jsCallback = new BIMAWebView.JsCallback() {
        @Override
        public void onHandle(JSMessage message) {
            String method = message.getMethod();
            JSONObject data = message.getData();
            if (data == null) {
                data = new JSONObject();
            }
            if ("setTitle".equals(method)) {
                String title = data.optString("title");
                setTitle(title);
            } else if ("closeWindow".equals(method)) {
                getActivity().finish();
            } else if ("switchBg".equals(method)) {
                int i = data.optInt("type");
                if (i == 0) {
                    getActivity().getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);
                    //getView().getRootView().setBackgroundResource(R.drawable.splash_bg);
                } else {
                    getActivity().getWindow().setBackgroundDrawableResource(R.drawable.dark_bg);
                    //getView().getRootView().setBackgroundResource(R.drawable.dark_bg);
                }
            } else if ("rightAction".equals(method)) {
                String text = data.optString("text");
                getRightAction().setText(text);
            }
        }
    };

    private void initWebView() {
        WebViewConfig.setWebSettings(getContext(), webView.getSettings());
        webView.setBackgroundColor(0);
        webView.getBackground().setAlpha(0);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.setJsCallback(jsCallback);
    }


    /**
     * one result
     *
     * @param callbackId
     * @param key
     * @param value
     */
    private void native2WebOneResult(String callbackId, String key, String value) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOpt(key, value);
            webView.native2Web(callbackId, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * multi result
     *
     * @param callbackId
     * @param result
     */
    private void native2Web(String callbackId, JSONObject result) {
        webView.native2Web(callbackId, result);
    }

    private void loadUrl() {
        webView.loadUrl(url);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (webView != null) {
            webView.setJsCallback(null);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        getLeftAction().setVisibility(View.GONE);
        return false;
    }
}
