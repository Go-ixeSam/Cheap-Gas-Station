package com.hoanpham.uit.cheapgasstation.Helper;

import android.os.Handler;

public class SerialTask {

    public interface Listener {
        void serialTaskDidEnd(SerialTask serialTask);
    }

    //////

    // this is just for debugging purpose
    public String taskID = null;

    private Listener mListener = null;
    private Runnable mRunnable = null;

    private long mBeginDelayMillis = 0;

    //////

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setRunnable(Runnable runnable) {
        mRunnable = runnable;
    }

    public long getBeginDelayMillis() {
        return mBeginDelayMillis;
    }

    public void setBeginDelayMillis(long beginDelayMillis) {
        mBeginDelayMillis = beginDelayMillis;
    }

    //////

    public void begin() {
        // here the task needs to have mRunnable
        // otherwise let it crash so we know and fix

        if (mBeginDelayMillis <= 0) {
            mRunnable.run();
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(mRunnable, mBeginDelayMillis);
        }
    }

    public void end() {
        if (mListener != null)
            mListener.serialTaskDidEnd(this);
    }
}
