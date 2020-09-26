package com.hoanpham.uit.cheapgasstation.Helper;

public class AppState {
    private static final AppState ourInstance = new AppState();

    // 400 ms is enough time to account for most animations
    private static final SerialTaskList displaySerialTaskList = new SerialTaskList(400);

    private boolean mAppWasLaunchedFromPush = false;

    public static AppState getInstance() {
        return ourInstance;
    }

    public static SerialTaskList getDisplaySerialTaskList() {
        return displaySerialTaskList;
    }

    private AppState() {
        // nothing here
    }

    public boolean appWasLaunchedFromPush() {
        return mAppWasLaunchedFromPush;
    }

    public void setAppWasLaunchedFromPush(boolean value) {
        mAppWasLaunchedFromPush = value;
    }
}
