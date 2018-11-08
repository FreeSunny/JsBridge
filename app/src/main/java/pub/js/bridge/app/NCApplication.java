package pub.js.bridge.app;

import android.app.Application;

/**
 */
public class NCApplication extends Application {

    static NCApplication app;

    public AppProfile appProfile;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AppProfile.context = this.getApplicationContext();
        appProfile = new AppProfile();
        appProfile.onCreate();
    }
}
