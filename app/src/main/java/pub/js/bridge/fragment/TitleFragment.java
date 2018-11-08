package pub.js.bridge.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pub.js.bridge.R;
import pub.js.bridge.util.ToolbarOptions;

public abstract class TitleFragment extends Fragment {

    public static final int MAX_TITLE = 11;

    private Toolbar toolbar;

    private TextView titleView;

    private TextView leftAction;

    private TextView rightAction;

    public void setToolBar(int toolBarId, ToolbarOptions options) {
        toolbar = getView().findViewById(toolBarId);
        // fix me !!!
        if (toolbar == null) {
            return;
        }
        leftAction = toolbar.findViewById(R.id.left_action);
        titleView = toolbar.findViewById(R.id.custom_title);
        rightAction = toolbar.findViewById(R.id.right_action);

        if (!TextUtils.isEmpty(options.leftText)) {
            leftAction.setText(options.leftText);
        }
        if (options.hideNavIcon) {
            leftAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        titleView.setText(options.title);
        if (options.isNavigate) {
            leftAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackAction();
                }
            });
        }
        rightAction.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, options.rightIcon, 0);
        rightAction.setText(options.rightText);
        rightAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightAction();
            }
        });
    }

    /**
     * maybe subclass special handle
     */
    protected void onBackAction() {
        getActivity().onBackPressed();
    }

    public void setTitle(String title) {
        if (title == null) {
            return;
        }
        if (title.length() > MAX_TITLE) {
            title = title.substring(0, MAX_TITLE - 1);
            title = title + "...";
        }
        titleView.setText(title);
    }

    public void setRightActionEnable(boolean enable) {
        if (rightAction != null) {
            rightAction.setEnabled(enable);
        }
    }

    public TextView getTitleView() {
        return titleView;
    }

    public TextView getRightAction() {
        return rightAction;
    }

    public TextView getLeftAction() {
        return leftAction;
    }

    public Toolbar getToolBar() {
        return toolbar;
    }

    public void showToolbar() {
        getToolBar().setVisibility(View.VISIBLE);
    }

    public void hideToolbar() {
        getToolBar().setVisibility(View.GONE);
    }

    public void onRightAction() {

    }
}
