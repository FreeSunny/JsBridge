package pub.js.bridge.webview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import pub.js.bridge.app.AppProfile;


/**
 */
public class JsApi {

    public static final int JS_CALL = 0x1;

    public static final String JS_BIMA_JS = "js/bima.js";

    public static String bimaJs;

    private Handler handler;

    private WebView webView;

    public JsApi(Handler handler, WebView webView) {
        this.handler = handler;
        this.webView = webView;
    }

    @Keep
    @JavascriptInterface
    public void call(String params) {
        if (handler == null || TextUtils.isEmpty(params)) {
            return;
        }
        Message message = handler.obtainMessage(JS_CALL);
        try {
            JSONObject jsonObject = new JSONObject(params);
            String method = jsonObject.optString("methodName");
            String data = jsonObject.optString("data");
            String callbackId = jsonObject.optString("callbackId");
            message.obj = new JSMessage(method, data, callbackId);
            message.sendToTarget();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getBimaJs() {
        if (TextUtils.isEmpty(bimaJs)) {
            bimaJs = loadParseJS(AppProfile.getContext(), JS_BIMA_JS);
        }
        return bimaJs;
    }

    public static String loadParseJS(Context context, String jsName) {
        String js = "";
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getResources().getAssets().open(jsName)));

            String line;
            while ((line = reader.readLine()) != null) {
                js += line + "\n";
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return js;
    }
}
