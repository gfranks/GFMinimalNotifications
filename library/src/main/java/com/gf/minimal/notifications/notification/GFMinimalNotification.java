package com.gf.minimal.notifications.notification;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gf.minimal.notifications.R;
import com.gf.minimal.notifications.activity.BaseNotificationActionBarActivity;
import com.gf.minimal.notifications.activity.BaseNotificationActivity;
import com.gf.minimal.notifications.activity.BaseNotificationFragmentActivity;
import com.gf.minimal.notifications.fragment.BaseNotificationFragment;
import com.gf.minimal.notifications.fragment.BaseNotificationSupportFragment;
import com.gf.minimal.notifications.widget.CircleImageView;

public class GFMinimalNotification implements View.OnClickListener {

    public static final long LENGTH_LONG = 5000;
    public static final long LENGTH_SHORT = 2500;

    private final long ANIMATION_DURATION = 400;

    private final int TITLE_CONTAINER_WEIGHT_FULL = 10;
    private final int TITLE_CONTAINER_WEIGHT_NORMAL = 6;
    private final int TITLE_CONTAINER_WEIGHT_HALF = 8;

    private GFMinimalNotificationStyle mStyle;
    private String mTitle;
    private String mSubtitle;
    private long mDuration;

    private View mNotificationView;
    private LinearLayout mNotificationTextContainer;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private View mLeftView;
    private View mRightView;

    private boolean mSlideFromTop = true;
    private boolean mCustomBackgroundSet;
    private boolean mCustomTitleTextColorSet;
    private boolean mCustomSubtitleTextColorSet;
    private boolean mMustClickToDismiss;

    private int mNotificationViewHeight;

    private Handler mHandler = null;
    private Runnable mRunnable = null;
    private GFMinimalNotificationCallback mCallback;
    private OnGFMinimalNotificationClickListener mOnGFMinimalNotificationClickListener;

    /**
     * @param context Context to be used to inflate the notification and set additional values
     */
    public GFMinimalNotification(Context context) {
        mNotificationViewHeight = context.getResources().getDimensionPixelSize(R.dimen.notification_height);
        init(context);
    }

    /**
     * @param context       Context to be used to inflate the notification and set additional values
     * @param titleResId    The desired notification title resource id
     * @param subtitleResId The desired notification subtitle resource id
     */
    public GFMinimalNotification(Context context, int titleResId, int subtitleResId) {
        this(context, GFMinimalNotificationStyle.DEFAULT, titleResId, subtitleResId);
    }

    /**
     * @param context  Context to be used to inflate the notification and set additional values
     * @param title    The desired notification title
     * @param subtitle The desired notification subtitle
     */
    public GFMinimalNotification(Context context, String title, String subtitle) {
        this(context, GFMinimalNotificationStyle.DEFAULT, title, subtitle);
    }

    /**
     * @param context       Context to be used to inflate the notification and set additional values
     * @param style         The desired style to be used
     * @param titleResId    The desired notification title resource id
     * @param subtitleResId The desired notification subtitle resource id
     */
    public GFMinimalNotification(Context context, GFMinimalNotificationStyle style, int titleResId, int subtitleResId) {
        this(context, style, titleResId, subtitleResId, LENGTH_SHORT);
    }

    /**
     * @param context  Context to be used to inflate the notification and set additional values
     * @param style    The desired style to be used
     * @param title    The desired notification title
     * @param subtitle The desired notification subtitle
     */
    public GFMinimalNotification(Context context, GFMinimalNotificationStyle style, String title, String subtitle) {
        this(context, style, title, subtitle, LENGTH_SHORT);
    }

    /**
     * @param context       Context to be used to inflate the notification and set additional values
     * @param style         The desired style to be used
     * @param titleResId    The desired notification title resource id
     * @param subtitleResId The desired notification subtitle resource id
     * @param duration      The desired duration for the notification to be shown (NOTE: passing 0 will result in a notification that will only be dismissed calling dismiss() yourself
     */
    public GFMinimalNotification(Context context, GFMinimalNotificationStyle style, int titleResId, int subtitleResId, long duration) {
        this(context, style, context.getString(titleResId), context.getString(subtitleResId), duration);
    }

    /**
     * @param context  Context to be used to inflate the notification and set additional values
     * @param style    The desired style to be used
     * @param title    The desired notification title
     * @param subtitle The desired notification subtitle
     * @param duration The desired duration for the notification to be shown (NOTE: passing 0 will result in a notification that will only be dismissed calling dismiss() yourself
     */
    public GFMinimalNotification(Context context, GFMinimalNotificationStyle style, String title, String subtitle, long duration) {
        mStyle = style;
        mTitle = title;
        mSubtitle = subtitle;
        if (duration > 0) {
            mDuration = duration;
            mMustClickToDismiss = false;
        } else {
            mMustClickToDismiss = true;
        }

        mNotificationViewHeight = context.getResources().getDimensionPixelSize(R.dimen.notification_height);
        init(context);
    }

    /**
     * @param callback The desired callback to be fired when the notification is shown or dismissed
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setGFMinimalNotificationCallback(GFMinimalNotificationCallback callback) {
        mCallback = callback;
        return this;
    }

    /**
     * @param onGFMinimalNotificationClickListener The desired click listener to be fired when the notification is clicked
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setOnGFMinimalNotificationClickListener(OnGFMinimalNotificationClickListener onGFMinimalNotificationClickListener) {
        mOnGFMinimalNotificationClickListener = onGFMinimalNotificationClickListener;
        return this;
    }

    /**
     * @param duration The desired duration before dismissing the notification
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setDuration(long duration) {
        if (duration > 0) {
            mDuration = duration;
            mMustClickToDismiss = false;
        } else {
            mMustClickToDismiss = true;
        }
        return this;
    }

    /**
     * @param style The desired GFMinimalNotificationStyle
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setStyle(GFMinimalNotificationStyle style) {
        mStyle = style;
        return this;
    }

    /**
     * @param title The desired text to be displayed as the title
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setTitleText(String title) {
        mTitle = title;
        return this;
    }

    /**
     * @param tf    The desired Typeface to be set as the title font
     * @param style The desired style to be set as the title style
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setTitleFont(Typeface tf, int style) {
        mTitleView.setTypeface(tf, style);
        return this;
    }

    /**
     * @param style The desired style to be set as the title style
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setTitleFont(int style) {
        mTitleView.setTypeface(null, style);
        return this;
    }

    /**
     * @param color The desired color to bet set as the title text color
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setTitleTextColor(int color) {
        mCustomTitleTextColorSet = true;
        mTitleView.setTextColor(color);
        return this;
    }

    /**
     * @param size The desired text size to be set as the title text size
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setTitleTextSize(float size) {
        mTitleView.setTextSize(size);
        return this;
    }

    /**
     * @param subtitle The desired text to be displayed as the title
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSubtitleText(String subtitle) {
        mSubtitle = subtitle;
        return this;
    }

    /**
     * @param tf    The desired Typeface to be set as the subtitle font
     * @param style The desired style to be set as the subtitle style
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSubtitleFont(Typeface tf, int style) {
        mSubtitleView.setTypeface(tf, style);
        return this;
    }

    /**
     * @param style The desired style to be set as the subtitle style
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSubtitleFont(int style) {
        mSubtitleView.setTypeface(null, style);
        return this;
    }

    /**
     * @param color The desired color to bet set as the subtitle text color
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSubtitleTextColor(int color) {
        mCustomSubtitleTextColorSet = true;
        mSubtitleView.setTextColor(color);
        return this;
    }

    /**
     * @param size The desired text size to be set as the subtitle text size
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSubtitleTextSize(float size) {
        mSubtitleView.setTextSize(size);
        return this;
    }

    /**
     * @param imageResId The desired image resource to be set as the left view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftImageResource(int imageResId) {
        try {
            ((ImageView) mLeftView).setImageResource(imageResId);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not an image view, view has changed
        }
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as the left view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftImageDrawable(Drawable drawable) {
        try {
            ((ImageView) mLeftView).setImageDrawable(drawable);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not an image view, view has changed
        }
        return this;
    }

    /**
     * @param borderWidth The desired border width to be set for the left image view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftImageBorderWidth(int borderWidth) {
        try {
            ((CircleImageView) mLeftView).setBorderWidth(borderWidth);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not a circle image view, view has changed
        }
        return this;
    }

    /**
     * @param borderColor The desired border color to bet set for the left image view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftImageBorderColor(int borderColor) {
        try {
            ((CircleImageView) mLeftView).setBorderColor(borderColor);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not a circle image view, view has changed
        }
        return this;
    }

    /**
     * @param visible Set the left view visible or not
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftImageVisible(boolean visible) {
        if (visible) {
            mLeftView.setVisibility(View.VISIBLE);
        } else {
            mLeftView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * NOTE: There is a height constraint of 45dp on this view
     *
     * @param context Context to be used to set the view height
     * @param view    The desired view to replace the current left view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftView(Context context, View view) {
        ((LinearLayout) mNotificationView).removeView(mRightView);
        ((LinearLayout) mNotificationView).addView(view, 0,
                new LinearLayout.LayoutParams(0, context.getResources().getDimensionPixelSize(R.dimen.notification_image_size), 2));
        mLeftView = view;
        return this;
    }

    /**
     * @param imageResId The desired image resource to be set as the right view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightImageResource(int imageResId) {
        try {
            ((ImageView) mRightView).setImageResource(imageResId);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not an image view, view has changed
            mRightView.setBackgroundResource(imageResId);
        }
        if (imageResId > -1) {
            mRightView.setVisibility(View.VISIBLE);
        } else {
            mRightView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as the right view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightImageDrawable(Drawable drawable) {
        try {
            ((ImageView) mRightView).setImageDrawable(drawable);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not an image view, view has changed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mRightView.setBackground(drawable);
            } else {
                mRightView.setBackgroundDrawable(drawable);
            }
        }
        if (drawable != null) {
            mRightView.setVisibility(View.VISIBLE);
        } else {
            mRightView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * @param borderWidth The desired border width to be set for the right image view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightImageBorderWidth(int borderWidth) {
        try {
            ((CircleImageView) mRightView).setBorderWidth(borderWidth);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not a circle image view, view has changed
        }
        return this;
    }

    /**
     * @param borderColor The desired border color to bet set for the right image view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightImageBorderColor(int borderColor) {
        try {
            ((CircleImageView) mRightView).setBorderColor(borderColor);
        } catch (Throwable t) {
            t.printStackTrace();
            // right view is not a circle image view, view has changed
        }
        return this;
    }

    /**
     * @param visible Set the right view visible or not
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightImageVisible(boolean visible) {
        if (visible) {
            mRightView.setVisibility(View.VISIBLE);
        } else {
            mRightView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * NOTE: There is a height constraint of 45dp on this view
     *
     * @param context Context to be used to set the view height
     * @param view    The desired view to replace the current right view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightView(Context context, View view) {
        ((LinearLayout) mNotificationView).removeView(mRightView);
        ((LinearLayout) mNotificationView).addView(view,
                new LinearLayout.LayoutParams(0, context.getResources().getDimensionPixelSize(R.dimen.notification_image_size), 2));
        mRightView = view;
        return this;
    }

    /**
     * @param color The desired background color to be set as notification background
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setNotificationBackgroundColor(int color) {
        mCustomBackgroundSet = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationView.setBackground(null);
        } else {
            mNotificationView.setBackgroundDrawable(null);
        }
        mNotificationView.setBackgroundColor(color);
        return this;
    }

    /**
     * @param drawableResId The desired drawable resource to be set as notification background
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setNotificationBackgroundResource(int drawableResId) {
        mCustomBackgroundSet = true;
        mNotificationView.setBackgroundResource(drawableResId);
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as notification background
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setNotificationBackground(Drawable drawable) {
        mCustomBackgroundSet = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationView.setBackground(drawable);
        } else {
            mNotificationView.setBackgroundDrawable(drawable);
        }
        return this;
    }

    /**
     * @return GFMinimalNotification
     * <p/>
     * Set the notification to slide in from the top
     */
    public GFMinimalNotification setSlideInFromTop() {
        mSlideFromTop = true;
        return this;
    }

    /**
     * @return GFMinimalNotification
     * <p/>
     * Set the notification to slide in from the bottom
     */
    public GFMinimalNotification setSlideInFromBottom() {
        mSlideFromTop = false;
        return this;
    }

    /**
     * Presents the notification
     *
     * @param activity BaseNotificationActionBarActivity to show notification in
     */
    public void show(BaseNotificationActionBarActivity activity) {
        show(activity, (FrameLayout) activity.findViewById(R.id.notification_root));
    }

    /**
     * Presents the notification
     *
     * @param activity BaseNotificationFragmentActivity to show notification in
     */
    public void show(BaseNotificationFragmentActivity activity) {
        show(activity, (FrameLayout) activity.findViewById(R.id.notification_root));
    }

    /**
     * Presents the notification
     *
     * @param activity BaseNotificationActivity to show notification in
     */
    public void show(BaseNotificationActivity activity) {
        show(activity, (FrameLayout) activity.findViewById(R.id.notification_root));
    }

    /**
     * Presents the notification
     *
     * @param fragment BaseNotificationFragment to show notification in
     */
    public void show(BaseNotificationFragment fragment) {
        FrameLayout rootView = (FrameLayout) fragment.getView().findViewById(R.id.notification_root);
        if (mNotificationView.getParent() == null) {
            rootView.addView(mNotificationView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mNotificationViewHeight));
        }
        mHandler.removeCallbacks(mRunnable);
        initNotification(fragment.getActivity());
        doShow();
    }

    /**
     * Presents the notification
     *
     * @param fragment BaseNotificationSupportFragment to show notification in
     */
    public void show(BaseNotificationSupportFragment fragment) {
        FrameLayout rootView = (FrameLayout) fragment.getView().findViewById(R.id.notification_root);
        if (mNotificationView.getParent() == null) {
            rootView.addView(mNotificationView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mNotificationViewHeight));
        }
        mHandler.removeCallbacks(mRunnable);
        initNotification(fragment.getActivity());
        doShow();
    }

    /**
     * Dismisses the notification
     */
    public void dismiss() {
        doDismiss();
    }

    @Override
    public void onClick(View v) {
        if (!mMustClickToDismiss) {
            dismiss();
        }
        if (mOnGFMinimalNotificationClickListener != null) {
            mOnGFMinimalNotificationClickListener.onClick(this);
        }
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNotificationView = inflater.inflate(R.layout.layout_gf_minimal_notification, null, false);

        mNotificationTextContainer = (LinearLayout) mNotificationView.findViewById(R.id.notification_text_container);
        mTitleView = (TextView) mNotificationView.findViewById(R.id.notification_title);
        mSubtitleView = (TextView) mNotificationView.findViewById(R.id.notification_subtitle);
        mLeftView = (CircleImageView) mNotificationView.findViewById(R.id.notification_left_image);
        mRightView = (CircleImageView) mNotificationView.findViewById(R.id.notification_right_image);

        mNotificationView.setOnClickListener(this);

        mNotificationView.setVisibility(View.GONE);
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                doDismiss();
            }
        };
    }

    private void show(Activity activity, FrameLayout rootView) {
        if (mNotificationView.getParent() == null) {
            rootView.addView(mNotificationView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mNotificationViewHeight));
        }
        mHandler.removeCallbacks(mRunnable);
        initNotification(activity);
        doShow();
    }

    private void initNotification(Context context) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mNotificationTextContainer.getLayoutParams();
        if (mRightView.getVisibility() == View.VISIBLE) {
            if (mLeftView.getVisibility() == View.VISIBLE) {
                lp.weight = TITLE_CONTAINER_WEIGHT_NORMAL;
            } else {
                lp.weight = TITLE_CONTAINER_WEIGHT_HALF;
            }
        } else {
            if (mLeftView.getVisibility() == View.VISIBLE) {
                lp.weight = TITLE_CONTAINER_WEIGHT_HALF;
            } else {
                lp.weight = TITLE_CONTAINER_WEIGHT_FULL;
            }
        }
        mNotificationTextContainer.setLayoutParams(lp);

        int titleTextColor = context.getResources().getColor(R.color.text_white);
        int subtitleTextColor = context.getResources().getColor(R.color.text_white);
        if (!mCustomBackgroundSet) {
            switch (mStyle) {
                case DEFAULT:
                    mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.blue));
                    titleTextColor = context.getResources().getColor(R.color.text_white);
                    subtitleTextColor = context.getResources().getColor(R.color.text_white);
                    try {
                        ((CircleImageView) mLeftView).setBorderColor(context.getResources().getColor(R.color.white));
                        ((CircleImageView) mRightView).setBorderColor(context.getResources().getColor(R.color.white));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    break;
                case ERROR:
                    mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.red));
                    titleTextColor = context.getResources().getColor(R.color.text_white);
                    subtitleTextColor = context.getResources().getColor(R.color.text_white);
                    try {
                        ((CircleImageView) mLeftView).setBorderColor(context.getResources().getColor(R.color.white));
                        ((CircleImageView) mRightView).setBorderColor(context.getResources().getColor(R.color.white));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    break;
                case INFO:
                    mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.orange));
                    titleTextColor = context.getResources().getColor(R.color.text_white);
                    subtitleTextColor = context.getResources().getColor(R.color.text_white);
                    try {
                        ((CircleImageView) mLeftView).setBorderColor(context.getResources().getColor(R.color.white));
                        ((CircleImageView) mRightView).setBorderColor(context.getResources().getColor(R.color.white));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    break;
                case SUCCESS:
                    mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.green));
                    titleTextColor = context.getResources().getColor(R.color.text_white);
                    subtitleTextColor = context.getResources().getColor(R.color.text_white);
                    try {
                        ((CircleImageView) mLeftView).setBorderColor(context.getResources().getColor(R.color.white));
                        ((CircleImageView) mRightView).setBorderColor(context.getResources().getColor(R.color.white));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    break;
                case WARNING:
                    mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                    titleTextColor = context.getResources().getColor(R.color.text_gray);
                    subtitleTextColor = context.getResources().getColor(R.color.text_gray);
                    try {
                        ((CircleImageView) mLeftView).setBorderColor(context.getResources().getColor(R.color.gray));
                        ((CircleImageView) mRightView).setBorderColor(context.getResources().getColor(R.color.gray));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    break;
            }
        } else {
            try {
                ((CircleImageView) mLeftView).setBorderWidth(0);
                ((CircleImageView) mRightView).setBorderWidth(0);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        mTitleView.setText(mTitle);
        mSubtitleView.setText(mSubtitle);

        if (!mCustomTitleTextColorSet) {
            mTitleView.setTextColor(titleTextColor);
        }
        if (!mCustomSubtitleTextColorSet) {
            mSubtitleView.setTextColor(subtitleTextColor);
        }
    }

    private void doShow() {
        mNotificationView.clearAnimation();
        if (mNotificationView.getVisibility() == View.VISIBLE)
            return;

        float fromY;
        float toY;
        if (mSlideFromTop) {
            fromY = -(mNotificationViewHeight);
            toY = 0;
        } else {
            fromY = ((View) mNotificationView.getParent()).getHeight();
            toY = ((View) mNotificationView.getParent()).getHeight() - mNotificationViewHeight;
        }

        TranslateAnimation animation = new TranslateAnimation(0, 0, fromY, toY);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mNotificationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!mMustClickToDismiss) {
                    mHandler.postDelayed(mRunnable, mDuration);
                }
                if (mCallback != null) {
                    mCallback.didShowNotification(GFMinimalNotification.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mNotificationView.startAnimation(animation);
    }

    private void doDismiss() {
        mNotificationView.clearAnimation();
        mHandler.removeCallbacks(mRunnable);
        if (mNotificationView.getVisibility() == View.GONE)
            return;

        float fromY;
        float toY;
        if (mSlideFromTop) {
            fromY = 0;
            toY = -(mNotificationViewHeight);
        } else {
            fromY = ((View) mNotificationView.getParent()).getHeight() - mNotificationViewHeight;
            toY = ((View) mNotificationView.getParent()).getHeight();
        }

        TranslateAnimation animation = new TranslateAnimation(0, 0, fromY, toY);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mNotificationView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mNotificationView.setVisibility(View.GONE);
                if (mCallback != null) {
                    mCallback.didDismissNotification(GFMinimalNotification.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mNotificationView.startAnimation(animation);
    }
}
