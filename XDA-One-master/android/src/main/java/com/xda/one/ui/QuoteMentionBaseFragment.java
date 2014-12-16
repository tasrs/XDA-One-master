package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.widget.XDALinerLayoutManager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public abstract class QuoteMentionBaseFragment extends Fragment {

    public static final String SAVED_ADAPTER_STATE = "saved_adapter_state";

    protected static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    // Infinite scrolling
    protected InfiniteRecyclerLoadHelper mLoadHelper;

    protected RecyclerView mRecyclerView;

    protected XDALinerLayoutManager mLayoutManager;

    protected ProgressBar mLoadMoreProgressBar;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile_recycler, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayoutManager = new XDALinerLayoutManager(getActivity());

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        // If the listener already exists then tell it about the new recycler view
        if (mLoadHelper != null) {
            mLoadHelper.updateRecyclerView(mRecyclerView);
        }

        mLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id.load_more_progress_bar);
    }
}
