package com.github.gfranks.minimal.notification;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

class GFMinimalNotificationManager {

    private static final int MSG_TIMEOUT = 0;

    private static final int SHORT_DURATION_MS = 1500;
    private static final int LONG_DURATION_MS = 2750;

    private static GFMinimalNotificationManager sManager;

    static GFMinimalNotificationManager getInstance() {
        if (sManager == null) {
            sManager = new GFMinimalNotificationManager();
        }
        return sManager;
    }

    private final Object mLock;
    private final Handler mHandler;

    private Record mCurrentNotification;
    private Record mNextNotification;

    private GFMinimalNotificationManager() {
        mLock = new Object();
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_TIMEOUT:
                        handleTimeout((Record) message.obj);
                        return true;
                }
                return false;
            }
        });
    }

    public void show(int duration, Callback callback) {
        synchronized (mLock) {
            if (isCurrentNotificationLocked(callback)) {
                // Means that the callback is already in the queue. We'll just update the duration
                mCurrentNotification.duration = duration;

                // If this is the GFNotification currently being shown, call re-schedule it's
                // timeout
                mHandler.removeCallbacksAndMessages(mCurrentNotification);
                scheduleTimeoutLocked(mCurrentNotification);
                return;
            } else if (isNextNotificationLocked(callback)) {
                // We'll just update the duration
                mNextNotification.duration = duration;
            } else {
                // Else, we need to create a new record and queue it
                mNextNotification = new Record(duration, callback);
            }

            if (mCurrentNotification != null && cancelNotificationLocked(mCurrentNotification,
                    GFMinimalNotification.Callback.DISMISS_EVENT_CONSECUTIVE)) {
                // If we currently have a GFNotification, try and cancel it and wait in line
                return;
            } else {
                // Clear out the current GFNotification
                mCurrentNotification = null;
                // Otherwise, just show it now
                showNextNotificationLocked();
            }
        }
    }

    public void dismiss(Callback callback, int event) {
        synchronized (mLock) {
            if (isCurrentNotificationLocked(callback)) {
                cancelNotificationLocked(mCurrentNotification, event);
            } else if (isNextNotificationLocked(callback)) {
                cancelNotificationLocked(mNextNotification, event);
            }
        }
    }

    /**
     * Should be called when a GFNotification is no longer displayed. This is after any exit
     * animation has finished.
     */
    public void onDismissed(Callback callback) {
        synchronized (mLock) {
            if (isCurrentNotificationLocked(callback)) {
                // If the callback is from a GFNotification currently shown, remove it and show a new one
                mCurrentNotification = null;
                if (mNextNotification != null) {
                    showNextNotificationLocked();
                }
            }
        }
    }

    /**
     * Should be called when a GFNotification is being shown. This is after any entrance animation has
     * finished.
     */
    public void onShown(Callback callback) {
        synchronized (mLock) {
            if (isCurrentNotificationLocked(callback)) {
                scheduleTimeoutLocked(mCurrentNotification);
            }
        }
    }

    public void cancelTimeout(Callback callback) {
        synchronized (mLock) {
            if (isCurrentNotificationLocked(callback)) {
                mHandler.removeCallbacksAndMessages(mCurrentNotification);
            }
        }
    }

    public void restoreTimeout(Callback callback) {
        synchronized (mLock) {
            if (isCurrentNotificationLocked(callback)) {
                scheduleTimeoutLocked(mCurrentNotification);
            }
        }
    }

    public boolean isCurrent(Callback callback) {
        synchronized (mLock) {
            return isCurrentNotificationLocked(callback);
        }
    }

    public boolean isCurrentOrNext(Callback callback) {
        synchronized (mLock) {
            return isCurrentNotificationLocked(callback) || isNextNotificationLocked(callback);
        }
    }

    public static class Record {
        private final WeakReference<Callback> callback;
        private int duration;

        Record(int duration, Callback callback) {
            this.callback = new WeakReference<>(callback);
            this.duration = duration;
        }

        boolean isNotification(Callback callback) {
            return callback != null && this.callback.get() == callback;
        }
    }

    public void showNextNotificationLocked() {
        if (mNextNotification != null) {
            mCurrentNotification = mNextNotification;
            mNextNotification = null;

            final Callback callback = mCurrentNotification.callback.get();
            if (callback != null) {
                callback.show();
            } else {
                // The callback doesn't exist any more, clear out the GFNotification
                mCurrentNotification = null;
            }
        }
    }

    public boolean cancelNotificationLocked(Record record, int event) {
        final Callback callback = record.callback.get();
        if (callback != null) {
            // Make sure we remove any timeouts for the NotificationRecord
            mHandler.removeCallbacksAndMessages(record);
            callback.dismiss(event);
            return true;
        }
        return false;
    }

    public boolean isCurrentNotificationLocked(Callback callback) {
        return mCurrentNotification != null && mCurrentNotification.isNotification(callback);
    }

    public boolean isNextNotificationLocked(Callback callback) {
        return mNextNotification != null && mNextNotification.isNotification(callback);
    }

    public void scheduleTimeoutLocked(Record r) {
        if (r.duration == GFMinimalNotification.LENGTH_INDEFINITE) {
            // If we're set to indefinite, we don't want to set a timeout
            return;
        }

        int durationMs = LONG_DURATION_MS;
        if (r.duration > 0) {
            durationMs = r.duration;
        } else if (r.duration == GFMinimalNotification.LENGTH_SHORT) {
            durationMs = SHORT_DURATION_MS;
        }
        mHandler.removeCallbacksAndMessages(r);
        mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r), durationMs);
    }

    public void handleTimeout(Record record) {
        synchronized (mLock) {
            if (mCurrentNotification == record || mNextNotification == record) {
                cancelNotificationLocked(record, GFMinimalNotification.Callback.DISMISS_EVENT_TIMEOUT);
            }
        }
    }

    public interface Callback {
        void show();

        void dismiss(int event);
    }
}
