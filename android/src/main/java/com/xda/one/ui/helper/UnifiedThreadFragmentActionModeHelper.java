package com.xda.one.ui.helper;

import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.constants.XDAConstants;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.ui.UnifiedThreadAdapter;
import com.xda.one.util.AccountUtils;

import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

public class UnifiedThreadFragmentActionModeHelper
        extends ActionModeHelper.RecyclerViewActionModeCallback {

    private final Context mContext;

    private final ThreadClient mThreadClient;

    private ActionModeHelper mModeHelper;

    private UnifiedThreadAdapter mAdapter;

    private ShareActionProvider mShareActionProvider;

    private MenuItem mSubscribeItem;

    public UnifiedThreadFragmentActionModeHelper(final Context context,
            final ThreadClient threadClient) {
        mContext = context;
        mThreadClient = threadClient;
    }

    public void setAdapter(final UnifiedThreadAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.thread_fragment_cab, menu);

        // Locate MenuItem with ShareActionProvider
        final MenuItem shareMenuItem = menu.findItem(R.id.thread_fragment_cab_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) shareMenuItem.getActionProvider();

        // Get the subscribed menu item
        mSubscribeItem = menu.findItem(R.id.thread_fragment_cab_subscribe);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
        if (mModeHelper.getCheckedItemCount() == 1) {
            updateShareIntent();

            final boolean visible = AccountUtils.isAccountAvailable(mContext);
            mSubscribeItem.setVisible(visible);
            if (visible) {
                final boolean subscribed = getCheckedThread().isSubscribed();
                mSubscribeItem.setIcon(subscribed
                        ? R.drawable.ic_action_star
                        : R.drawable.ic_action_star_outline);
            }
        }
        return true;
    }

    private void updateShareIntent() {
        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getCheckedThread().getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, XDAConstants.XDA_FORUM_URL +
                getCheckedThread().getWebUri());
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
    }

    public AugmentedUnifiedThread getCheckedThread() {
        return mAdapter.getThread(mModeHelper.getCheckedPositions().get(0));
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.thread_fragment_cab_subscribe:
                mThreadClient.toggleSubscribeAsync(getCheckedThread());
                break;
        }
        actionMode.finish();
        return true;
    }

    @Override
    public void onCheckedStateChanged(final ActionMode actionMode, final int position,
            final boolean isNowChecked) {
        actionMode.invalidate();
    }

    public void setModeHelper(final ActionModeHelper modeHelper) {
        mModeHelper = modeHelper;
    }
}
