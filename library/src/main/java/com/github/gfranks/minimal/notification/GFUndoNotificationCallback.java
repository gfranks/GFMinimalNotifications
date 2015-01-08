package com.github.gfranks.minimal.notification;

public interface GFUndoNotificationCallback {

    /**
     * Callback for undo action clicks
     *
     * @param notification GFUndoNotification
     */
    public void onUndoAction(GFUndoNotification notification);
}
