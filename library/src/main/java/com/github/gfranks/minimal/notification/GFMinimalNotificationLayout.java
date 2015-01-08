package com.github.gfranks.minimal.notification;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class GFMinimalNotificationLayout extends FrameLayout {

    protected GFMinimalNotification mNotification;

    public GFMinimalNotificationLayout(Context context) {
        super(context);
        initNotification(context);
    }

    public GFMinimalNotificationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNotification(context);
        init(context, attrs, 0);
    }

    public GFMinimalNotificationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initNotification(context);
        init(context, attrs, defStyleAttr);
    }

    public GFMinimalNotification getNotification() {
        return mNotification;
    }

    protected void initNotification(Context context) {
        mNotification = new GFMinimalNotification(context);
    }

    protected void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GFMinimalNotificationLayout, defStyle, 0);
        String titleText = a.getString(R.styleable.GFMinimalNotificationLayout_title_text);
        String subtitleText = a.getString(R.styleable.GFMinimalNotificationLayout_subtitle_text);
        int leftViewRes = a.getInt(R.styleable.GFMinimalNotificationLayout_left_layout, -1);
        int rightViewRes = a.getInt(R.styleable.GFMinimalNotificationLayout_right_layout, -1);
        int style = a.getInt(R.styleable.GFMinimalNotificationLayout_style, GFMinimalNotificationStyle.DEFAULT.ordinal());
        int slideDirection = a.getInt(R.styleable.GFMinimalNotificationLayout_display, 0);
        a.recycle();

        View leftView = null;
        View rightView = null;
        try {
            leftView = inflate(context, leftViewRes, null);
        } catch (Throwable t) {
            t.printStackTrace();
            // error inflating left view
        }

        try {
            rightView = inflate(context, rightViewRes, null);
        } catch (Throwable t) {
            t.printStackTrace();
            // error inflating right view
        }

        mNotification.setStyle(GFMinimalNotificationStyle.values()[style]);
        mNotification.setTitleText(titleText);
        mNotification.setSubtitleText(subtitleText);
        if (leftView != null) {
            mNotification.setLeftView(leftView);
        }
        if (rightView != null) {
            mNotification.setRightView(rightView);
        }
        mNotification.setSlideDirection(slideDirection);
    }

    /**
     *
     * @param builder Builder to build the notification with
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout createNotificationFromBuilder(GFMinimalNotification.Builder builder) {
        if (mNotification.isShowing()) {
            mNotification.updateFromBuilder(builder);
        } else {
            mNotification = new GFMinimalNotification(builder);
        }
        return this;
    }

    /**
     *
     * @param notification The desired notification to show
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setNotification(GFMinimalNotification notification) {
        mNotification = notification;
        return this;
    }

    /**
     * @param callback The desired callback to be fired when the notification is shown or dismissed
     * @return GFMinimalNotification
     */
    public GFMinimalNotificationLayout setGFMinimalNotificationCallback(GFMinimalNotificationCallback callback) {
        mNotification.setGFMinimalNotificationCallback(callback);
        return this;
    }

    /**
     * @param onGFMinimalNotificationClickListener The desired click listener to be fired when the notification is clicked
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setOnGFMinimalNotificationClickListener(OnGFMinimalNotificationClickListener onGFMinimalNotificationClickListener) {
        mNotification.setOnGFMinimalNotificationClickListener(onGFMinimalNotificationClickListener);
        return this;
    }

    /**
     * @param duration The desired duration before dismissing the notification
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setDuration(long duration) {
        mNotification.setDuration(duration);
        return this;
    }

    /**
     * @param animationDuration The desired animation duration when showing and dismissing
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setAnimationDuration(long animationDuration) {
        mNotification.setAnimationDuration(animationDuration);
        return this;
    }

    /**
     * @param style The desired GFMinimalNotificationStyle
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setStyle(GFMinimalNotificationStyle style) {
        mNotification.setStyle(style);
        return this;
    }

    /**
     * @param title The desired text to be displayed as the title
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setTitleText(String title) {
        mNotification.setTitleText(title);
        return this;
    }

    /**
     * @param tf    The desired Typeface to be set as the title font
     * @param style The desired style to be set as the title style
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setTitleFont(Typeface tf, int style) {
        mNotification.setTitleFont(tf, style);
        return this;
    }

    /**
     * @param style The desired style to be set as the title style
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setTitleFont(int style) {
        mNotification.setTitleFont(style);
        return this;
    }

    /**
     * @param color The desired color to bet set as the title text color
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setTitleTextColor(int color) {
        mNotification.setTitleTextColor(color);
        return this;
    }

    /**
     * @param size The desired text size to be set as the title text size
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setTitleTextSize(float size) {
        mNotification.setTitleTextSize(size);
        return this;
    }

    /**
     * @param subtitle The desired text to be displayed as the title
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setSubtitleText(String subtitle) {
        mNotification.setSubtitleText(subtitle);
        return this;
    }

    /**
     * @param tf    The desired Typeface to be set as the subtitle font
     * @param style The desired style to be set as the subtitle style
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setSubtitleFont(Typeface tf, int style) {
        mNotification.setSubtitleFont(tf, style);
        return this;
    }

    /**
     * @param style The desired style to be set as the subtitle style
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setSubtitleFont(int style) {
        mNotification.setSubtitleFont(style);
        return this;
    }

    /**
     * @param color The desired color to bet set as the subtitle text color
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setSubtitleTextColor(int color) {
        mNotification.setSubtitleTextColor(color);
        return this;
    }

    /**
     * @param size The desired text size to be set as the subtitle text size
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setSubtitleTextSize(float size) {
        mNotification.setSubtitleTextSize(size);
        return this;
    }

    /**
     * @param imageResId The desired image resource to be set as the left image view if not replaced
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setLeftImageResource(int imageResId) {
        mNotification.setLeftImageResource(imageResId);
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as the left image view if not replaced
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setLeftImageDrawable(Drawable drawable) {
        mNotification.setLeftImageDrawable(drawable);
        return this;
    }

    /**
     * @param visible Set the left view visible or not
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setLeftImageVisible(boolean visible) {
        mNotification.setLeftImageVisible(visible);
        return this;
    }

    /**
     * NOTE: There is a height constraint of 65dp on this view
     *
     * @param view    The desired view to replace the current left view
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setLeftView(View view) {
        mNotification.setLeftView(view);
        return this;
    }

    /**
     * @return Left view of the notification
     */
    public View getLeftView() {
        return mNotification.getLeftView();
    }

    /**
     * @param imageResId The desired image resource to be set as the right view if not replaced
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setRightImageResource(int imageResId) {
        mNotification.setRightImageResource(imageResId);
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as the right view if not replaced
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setRightImageDrawable(Drawable drawable) {
        mNotification.setRightImageDrawable(drawable);
        return this;
    }

    /**
     * @param visible Set the right view visible or not
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setRightImageVisible(boolean visible) {
        mNotification.setRightImageVisible(visible);
        return this;
    }

    /**
     * NOTE: There is a height constraint of 65dp on this view
     *
     * @param view    The desired view to replace the current right view
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setRightView(View view) {
        mNotification.setRightView(view);
        return this;
    }

    /**
     * @return Right view of the notification
     */
    public View getRightView() {
        return mNotification.getRightView();
    }

    /**
     * @param color The desired background color to be set as notification background
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setNotificationBackgroundColor(int color) {
        mNotification.setNotificationBackgroundColor(color);
        return this;
    }

    /**
     * @param drawableResId The desired drawable resource to be set as notification background
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setNotificationBackgroundResource(int drawableResId) {
        mNotification.setNotificationBackgroundResource(drawableResId);
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as notification background
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setNotificationBackground(Drawable drawable) {
        mNotification.setNotificationBackground(drawable);
        return this;
    }

    /**
     * Set the notification slide direction (SLIDE_TOP or SLIDE_BOTTOM)
     *
     * @return GFMinimalNotificationLayout
     */
    public GFMinimalNotificationLayout setSlideDirection(int slideDirection) {
        mNotification.setSlideDirection(slideDirection);
        return this;
    }

    public void show() {
        mNotification.show(this);
    }

    public void dismiss() {
        mNotification.dismiss();
    }
}
