package com.github.gfranks.minimal.notification;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.gfranks.minimal.notification.activity.BaseNotificationActionBarActivity;
import com.github.gfranks.minimal.notification.activity.BaseNotificationActivity;
import com.github.gfranks.minimal.notification.activity.BaseNotificationFragmentActivity;
import com.github.gfranks.minimal.notification.activity.BaseNotificationToolbarActivity;
import com.github.gfranks.minimal.notification.fragment.BaseNotificationFragment;
import com.github.gfranks.minimal.notification.fragment.BaseNotificationSupportFragment;

public class GFUndoNotification extends GFMinimalNotification {

    private GFUndoNotificationCallback mUndoCallback;

    /**
     * Static method to instantiate GFUndoNotification to build the notification
     * @param context Context to be used to inflate the undo notification and set additional values
     * @return GFUndoNotification
     */
    public static GFUndoNotification with(Context context) {
        return new GFUndoNotification(context);
    }

    public GFUndoNotification(Builder builder) {
        this(builder.mContext, builder.mDuration);
        setTitleText(builder.mTitle);
        setSubtitleText(builder.mSubtitle);

        mAnimationDuration = builder.mAnimationDuration;
        if (builder.mBackground != null) {
            setNotificationBackground(builder.mBackground);
        }

        if (builder.mLeftView != null) {
            setLeftView(builder.mLeftView);
        } else if (builder.mLeftImageDrawable != null) {
            setLeftImageDrawable(builder.mLeftImageDrawable);
        }

        mSlideDirection = builder.mDirection;
        mCallback = builder.mNotificationCallback;
        mUndoCallback = builder.mUndoCallback;
        mOnGFMinimalNotificationClickListener = builder.mListener;
    }

    public GFUndoNotification(Context context) {
        this(context, LENGTH_SHORT);
    }

    public GFUndoNotification(Context context, long duration) {
        mNotificationViewHeight = context.getResources().getDimensionPixelSize(R.dimen.notification_height);
        setDuration(duration);
        init(context);
    }

    public GFUndoNotification(Context context, int titleResId) {
        this(context, titleResId, LENGTH_SHORT);
    }

    public GFUndoNotification(Context context, String title) {
        this(context, title, null, LENGTH_SHORT);
    }

    public GFUndoNotification(Context context, int titleResId, int subtitleResId) {
        this(context, titleResId, subtitleResId, LENGTH_SHORT);
    }

    public GFUndoNotification(Context context, String title, String subtitle) {
        this(context, title, subtitle, LENGTH_SHORT);
    }

    public GFUndoNotification(Context context, int titleResId, long duration) {
        this(context, context.getString(titleResId), null, duration);
    }

    public GFUndoNotification(Context context, int titleResId, int subtitleResId, long duration) {
        this(context, context.getString(titleResId), context.getString(subtitleResId), duration);
    }

    public GFUndoNotification(Context context, String title, long duration) {
        this(context, title, null, duration);
    }

    public GFUndoNotification(Context context, String title, String subtitle, long duration) {
        if (duration > 0) {
            mDuration = duration;
            mCanSelfDismiss = true;
        } else {
            mCanSelfDismiss = false;
        }

        mNotificationViewHeight = context.getResources().getDimensionPixelSize(R.dimen.notification_height);
        init(context);

        setTitleText(title);
        setSubtitleText(subtitle);
    }

    /**
     * @param callback The desired callback to be fired when the notification is shown, dismissed, or when the undo action should be performed
     * @return GFUndoNotification
     */
    public GFUndoNotification setGFUndoNotificationCallback(GFUndoNotificationCallback callback) {
        mUndoCallback = callback;
        return this;
    }

    /**
     * If the notification is already showing, you may update it with a Builder. NOTE: Slide Direction cannot change in this instance if
     * notification is already showing
     *
     * @param builder Builder to build the notification view
     * @return GFUndoNotification
     */
    public GFUndoNotification updateFromBuilder(Builder builder) {
        setTitleText(builder.mTitle);
        setSubtitleText(builder.mSubtitle);

        mAnimationDuration = builder.mAnimationDuration;
        if (builder.mBackground != null) {
            setNotificationBackground(builder.mBackground);
        }

        if (builder.mLeftView != null) {
            setLeftView(builder.mLeftView);
        } else if (builder.mLeftImageDrawable != null) {
            setLeftImageDrawable(builder.mLeftImageDrawable);
        }

        mSlideDirection = builder.mDirection;
        mCallback = builder.mNotificationCallback;
        mUndoCallback = builder.mUndoCallback;
        mOnGFMinimalNotificationClickListener = builder.mListener;
        if (isShowing()) {
            doShow();
        }
        return this;
    }

    /**
     * Do to this being an undo notification, no styling may be applied. To apply styling, you may call any of the provided
     * methods to alter the notification background, alter the title or subtitle text colors/fonts, etc.
     *
     * @param style The desired GFMinimalNotificationStyle
     * @return GFUndoNotification
     */
    @Override
    public GFUndoNotification setStyle(GFMinimalNotificationStyle style) {
        // no styling available for undo notifications
        return this;
    }

    /**
     * Do to this being an undo notification, the right view is a text view and may not be altered unless you call getUndoView()
     *
     * @param imageResId The desired image resource to be set as the right view if not replaced
     * @return GFUndoNotification
     */
    @Override
    public GFMinimalNotification setRightImageResource(int imageResId) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * Do to this being an undo notification, the right view is a text view and may not be altered unless you call getUndoView()
     *
     * @param drawable The desired drawable to be set as the right view if not replaced
     * @return GFUndoNotification
     */
    @Override
    public GFUndoNotification setRightImageDrawable(Drawable drawable) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * Do to this being an undo notification, the right view is a text view and may not be altered unless you call getUndoView()
     *
     * @param visible Set the right view visible or not
     * @return GFUndoNotification
     */
    @Override
    public GFUndoNotification setRightImageVisible(boolean visible) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * @param view    The desired view to replace the current right view
     * @return GFUndoNotification
     */
    @Override
    public GFUndoNotification setRightView(View view) {
        // right view unavailable, right view is the undo option
        return this;
    }

    /**
     * Returns the undo text view so you may alter it's color, background, font, etc.
     *
     * @return TextView
     */
    public TextView getUndoView() {
        return (TextView) getRightView();
    }

    /**
     * @param v View clicked
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == mRightView.getId()) {
            if (mUndoCallback != null) {
                mUndoCallback.onUndoAction(this);
            }
        }
        super.onClick(v);
    }

    @Override
    protected int getNotificationLayout() {
        return R.layout.layout_gf_undo_notification;
    }

    @Override
    protected void initViews(Context context) {
        super.initViews(context);

        mRightView.setOnClickListener(this);
        setSlideDirection(SLIDE_BOTTOM);
        setNotificationBackgroundColor(context.getResources().getColor(R.color.gf_notification_black));
    }

    public static class Builder {

        private Context mContext;
        private long mDuration = LENGTH_SHORT;
        private long mAnimationDuration = DEFAULT_ANIMATION_DURATION;
        private String mTitle;
        private String mSubtitle;
        private Drawable mLeftImageDrawable;
        private View mLeftView;
        private Drawable mBackground;
        private int mDirection = SLIDE_BOTTOM;
        private GFMinimalNotificationCallback mNotificationCallback;
        private GFUndoNotificationCallback mUndoCallback;
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

        public Builder undoCallback(GFUndoNotificationCallback callback) {
            mUndoCallback = callback;
            return this;
        }

        public Builder clickListener(OnGFMinimalNotificationClickListener listener) {
            mListener = listener;
            return this;
        }

        public GFUndoNotification build() {
            return new GFUndoNotification(this);
        }

        public void show(Activity activity) {
            build().show((ViewGroup) activity.getWindow().getDecorView());
        }

        public void show(BaseNotificationActionBarActivity activity) {
            build().show((FrameLayout) activity.findViewById(R.id.notification_root));
        }

        public void show(BaseNotificationToolbarActivity activity) {
            build().show((FrameLayout) activity.findViewById(R.id.notification_root));
        }

        public void show(BaseNotificationFragmentActivity activity) {
            build().show((FrameLayout) activity.findViewById(R.id.notification_root));
        }

        public void show(BaseNotificationActivity activity) {
            build().show((FrameLayout) activity.findViewById(R.id.notification_root));
        }

        public void show(BaseNotificationFragment fragment) {
            build().show((FrameLayout) fragment.getView().findViewById(R.id.notification_root));
        }

        public void show(BaseNotificationSupportFragment fragment) {
            build().show((FrameLayout) fragment.getView().findViewById(R.id.notification_root));
        }

        public void show(Fragment fragment) {
            build().show(fragment);
        }

        public void show(android.app.Fragment fragment) {
            build().show(fragment);
        }
    }
}