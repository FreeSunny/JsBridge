package pub.js.bridge.webview;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import pub.js.bridge.util.LogUtil;

/**
 */
public class JSMessage {

    public static final String TAG = "JSMessage";

    private String method;

    /**
     * json
     */
    private String data;

    private String callbackId;

    public JSMessage(String method, String data, String callbackId) {
        this.method = method;
        this.data = data;
        this.callbackId = callbackId;
    }

    public String getMethod() {
        return method;
    }

    public JSONObject getData() {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "js message data error");
        } catch (Exception e) {
            LogUtil.e(TAG, "js message data error");
        }
        return null;
    }

    public String getCallbackId() {
        return callbackId;
    }
}
