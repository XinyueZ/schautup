package android.support.v7.app;

import android.os.Bundle;
import android.view.Window;

class ActionBarActivityDelegateCompatHC extends ActionBarActivityDelegateCompatBase {

    ActionBarActivityDelegateCompatHC(ActionBarPreferenceActivity activity) {
        super(activity);
    }

    @Override
    void onCreate(Bundle savedInstanceState) {
        /**
         * A native Action Mode could be displayed (text selection, etc) so we need to make sure it
         * is positioned correctly. Here we request the ACTION_MODE_OVERLAY feature so that it
         * displays over the compat Action Bar.
         * {@link android.support.v7.internal.widget.NativeActionModeAwareLayout} is responsible for
         * making sure that the compat Action Bar is visible when an Action Mode is started
         * (for positioning).
         */
        mActivity.getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        super.onCreate(savedInstanceState);
    }

    @Override
    public ActionBar createSupportActionBar() {
        ensureSubDecor();
        return new ActionBarImplCompatHC(mActivity, mActivity);
    }
}
