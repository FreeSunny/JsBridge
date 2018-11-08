package pub.js.bridge.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class BIMAWebView extends WebView {

    public BIMAWebView(Context context) {
        super(context);
        init();
    }

    public BIMAWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BIMAWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void loadJsBridge(int newProgress) {
        if (newProgress > 95) {// 避免多次调用
            evaluateJavascript(JsApi.getBimaJs(), null);
        }
    }

    public interface JsCallback {
        void onHandle(JSMessage message);
    }

    private JsApi jsApi;

    private JsCallback jsCallback;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSMessage message = (JSMessage) msg.obj;
            if (jsCallback != null) {
                jsCallback.onHandle(message);
            }
        }
    };

    public void setJsCallback(JsCallback jsCallback) {
        this.jsCallback = jsCallback;
    }

    @SuppressLint("JavascriptInterface")
    private void init() {
        jsApi = new JsApi(handler, this);
        addJavascriptInterface(jsApi, "WVJBInterface");
    }

    public void native2Web(String callbackId, JSONObject response) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("callbackId", callbackId);
            jsonObject.putOpt("data", response);
            evaluateJavascript(String.format("WVJBridge._callback(%s)", jsonObject.toString()), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
