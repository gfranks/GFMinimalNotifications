package com.github.gfranks.minimal.notification;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class GFUndoNotificationLayout extends GFMinimalNotificationLayout {

    public GFUndoNotificationLayout(Context context) {
        super(context);
    }

    public GFUndoNotificationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GFUndoNotificationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GFUndoNotification getNotification() {
        return (GFUndoNotification) mNotification;
    }

    @Override
    protected void initNotification(Context context) {
        mNotification = new GFUndoNotification(context);
    }

    protected void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GFUndoNotificationLayout, defStyle, 0);
        String titleText = a.getString(R.styleable.GFUndoNotificationLayout_undo_title_text);
        String subtitleText = a.getString(R.styleable.GFUndoNotificationLayout_undo_subtitle_text);
        int leftViewRes = a.getInt(R.styleable.GFUndoNotificationLayout_undo_left_layout, -1);
        int slideDirection = a.getInt(R.styleable.GFUndoNotificationLayout_undo_display, 0);
        a.recycle();

        View leftView = null;
        try {
            leftView = inflate(context, leftViewRes, null);
        } catch (Throwable t) {
            t.printStackTrace();
            // error inflating left view
        }

        mNotification.setTitleText(titleText);
        mNotification.setSubtitleText(subtitleText);
        if (leftView != null) {
            mNotification.setLeftView(leftView);
        }
        mNotification.setSlideDirection(slideDirection);
    }

    /**
     *
     * @param builder Builder to build the notification with
     * @return GFUndoNotification
     */
    public GFUndoNotificationLayout createUndoNotificationFromBuilder(GFUndoNotification.Builder builder) {
        if (mNotification.isShowing()) {
            ((GFUndoNotification) mNotification).updateFromBuilder(builder);
        } else {
            mNotification = new GFUndoNotification(builder);
        }
        return this;
    }

    /**
     * @param callback The desired callback to be fired when the notification is shown, dismissed, or when the undo action should be performed
     * @return GFUndoNotificationLayout
     */
    public GFUndoNotificationLayout setGFUndoNotificationCallback(GFUndoNotificationCallback callback) {
        ((GFUndoNotification) mNotification).setGFUndoNotificationCallback(callback);
        return this;
    }

    /**
     * Do to this being an undo notification, no styling may be applied. To apply styling, you may call any of the provided
     * methods to alter the notification background, alter the title or subtitle text colors/fonts, etc.
     *
     * @param style The desired GFMinimalNotificationStyle
     * @return GFUndoNotificationLayout
     */
    @Override
    public GFUndoNotificationLayout setStyle(GFMinimalNotificationStyle style) {
        // no styling available for undo notifications
        return this;
    }

    /**
     * Do to this being an undo notification, the right view is a text view and may not be altered unless you call getUndoView()
     *
     * @param imageResId The desired image resource to be set as the right view if not replaced
     * @return GFUndoNotificationLayout
     */
    @Override
    public GFMinimalNotificationLayout setRightImageResource(int imageResId) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * Do to this being an undo notification, the right view is a text view and may not be altered unless you call getUndoView()
     *
     * @param drawable The desired drawable to be set as the right view if not replaced
     * @return GFUndoNotificationLayout
     */
    @Override
    public GFUndoNotificationLayout setRightImageDrawable(Drawable drawable) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * Do to this being an undo notification, the right view is a text view and may not be altered unless you call getUndoView()
     *
     * @param visible Set the right view visible or not
     * @return GFUndoNotificationLayout
     */
    @Override
    public GFUndoNotificationLayout setRightImageVisible(boolean visible) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * @param view    The desired view to replace the current right view
     * @return GFUndoNotificationLayout
     */
    @Override
    public GFUndoNotificationLayout setRightView(View view) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * Returns the undo text view so you may alter it's color, background, font, etc.
     *
     * @return TextView
     */
    public TextView getUndoView() {
        return ((GFUndoNotification) mNotification).getUndoView();
    }
}
