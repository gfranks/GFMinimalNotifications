package com.github.gfranks.minimal.notification;

public interface GFUndoNotificationCallback {

    /**
     * Callback for undo action clicks
     *
     * @param notification GFUndoNotification
     */
    void onUndoAction(GFUndoNotification notification);
}
