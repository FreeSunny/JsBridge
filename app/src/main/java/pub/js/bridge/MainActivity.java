package pub.js.bridge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import pub.js.bridge.constant.Servers;
import pub.js.bridge.fragment.WebViewFragment;
import pub.js.bridge.util.WindowUtil;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private WebViewFragment fragment;

    protected final void replaceFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(containerId, fragment).commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowUtil.compactSetStatusBarTransparent(this);
        super.onCreate(savedInstanceState);
        WindowUtil.compactSetToolbarTransparent(this);
        setContentView(R.layout.activity_main);
        fragment = WebViewFragment.newInstance(Servers.getUrl());
        replaceFragment(R.id.web_view_container, fragment);
    }

    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.onBackPressed()) {
            // do nothing
            return;
        }
        super.onBackPressed();
    }
}
