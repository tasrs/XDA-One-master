package com.xda.one.ui.listener;

import com.xda.one.ui.widget.XDALinerLayoutManager;

import android.support.v7.widget.RecyclerView;

public class RecyclerEndHelper implements RecyclerView.OnScrollListener {

    private final Callback mCallback;

    private XDALinerLayoutManager mLayoutManager;

    private boolean mListEnd;

    private int mCurrentScrollState;

    public RecyclerEndHelper(final RecyclerView recyclerView, final Callback callback) {
        mCallback = callback;
        updateRecyclerView(recyclerView);
    }

    private void isScrollCompleted() {
        if (mCurrentScrollState == RecyclerView.SCROLL_STATE_IDLE && mListEnd) {
            mCallback.onListEndReached();
        }
    }

    @Override
    public void onScrollStateChanged(final int newState) {
        mCurrentScrollState = newState;
        isScrollCompleted();
    }

    @Override
    public void onScrolled(final int dx, final int dy) {
        mListEnd = mLayoutManager.isListEnd();
    }

    public void updateRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = (XDALinerLayoutManager) recyclerView.getLayoutManager();
    }

    public interface Callback {

        public void onListEndReached();
    }
}
