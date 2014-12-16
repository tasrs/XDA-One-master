package com.xda.one.ui.helper;

import com.xda.one.ui.widget.XDALinerLayoutManager;

import android.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class QuickReturnHelper {

    private static final int ANIMATION_DURATION_MILLIS = 400;

    private final View mQuickReturnView;

    private final ActionBar mActionBar;

    private SparseArray<QuickReturnOnScroll> mScrollSparseArray = new
            SparseArray<>();

    private int mActionBarHeight;

    private int mPosition;

    private int mQuickReturnHeight;

    private boolean mHeaderVisible = true;

    private int mTouchSlop;

    public QuickReturnHelper(final View quickReturnView, final ActionBar actionBar) {
        mQuickReturnView = quickReturnView;
        mActionBar = actionBar;

        final ViewConfiguration vc = ViewConfiguration.get(actionBar.getThemedContext());
        mTouchSlop = vc.getScaledTouchSlop();

        mActionBarHeight = mActionBar.getHeight();
        if (quickReturnView == null) {
            return;
        }
        mHeaderVisible = mQuickReturnView.getVisibility() == View.VISIBLE;
        quickReturnView.post(new Runnable() {
            @Override
            public void run() {
                mQuickReturnHeight = mQuickReturnView.getHeight();
                mActionBarHeight = mActionBar.getHeight();
            }
        });
    }

    public static void postPaddingToQuickReturn(final ActionBar actionBar,
            final View quickReturnView, final View content) {
        quickReturnView.post(new Runnable() {
            @Override
            public void run() {
                final int paddingLeft = content.getPaddingLeft();
                final int paddingTop = content.getPaddingTop();
                final int paddingRight = content.getPaddingRight();
                final int paddingBottom = content.getPaddingBottom();
                content.setPadding(paddingLeft, quickReturnView.getHeight() + actionBar.getHeight()
                        + paddingTop, paddingRight, paddingBottom);
            }
        });
    }

    public void setOnScrollListener(final RecyclerView recyclerView, final int position) {
        final XDALinerLayoutManager linerLayoutManager = (XDALinerLayoutManager) recyclerView
                .getLayoutManager();
        final QuickReturnOnScroll onScroll = new QuickReturnOnScroll(position, linerLayoutManager);
        mScrollSparseArray.put(position, onScroll);
        recyclerView.setOnScrollListener(onScroll);
    }

    public void showTopBar() {
        if (mActionBar.isShowing()) {
            return;
        }
        mActionBar.show();
        if (mHeaderVisible || mQuickReturnView == null) {
            return;
        }
        animateToExpanding();
        mHeaderVisible = true;
    }

    public void hideTopBar() {
        if (!mActionBar.isShowing()) {
            return;
        }
        mActionBar.hide();
        if (!mHeaderVisible || mQuickReturnView == null) {
            return;
        }
        animateToOffScreen();
        mHeaderVisible = false;
    }

    private void animateToOffScreen() {
        final TranslateAnimation anim = new TranslateAnimation(0, 0, 0,
                -mQuickReturnHeight - mActionBarHeight);
        startAnimation(anim);
        mQuickReturnView.setVisibility(View.INVISIBLE);
    }

    private void animateToExpanding() {
        final TranslateAnimation anim = new TranslateAnimation(0, 0,
                -mQuickReturnHeight - mActionBarHeight, 0);
        startAnimation(anim);
        mQuickReturnView.setVisibility(View.VISIBLE);
    }

    private void startAnimation(final Animation anim) {
        anim.setDuration(ANIMATION_DURATION_MILLIS);
        mQuickReturnView.startAnimation(anim);
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    private class QuickReturnOnScroll implements RecyclerView.OnScrollListener {

        private final int mFragmentPosition;

        private final XDALinerLayoutManager mLinerLayoutManager;

        private QuickReturnOnScroll(final int fragmentPosition,
                final XDALinerLayoutManager linerLayoutManager) {
            mFragmentPosition = fragmentPosition;
            mLinerLayoutManager = linerLayoutManager;
        }

        private void goingUp() {
            showTopBar();
        }

        private void goingDown() {
            hideTopBar();
        }

        @Override
        public void onScrollStateChanged(int newState) {
        }

        @Override
        public void onScrolled(final int dx, final int dy) {
            if (mPosition != mFragmentPosition) {
                return;
            }

            if (mLinerLayoutManager.isListEnd()) {
                showTopBar();
            } else if (dy - mTouchSlop > 0) {
                goingDown();
            } else if (dy + mTouchSlop < 0) {
                goingUp();
            }
        }
    }
}