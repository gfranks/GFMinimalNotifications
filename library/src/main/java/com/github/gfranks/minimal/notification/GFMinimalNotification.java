package com.github.gfranks.minimal.notification;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GFMinimalNotification implements View.OnClickListener {

    public static final long LENGTH_LONG = 5000;
    public static final long LENGTH_SHORT = 2500;

    public static final int SLIDE_TOP = 0;
    public static final int SLIDE_BOTTOM = 1;

    protected static final int TITLE_CONTAINER_WEIGHT_FULL = 5;
    protected static final int TITLE_CONTAINER_WEIGHT_NORMAL = 3;
    protected static final int TITLE_CONTAINER_WEIGHT_HALF = 4;
    protected static final int LEFT_RIGHT_VIEW_WEIGHT = 1;
    protected static final long DEFAULT_ANIMATION_DURATION = 250;
    protected long mAnimationDuration = DEFAULT_ANIMATION_DURATION;
    protected View mNotificationView;
    protected LinearLayout mNotificationTextContainer;
    protected TextView mTitleView;
    protected TextView mSubtitleView;
    protected View mLeftView;
    protected View mRightView;
    protected int mNotificationViewHeight;
    protected long mDuration;
    protected int mSlideDirection = SLIDE_TOP;
    protected boolean mCanSelfDismiss;

    protected Handler mHandler = null;
    protected Runnable mRunnable = null;
    protected GFMinimalNotificationCallback mCallback;
    protected OnGFMinimalNotificationClickListener mOnGFMinimalNotificationClickListener;

    protected GFMinimalNotification() {
    }

    /**
     * @param builder Builder to build the notification view
     */
    public GFMinimalNotification(Builder builder) {
        this(builder.mContext, builder.mDuration);
        setTitleText(builder.mTitle);
        setSubtitleText(builder.mSubtitle);
        setStyle(builder.mStyle);

        mAnimationDuration = builder.mAnimationDuration;
        if (builder.mBackground != null) {
            setNotificationBackground(builder.mBackground);
        }

        if (builder.mLeftView != null) {
            setLeftView(builder.mLeftView);
        } else if (builder.mLeftImageDrawable != null) {
            setLeftImageDrawable(builder.mLeftImageDrawable);
        }

        if (builder.mRightView != null) {
            setRightView(builder.mRightView);
        } else if (builder.mRightImageDrawable != null) {
            setRightImageDrawable(builder.mRightImageDrawable);
        }

        mSlideDirection = builder.mDirection;
        mCallback = builder.mNotificationCallback;
        mOnGFMinimalNotificationClickListener = builder.mListener;
    }

    /**
     * @param context Context to be used to inflate the notification and set additional values
     */
    public GFMinimalNotification(Context context) {
        this(context, LENGTH_SHORT);
    }

    /**
     * @param context  Context to be used to inflate the notification and set additional values
     * @param duration The desired duration for the notification to be shown (NOTE: passing 0 will result in a notification that will only be dismissed calling dismiss() yourself
     */
    public GFMinimalNotification(Context context, long duration) {
        mNotificationViewHeight = context.getResources().getDimensionPixelSize(R.dimen.notification_height);
        setDuration(duration);
        init(context);
        setStyle(GFMinimalNotificationStyle.DEFAULT);
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
        this(context, title, subtitle, LENGTH_SHORT);
    }

    /**
     * @param context       Context to be used to inflate the notification and set additional values
     * @param titleResId    The desired notification title resource id
     * @param subtitleResId The desired notification subtitle resource id
     * @param duration      The desired duration for the notification to be shown (NOTE: passing 0 will result in a notification that will only be dismissed calling dismiss() yourself)
     */
    public GFMinimalNotification(Context context, int titleResId, int subtitleResId, long duration) {
        this(context, GFMinimalNotificationStyle.DEFAULT, titleResId, subtitleResId, duration);
    }

    /**
     * @param context  Context to be used to inflate the notification and set additional values
     * @param title    The desired notification title
     * @param subtitle The desired notification subtitle
     * @param duration The desired duration for the notification to be shown (NOTE: passing 0 will result in a notification that will only be dismissed calling dismiss() yourself)
     */
    public GFMinimalNotification(Context context, String title, String subtitle, long duration) {
        this(context, GFMinimalNotificationStyle.DEFAULT, title, subtitle, duration);
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
     * @param duration      The desired duration for the notification to be shown (NOTE: passing 0 will result in a notification that will only be dismissed calling dismiss() yourself)
     */
    public GFMinimalNotification(Context context, GFMinimalNotificationStyle style, int titleResId, int subtitleResId, long duration) {
        this(context, style, context.getString(titleResId), context.getString(subtitleResId), duration);
    }

    /**
     * @param context  Context to be used to inflate the notification and set additional values
     * @param style    The desired style to be used
     * @param title    The desired notification title
     * @param subtitle The desired notification subtitle
     * @param duration The desired duration for the notification to be shown (NOTE: passing 0 will result in a notification that will only be dismissed calling dismiss() yourself)
     */
    public GFMinimalNotification(Context context, GFMinimalNotificationStyle style, String title, String subtitle, long duration) {
        if (duration > 0) {
            mDuration = duration;
            mCanSelfDismiss = true;
        } else {
            mCanSelfDismiss = false;
        }

        mNotificationViewHeight = context.getResources().getDimensionPixelSize(R.dimen.notification_height);
        init(context);

        setStyle(style);
        setTitleText(title);
        setSubtitleText(subtitle);
    }

    /**
     * Static method to instantiate GFMinimalNotification to build the notification
     *
     * @param context Context to be used to inflate the notification and set additional values
     * @return GFMinimalNotification
     */
    public static GFMinimalNotification with(Context context) {
        return new GFMinimalNotification(context);
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
     * If the notification is already showing, you may update it with a Builder. NOTE: Slide Direction cannot change in this instance if
     * notification is already showing
     *
     * @param builder Builder to build the notification view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification updateFromBuilder(Builder builder) {
        setTitleText(builder.mTitle);
        setSubtitleText(builder.mSubtitle);
        setStyle(builder.mStyle);

        mAnimationDuration = builder.mAnimationDuration;
        if (builder.mBackground != null) {
            setNotificationBackground(builder.mBackground);
        }

        if (builder.mLeftView != null) {
            setLeftView(builder.mLeftView);
        } else if (builder.mLeftImageDrawable != null) {
            setLeftImageDrawable(builder.mLeftImageDrawable);
        }

        if (builder.mRightView != null) {
            setRightView(builder.mRightView);
        } else if (builder.mRightImageDrawable != null) {
            setRightImageDrawable(builder.mRightImageDrawable);
        }

        mSlideDirection = builder.mDirection;
        mCallback = builder.mNotificationCallback;
        mOnGFMinimalNotificationClickListener = builder.mListener;
        if (isShowing()) {
            doShow();
        }
        return this;
    }

    /**
     * @param duration The desired duration before dismissing the notification
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setDuration(long duration) {
        if (duration > 0) {
            mDuration = duration;
            mCanSelfDismiss = true;
        } else {
            mCanSelfDismiss = false;
        }
        return this;
    }

    /**
     * @param animationDuration The desired animation duration when showing and dismissing
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setAnimationDuration(long animationDuration) {
        if (animationDuration > 0) {
            mAnimationDuration = animationDuration;
        }
        return this;
    }

    /**
     * @param style The desired GFMinimalNotificationStyle
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setStyle(GFMinimalNotificationStyle style) {
        Context context = mNotificationView.getContext();
        int leftImageRes;
        switch (style) {
            case ERROR:
                mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.gf_notification_red));
                mTitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                mSubtitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                leftImageRes = R.drawable.bg_error;
                break;
            case INFO:
                mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.gf_notification_orange));
                mTitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                mSubtitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                leftImageRes = R.drawable.bg_info;
                break;
            case SUCCESS:
                mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.gf_notification_green));
                mTitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                mSubtitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                leftImageRes = R.drawable.bg_success;
                break;
            case WARNING:
                mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.gf_notification_yellow));
                mTitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_gray));
                mSubtitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_gray));
                leftImageRes = R.drawable.bg_warning;
                break;
            case DEFAULT:
            default:
                mNotificationView.setBackgroundColor(context.getResources().getColor(R.color.gf_notification_blue));
                mTitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                mSubtitleView.setTextColor(context.getResources().getColor(R.color.gf_notification_text_white));
                leftImageRes = R.drawable.bg_default;
                break;
        }

        if (mLeftView instanceof ImageView) {
            ((ImageView) mLeftView).setImageResource(leftImageRes);
        } else {
            ImageView iv = new ImageView(context);
            iv.setImageResource(leftImageRes);
            setLeftView(iv);
        }
        return this;
    }

    /**
     * @param titleResId The desired text to be displayed as the title
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setTitleText(int titleResId) {
        return setTitleText(mNotificationView.getContext().getString(titleResId));
    }

    /**
     * @param title The desired text to be displayed as the title
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setTitleText(String title) {
        mTitleView.setText(title);
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
     * @param subtitleResId The desired text to be displayed as the title
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSubtitleText(int subtitleResId) {
        return setSubtitleText(mNotificationView.getContext().getString(subtitleResId));
    }

    /**
     * @param subtitle The desired text to be displayed as the title
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSubtitleText(String subtitle) {
        mSubtitleView.setText(subtitle);
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
     * @param imageResId The desired image resource to be set as the left image view if not replaced
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftImageResource(int imageResId) {
        try {
            ((ImageView) mLeftView).setImageResource(imageResId);
        } catch (Throwable t) {
            t.printStackTrace();
            // left view is not an image view, view has changed
        }
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as the left image view if not replaced
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftImageDrawable(Drawable drawable) {
        try {
            ((ImageView) mLeftView).setImageDrawable(drawable);
        } catch (Throwable t) {
            t.printStackTrace();
            // left view is not an image view, view has changed
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
        checkWeightConstraints();
        return this;
    }

    /**
     * @return Left view of the notification
     */
    public View getLeftView() {
        return mLeftView;
    }

    /**
     * NOTE: There is a height constraint of 65dp on this view
     *
     * @param view The desired view to replace the current left view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setLeftView(View view) {
        ((LinearLayout) mNotificationView).removeView(mLeftView);
        ((LinearLayout) mNotificationView).addView(view, 0,
                new LinearLayout.LayoutParams(0, mNotificationView.getContext().getResources().getDimensionPixelSize(R.dimen.notification_image_size), LEFT_RIGHT_VIEW_WEIGHT));
        mLeftView = view;
        return this;
    }

    /**
     * @param imageResId The desired image resource to be set as the right view if not replaced
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
     * @param drawable The desired drawable to be set as the right view if not replaced
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
     * @param visible Set the right view visible or not
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightImageVisible(boolean visible) {
        if (visible) {
            mRightView.setVisibility(View.VISIBLE);
        } else {
            mRightView.setVisibility(View.GONE);
        }
        checkWeightConstraints();
        return this;
    }

    /**
     * @return Right view of the notification
     */
    public View getRightView() {
        return mRightView;
    }

    /**
     * NOTE: There is a height constraint of 65dp on this view
     *
     * @param view The desired view to replace the current right view
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setRightView(View view) {
        ((LinearLayout) mNotificationView).removeView(mRightView);
        ((LinearLayout) mNotificationView).addView(view, ((LinearLayout) mNotificationView).getChildCount(),
                new LinearLayout.LayoutParams(0, mNotificationView.getContext().getResources().getDimensionPixelSize(R.dimen.notification_image_size), LEFT_RIGHT_VIEW_WEIGHT));
        mRightView = view;
        return this;
    }

    /**
     * @param color The desired background color to be set as notification background
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setNotificationBackgroundColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationView.setBackground(new ColorDrawable(color));
        } else {
            mNotificationView.setBackgroundDrawable(new ColorDrawable(color));
        }
        return this;
    }

    /**
     * @param drawableResId The desired drawable resource to be set as notification background
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setNotificationBackgroundResource(int drawableResId) {
        mNotificationView.setBackgroundResource(drawableResId);
        return this;
    }

    /**
     * @param drawable The desired drawable to be set as notification background
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setNotificationBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationView.setBackground(drawable);
        } else {
            mNotificationView.setBackgroundDrawable(drawable);
        }
        return this;
    }

    /**
     * Set the notification slide direction (SLIDE_TOP or SLIDE_BOTTOM)
     *
     * @return GFMinimalNotification
     */
    public GFMinimalNotification setSlideDirection(int slideDirection) {
        if (slideDirection != SLIDE_TOP && slideDirection != SLIDE_BOTTOM) {
            throw new IllegalStateException("Slide direction must be SLIDE_TOP or SLIDE_BOTTOM");
        }

        mSlideDirection = slideDirection;
        return this;
    }

    /**
     * Displays the notification inside the passed viewgroup
     *
     * @param rootView View to display the notification in
     */
    public void show(ViewGroup rootView) {
        if (mNotificationView.getParent() == null) {
            rootView.addView(mNotificationView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mNotificationViewHeight));
        }
        mHandler.removeCallbacks(mRunnable);
        doShow();
    }

    /**
     * Dismisses the notification
     */
    public void dismiss() {
        doDismiss();
    }

    /**
     * @param v View clicked
     */
    @Override
    public void onClick(View v) {
        if (mCanSelfDismiss) {
            dismiss();
        }
        if (mOnGFMinimalNotificationClickListener != null) {
            mOnGFMinimalNotificationClickListener.onClick(this);
        }
    }

    protected void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNotificationView = inflater.inflate(getNotificationLayout(), null, false);
        initViews(context);
    }

    protected int getNotificationLayout() {
        return R.layout.layout_gf_minimal_notification;
    }

    protected void initViews(Context context) {
        mNotificationTextContainer = (LinearLayout) mNotificationView.findViewById(R.id.notification_text_container);
        mTitleView = (TextView) mNotificationView.findViewById(R.id.notification_title);
        mSubtitleView = (TextView) mNotificationView.findViewById(R.id.notification_subtitle);
        mLeftView = mNotificationView.findViewById(R.id.notification_left_view);
        mRightView = mNotificationView.findViewById(R.id.notification_right_view);

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

    protected void checkWeightConstraints() {
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

        if (mTitleView.getText().toString().length() > 0) {
            mTitleView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams titleLp = (LinearLayout.LayoutParams) mTitleView.getLayoutParams();
            if (mSubtitleView.getText().toString().length() > 0) {
                mSubtitleView.setVisibility(View.VISIBLE);
                mSubtitleView.setSingleLine(true);
                mSubtitleView.setMaxLines(1);
                mSubtitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                mTitleView.setGravity(Gravity.START | Gravity.TOP);
                mTitleView.setSingleLine(true);
                mTitleView.setMaxLines(1);
                mTitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                titleLp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            } else {
                mSubtitleView.setVisibility(View.GONE);
                mTitleView.setGravity(Gravity.CENTER_VERTICAL);
                mTitleView.setSingleLine(false);
                mTitleView.setMaxLines(2);
                mTitleView.setEllipsize(TextUtils.TruncateAt.END);
                titleLp.height = LinearLayout.LayoutParams.MATCH_PARENT;
            }
            mSubtitleView.setGravity(Gravity.START | Gravity.TOP);
            mTitleView.setLayoutParams(titleLp);
        } else {
            mTitleView.setVisibility(View.GONE);
            LinearLayout.LayoutParams subtitleLp = (LinearLayout.LayoutParams) mSubtitleView.getLayoutParams();
            if (mSubtitleView.getText().toString().length() > 0) {
                mSubtitleView.setVisibility(View.VISIBLE);
                mSubtitleView.setSingleLine(false);
                mSubtitleView.setMaxLines(3);
                mSubtitleView.setEllipsize(TextUtils.TruncateAt.END);
                subtitleLp.height = LinearLayout.LayoutParams.MATCH_PARENT;
            } else {
                mSubtitleView.setVisibility(View.GONE);
                mSubtitleView.setSingleLine(true);
                mSubtitleView.setMaxLines(1);
                mSubtitleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                subtitleLp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            mSubtitleView.setGravity(Gravity.CENTER_VERTICAL);
            mSubtitleView.setLayoutParams(subtitleLp);
        }

        mNotificationTextContainer.setPadding(mLeftView.getVisibility() == View.VISIBLE ? 0 : 25, 0, 0, 0);
        mNotificationTextContainer.setLayoutParams(lp);
    }

    protected void doShow() {
        mNotificationView.clearAnimation();
        checkWeightConstraints();
        if (isShowing()) {
            if (mCanSelfDismiss) {
                mHandler.postDelayed(mRunnable, mDuration);
            }
            return;
        }

        if (mSlideDirection == SLIDE_TOP) {
            mNotificationView.setTranslationY(-mNotificationViewHeight);
            mNotificationView.animate()
                    .translationY(0)
                    .setDuration(mAnimationDuration)
                    .setInterpolator(new DecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mNotificationView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mCanSelfDismiss) {
                                mHandler.postDelayed(mRunnable, mDuration);
                            }
                            if (mCallback != null) {
                                mCallback.didShowNotification(GFMinimalNotification.this);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
        } else {
            mNotificationView.setTranslationY(((View) mNotificationView.getParent()).getHeight());
            mNotificationView.animate()
                    .translationY(((View) mNotificationView.getParent()).getHeight() - mNotificationViewHeight)
                    .setDuration(mAnimationDuration)
                    .setInterpolator(new DecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mNotificationView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mCanSelfDismiss) {
                                mHandler.postDelayed(mRunnable, mDuration);
                            }
                            if (mCallback != null) {
                                mCallback.didShowNotification(GFMinimalNotification.this);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
        }
    }

    protected void doDismiss() {
        mNotificationView.clearAnimation();
        mHandler.removeCallbacks(mRunnable);
        if (!isShowing()) {
            return;
        }

        if (mSlideDirection == SLIDE_TOP) {
            mNotificationView.animate()
                    .translationY(-mNotificationViewHeight)
                    .setDuration(mAnimationDuration)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mNotificationView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mNotificationView.setVisibility(View.GONE);
                            if (mCallback != null) {
                                mCallback.didDismissNotification(GFMinimalNotification.this);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
        } else {
            mNotificationView.animate()
                    .translationY(((View) mNotificationView.getParent()).getHeight())
                    .setDuration(mAnimationDuration)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mNotificationView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mNotificationView.setVisibility(View.GONE);
                            if (mCallback != null) {
                                mCallback.didDismissNotification(GFMinimalNotification.this);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .start();
        }
    }

    protected boolean isShowing() {
        return mNotificationView.getVisibility() == View.VISIBLE;
    }

    public static class Builder {

        private Context mContext;
        private long mDuration = LENGTH_SHORT;
        private long mAnimationDuration = DEFAULT_ANIMATION_DURATION;
        private GFMinimalNotificationStyle mStyle = GFMinimalNotificationStyle.DEFAULT;
        private String mTitle;
        private String mSubtitle;
        private Drawable mLeftImageDrawable;
        private View mLeftView;
        private Drawable mRightImageDrawable;
        private View mRightView;
        private Drawable mBackground;
        private int mDirection = SLIDE_TOP;
        private GFMinimalNotificationCallback mNotificationCallback;
        private OnGFMinimalNotificationClickListener mListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder duration(long duration) {
            mDuration = duration;
            return this;
        }

        public Builder animationDuration(long animationDuration) {
            mAnimationDuration = animationDuration;
            return this;
        }

        public Builder style(GFMinimalNotificationStyle style) {
            mStyle = style;
            return this;
        }

        public Builder title(int titleResId) {
            return title(mContext.getString(titleResId));
        }

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public Builder subtitle(int subtitleResId) {
            return subtitle(mContext.getString(subtitleResId));
        }

        public Builder subtitle(String subtitle) {
            mSubtitle = subtitle;
            return this;
        }

        public Builder leftImage(int imageRes) {
            return leftImage(mContext.getResources().getDrawable(imageRes));
        }

        public Builder leftImage(Drawable drawable) {
            mLeftImageDrawable = drawable;
            return this;
        }

        public Builder leftView(View view) {
            mLeftView = view;
            return this;
        }

        public Builder rightImage(int imageRes) {
            return rightImage(mContext.getResources().getDrawable(imageRes));
        }

        public Builder rightImage(Drawable drawable) {
            mRightImageDrawable = drawable;
            return this;
        }

        public Builder rightView(View view) {
            mRightView = view;
            return this;
        }

        public Builder backgroundColor(int color) {
            return background(new ColorDrawable(color));
        }

        public Builder background(int backgroundRes) {
            return background(mContext.getResources().getDrawable(backgroundRes));
        }

        public Builder background(Drawable drawable) {
            mBackground = drawable;
            return this;
        }

        public Builder direction(int direction) {
            mDirection = direction;
            return this;
        }

        public Builder notificationCallback(GFMinimalNotificationCallback callback) {
            mNotificationCallback = callback;
            return this;
        }

        public Builder clickListener(OnGFMinimalNotificationClickListener listener) {
            mListener = listener;
            return this;
        }

        public GFMinimalNotification build() {
            return new GFMinimalNotification(this);
        }

        public void show(ViewGroup viewGroup) {
            build().show(viewGroup);
        }
    }
}
