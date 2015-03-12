package com.kunboy.weiboclient.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

	private CustomScrollListener mScrollListener;
	private float scrollX = -1, scrollY = -1;
	private boolean notified = true;
    private boolean mMeasured = false;
    private boolean mPreMeasureRefreshing = false;

	public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {

		return super.onTouchEvent(arg0);
	}

    @Override
    public void setRefreshing(boolean refreshing) {
        if (mMeasured) {
            super.setRefreshing(refreshing);
        } else {
            mPreMeasureRefreshing = refreshing;
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mMeasured) {
            mMeasured = true;
            setRefreshing(mPreMeasureRefreshing);
        }
    }

    @Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
			scrollY = arg0.getY();
			Log.d("kunboy", "action down:" + scrollY);
			notified = false;
		}
		if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
			if (!notified) {
				if (mScrollListener != null) {
					Log.d("kunboy", "action move:" + scrollY);
					float y = arg0.getY();
					Log.d("kunboy", "action move  YYYYY:" + y);
					if (y - scrollY < -180) {
						Log.d("kunboy", "通知上滑");
						mScrollListener.onScroll(true);
						notified = true;
					} else if (y - scrollY > 180) {
						Log.d("kunboy", "通知下滑");
						mScrollListener.onScroll(false);
						notified = true;
					}
				}
			}
		}
		return super.onInterceptTouchEvent(arg0);
	}


	@Override
	public boolean canChildScrollUp() {
		return super.canChildScrollUp();
	}

	public interface CustomScrollListener {
		public void onScroll(boolean scrollUp);
	}

	public void setCustomScrollListener(CustomScrollListener listener) {
		this.mScrollListener = listener;
	}
}
