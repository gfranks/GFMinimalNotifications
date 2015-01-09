package com.gf.minimal.notifications.notification;

public interface OnGFMinimalNotificationClickListener {

    /**
     * Callback for onClick event on notification
     *
     * @param notification notification that received the onClick event
     */
    void onClick(GFMinimalNotification notification);
}
