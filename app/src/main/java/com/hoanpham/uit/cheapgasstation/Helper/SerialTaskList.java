package com.hoanpham.uit.cheapgasstation.Helper;

import android.os.Handler;

import java.util.LinkedList;

public class SerialTaskList implements SerialTask.Listener {

    private long mTaskTransitionDelayMillis;

    //////

    private LinkedList<SerialTask> mTasks = new LinkedList<>();
    private SerialTask mRunningTask = null;

    private boolean mIsRunning = false;

    //////

    public SerialTaskList(long taskTransitionDelayMillis) {
        mTaskTransitionDelayMillis = taskTransitionDelayMillis;
    }

    //////
    // on Android we need to handle pausing/resuming
    // this is different from iOS
    //////

    public void resume() {
        mIsRunning = true;

        if (mRunningTask != null) {
            mRunningTask.begin();
        } else {
            // no delay
            tryBeginOneTask(0);
        }
    }

    public void pause() {
        mIsRunning = false;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    //////

    public int getNumTasks() {
        return mTasks.size();
    }

    public void addTask(SerialTask task) {
        task.setListener(this);
        mTasks.add(task);

        // no delay
        tryBeginOneTask(0);
    }

    public void addTask(SerialTask task, int index) {
        task.setListener(this);
        mTasks.add(index, task);

        // no delay
        tryBeginOneTask(0);
    }

    //////

    private void tryBeginOneTask(long delayMillis) {
        if (!mIsRunning)
            return;

        if (mRunningTask != null) {
            // a task is already running
            return;
        }

        if (!mTasks.isEmpty()) {
            mRunningTask = mTasks.removeFirst();

            if (delayMillis <= 0) {
                // no delay
                mRunningTask.begin();
            } else {
                final SerialTask capturedTask = mRunningTask;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (capturedTask == mRunningTask)
                            mRunningTask.begin();
                    }
                }, delayMillis);
            }
        }
    }

    private void tryEndRunningTask() {
        if (mRunningTask == null)
            return;

        mRunningTask.setListener(null);
        mRunningTask.setRunnable(null);
        mRunningTask = null;
    }

    //////
    // SerialTask.Listener
    //////

    @Override
    public void serialTaskDidEnd(SerialTask serialTask) {
        if (mRunningTask == serialTask)
            tryEndRunningTask();

        // transitioning between tasks => delay a little
        tryBeginOneTask(mTaskTransitionDelayMillis);
    }
}
