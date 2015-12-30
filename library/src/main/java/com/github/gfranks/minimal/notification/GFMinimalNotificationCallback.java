package com.github.gfranks.minimal.notification;

public interface GFMinimalNotificationCallback {

    /**
     * Callback for notification showing
     *
     * @param notification Notifcation to be shown
     */
    void didShowNotification(GFMinimalNotification notification);

    /**
     * Callback for notification dismissal
     *
     * @param notification Notifcation that was dismissed
     */
    void didDismissNotification(GFMinimalNotification notification);
}
