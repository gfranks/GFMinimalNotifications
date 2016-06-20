package com.github.gfranks.minimal.notification;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GFMinimalNotification {

    private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final int COLOR_DEFAULT = 0xFF323232;
    private static final int COLOR_ERROR = 0xFFE84D3B;
    private static final int COLOR_WARNING = 0xFFEDC034;

    /**
     * Callback class for {@link GFMinimalNotification} instances.
     *
     * @see GFMinimalNotification#setCallback(GFMinimalNotification.Callback)
     */
    public static abstract class Callback {
        /** Indicates that the GFMinimalNotification was dismissed via a swipe.*/
        public static final int DISMISS_EVENT_SWIPE = 0;
        /** Indicates that the GFMinimalNotification was dismissed via an action click.*/
        public static final int DISMISS_EVENT_ACTION = 1;
        /** Indicates that the GFMinimalNotification was dismissed via a timeout.*/
        public static final int DISMISS_EVENT_TIMEOUT = 2;
        /** Indicates that the GFMinimalNotification was dismissed via a call to {@link #dismiss()}.*/
        public static final int DISMISS_EVENT_MANUAL = 3;
        /** Indicates that the GFMinimalNotification was dismissed from a new GFMinimalNotification being shown.*/
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        /** @hide */
        @IntDef({DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT,
                DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE})
        @Retention(RetentionPolicy.SOURCE)
        public @interface DismissEvent {}

        /**
         * Called when the given {@link GFMinimalNotification} is visible.
         *
         * @param notification The notification which is now visible.
         * @see GFMinimalNotification#show()
         */
        public void onShown(GFMinimalNotification notification) {
            // empty
        }

        /**
         * Called when the given {@link GFMinimalNotification} has been dismissed, either through a time-out,
         * having been manually dismissed, or an action being clicked.
         *
         * @param notification The notification which has been dismissed.
         * @param event The event which caused the dismissal. One of either:
         *              {@link #DISMISS_EVENT_SWIPE}, {@link #DISMISS_EVENT_ACTION},
         *              {@link #DISMISS_EVENT_TIMEOUT}, {@link #DISMISS_EVENT_MANUAL} or
         *              {@link #DISMISS_EVENT_CONSECUTIVE}.
         *
         * @see GFMinimalNotification#dismiss()
         */
        public void onDismissed(GFMinimalNotification notification, @GFMinimalNotification.Callback.DismissEvent int event) {
            // empty
        }
    }

    /**
     * @hide
     */
    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {}

    /**
     * @hide
     */
    @IntDef({TYPE_DEFAULT, TYPE_ERROR, TYPE_WARNING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    /**
     * Show the GFMinimalNotification indefinitely. This means that the GFMinimalNotification will be displayed from
     * the time that is {@link #show() shown} until either it is dismissed, or another GFMinimalNotification is shown.
     *
     * @see #setDuration
     */
    public static final int LENGTH_INDEFINITE = -2;

    /**
     * Show the GFMinimalNotification for a short period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_SHORT = -1;

    /**
     * Show the GFMinimalNotification for a long period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_LONG = 0;

    /**
     * Show the GFMinimalNotification with the default type settings. This means that the GFMinimalNotification
     * will be have a default background and text color.
     *
     * @see #setType
     */
    public static final int TYPE_DEFAULT = 1;

    /**
     * Show the GFMinimalNotification with the error type settings. This means that the GFMinimalNotification
     * will be have a error background and text color.
     *
     * @see #setType
     */
    public static final int TYPE_ERROR = 2;

    /**
     * Show the GFMinimalNotification with the warning type settings. This means that the GFMinimalNotification
     * will be have a warning background and text color.
     *
     * @see #setType
     */
    public static final int TYPE_WARNING = 3;

    private static final int ANIMATION_DURATION = 250;
    private static final int ANIMATION_FADE_DURATION = 180;

    private static final Handler sHandler;
    private static final int MSG_SHOW = 0;
    private static final int MSG_DISMISS = 1;

    static {
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW:
                        ((GFMinimalNotification) message.obj).showView();
                        return true;
                    case MSG_DISMISS:
                        ((GFMinimalNotification) message.obj).hideView(message.arg1);
                        return true;
                }
                return false;
            }
        });
    }

    private final ViewGroup mTargetParent;
    private final Context mContext;
    private final GFMinimalNotificationLayout mView;
    private int mDuration;
    private int mType;
    private int mCustomBackgroundColor = -1;
    private Callback mCallback;

    private final AccessibilityManager mAccessibilityManager;

    private GFMinimalNotification(ViewGroup parent) {
        mTargetParent = parent;
        mContext = parent.getContext();

        TypedArray a = mContext.obtainStyledAttributes(new int[] { R.attr.colorPrimary });
        final boolean failed = !a.hasValue(0);
        if (a != null) {
            a.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = (GFMinimalNotificationLayout) inflater.inflate(
                R.layout.layout_minimal_notification, mTargetParent, false);

        mAccessibilityManager = (AccessibilityManager)
                mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    /**
     * Make a GFMinimalNotification to display a message
     *
     * <p>GFMinimalNotification will try and find a parent view to hold GFMinimalNotification's view from the value given
     * to {@code view}. GFMinimalNotification will walk up the view tree trying to find a suitable parent,
     * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
     * whichever comes first.
     *
     * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows GFMinimalNotification to enable
     * certain features, such as swipe-to-dismiss and automatically moving of widgets like
     * {@link FloatingActionButton}.
     *
     * @param view     The view to find a parent from.
     * @param text     The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
     *                 #LENGTH_LONG}
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, @NonNull CharSequence text,
                                             @Duration int duration) {
        GFMinimalNotification notification = new GFMinimalNotification(findSuitableParent(view));
        notification.setText(text);
        notification.setDuration(duration);
        notification.setType(TYPE_DEFAULT);
        return notification;
    }

    /**
     * Make a GFMinimalNotification to display a message.
     *
     * <p>GFMinimalNotification will try and find a parent view to hold GFMinimalNotification's view from the value given
     * to {@code view}. GFMinimalNotification will walk up the view tree trying to find a suitable parent,
     * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
     * whichever comes first.
     *
     * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows GFMinimalNotification to enable
     * certain features, such as swipe-to-dismiss and automatically moving of widgets like
     * {@link FloatingActionButton}.
     *
     * @param view     The view to find a parent from.
     * @param resId    The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
     *                 #LENGTH_LONG}
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, @StringRes int resId, @Duration int duration) {
        try {
            return make(view, view.getResources().getText(resId), duration);
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
        }
        return make(view, "", duration);
    }

    /**
     * See {@link #make(View, CharSequence, int)}
     *
     * @param type The type of the notification to present. Either {@link #TYPE_DEFAULT}, {@link #TYPE_ERROR},
     * or {@link #TYPE_WARNING}
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, @NonNull CharSequence text,
                                             @Duration int duration, @Type int type) {
        GFMinimalNotification notification = make(view, text, duration);
        notification.setType(type);
        return notification;
    }

    /**
     * See {@link #make(View, int, int, int)}
     *
     * @param type The type of the notification to present. Either {@link #TYPE_DEFAULT}, {@link #TYPE_ERROR},
     * or {@link #TYPE_WARNING}
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, @StringRes int resId, @Duration int duration,
                                             @Type int type) {
        GFMinimalNotification notification;
        try {
            notification = make(view, view.getResources().getText(resId), duration);
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
            notification = make(view, "", duration);
        }
        notification.setType(type);
        return notification;
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    /**
     * Set the action to be displayed in this {@link GFMinimalNotification}.
     * Doing so removes the action image, if any
     *
     * @param resId    String resource to display
     * @param listener callback to be invoked when the action is clicked
     */
    @NonNull
    public GFMinimalNotification setAction(@StringRes int resId, OnActionClickListener listener) {
        try {
            return setAction(mContext.getText(resId), listener);
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
        }
        return this;
    }

    /**
     * Set the action to be displayed in this {@link GFMinimalNotification}.
     * Doing so removes the action image, if any
     *
     * @param text     Text to display
     * @param listener callback to be invoked when the action is clicked
     */
    @NonNull
    public GFMinimalNotification setAction(CharSequence text, final OnActionClickListener listener) {
        final TextView tv = mView.getActionTextView();

        if (TextUtils.isEmpty(text) || listener == null) {
            tv.setVisibility(View.GONE);
            tv.setOnClickListener(null);
        } else {
            mView.getActionImageView().setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setText(text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener.onActionClick(GFMinimalNotification.this)) {
                        // Now dismiss the GFMinimalNotification
                        dispatchDismiss(Callback.DISMISS_EVENT_ACTION);
                    }
                }
            });
        }
        return this;
    }

    /**
     * Sets the text color of the action specified in
     * {@link #setAction(CharSequence, OnActionClickListener)}.
     */
    @NonNull
    public GFMinimalNotification setActionTextColor(ColorStateList colors) {
        final TextView tv = mView.getActionTextView();
        tv.setTextColor(colors);
        return this;
    }

    /**
     * Sets the text color of the action specified in
     * {@link #setAction(CharSequence, OnActionClickListener)}.
     */
    @NonNull
    public GFMinimalNotification setActionTextColor(@ColorInt int color) {
        final TextView tv = mView.getActionTextView();
        tv.setTextColor(color);
        return this;
    }

    /**
     * Set the action drawable resource to be displayed in this {@link GFMinimalNotification}.
     * Doing so removes the action text, if any
     *
     * @param resId    Drawable resource to display
     * @param listener callback to be invoked when the action is clicked
     */
    @NonNull
    public GFMinimalNotification setActionImage(@DrawableRes int resId, OnActionClickListener listener) {
        try {
            return setActionImage(ContextCompat.getDrawable(mContext, resId), listener);
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
            mView.getActionImageView().setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * Set the action drawable to be displayed in this {@link GFMinimalNotification}.
     * Doing so removes the action text, if any
     *
     * @param drawable Drawable to display
     * @param listener callback to be invoked when the action is clicked
     */
    @NonNull
    public GFMinimalNotification setActionImage(Drawable drawable, final OnActionClickListener listener) {
        final ImageButton btn = mView.getActionImageView();

        if (drawable == null || listener == null) {
            btn.setVisibility(View.GONE);
            btn.setOnClickListener(null);
        } else {
            mView.getActionTextView().setVisibility(View.GONE);
            btn.setVisibility(View.VISIBLE);
            btn.setImageDrawable(drawable);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener.onActionClick(GFMinimalNotification.this)) {
                        // Now dismiss the GFMinimalNotification
                        dispatchDismiss(Callback.DISMISS_EVENT_ACTION);
                    }
                }
            });
        }
        return this;
    }

    /**
     * Set the helper drawable resource to be displayed in this {@link GFMinimalNotification}.
     *
     * @param resId Drawable resource to display as helper image
     */
    @NonNull
    public GFMinimalNotification setHelperImage(@DrawableRes int resId) {
        try {
            return setHelperImage(ContextCompat.getDrawable(mContext, resId));
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
            mView.getHelperImageView().setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * Set the helper drawable resource to be displayed in this {@link GFMinimalNotification}.
     *
     * @param drawable Drawable to display as helper image
     */
    @NonNull
    public GFMinimalNotification setHelperImage(Drawable drawable) {
        final ImageView iv = mView.getHelperImageView();

        if (drawable == null) {
            iv.setVisibility(View.GONE);
        } else {
            iv.setVisibility(View.VISIBLE);
            iv.setImageDrawable(drawable);
        }
        return this;
    }

    /**
     * Update the text in this {@link GFMinimalNotification}.
     *
     * @param message The new text for the Toast.
     */
    @NonNull
    public GFMinimalNotification setText(@NonNull CharSequence message) {
        final TextView tv = mView.getMessageView();
        tv.setText(message);
        return this;
    }

    /**
     * Update the text in this {@link GFMinimalNotification}.
     *
     * @param resId The new text for the Toast.
     */
    @NonNull
    public GFMinimalNotification setText(@StringRes int resId) {
        try {
            return setText(mContext.getText(resId));
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
        }
        return this;
    }

    /**
     * Set how long to show the view for.
     *
     * @param duration either be one of the predefined durations:
     *                 {@link #LENGTH_SHORT}, {@link #LENGTH_LONG}, or a custom duration
     *                 in milliseconds.
     */
    @NonNull
    public GFMinimalNotification setDuration(@Duration int duration) {
        mDuration = duration;
        return this;
    }

    /**
     * Return the duration.
     *
     * @see #setDuration
     */
    @Duration
    public int getDuration() {
        return mDuration;
    }

    /**
     * Set the type of the notification message to show.
     *
     * @param type either be one of the predefined types:
     *             {@link #TYPE_DEFAULT}, {@link #TYPE_ERROR}, or {@link #TYPE_WARNING}
     */
    @NonNull
    public GFMinimalNotification setType(@Type int type) {
        mType = type;
        mView.setBackgroundColor(getBackgroundColorFromType());
        setActionImageViewStateListDrawable();
        return this;
    }

    /**
     * Return the type.
     *
     * @see #setType
     */
    @Type
    public int getType() {
        return mType;
    }

    /**
     * Set a custom background color for the GFMinimalNotification
     *
     * @param customBackgroundColor The color to be set as the background of the GFMinimalNotification
     */
    public GFMinimalNotification setCustomBackgroundColor(int customBackgroundColor) {
        mCustomBackgroundColor = customBackgroundColor;
        mView.setBackgroundColor(getBackgroundColorFromType());
        setActionImageViewStateListDrawable();
        return this;
    }

    /**
     * Set a custom tint color for the helper and action image views
     *
     * @param customIconTintColor The color to be set as the tint for the helper and action image views
     */
    public GFMinimalNotification setCustomIconTintColor(int customIconTintColor) {
        mView.getHelperImageView().setColorFilter(customIconTintColor);
        mView.getActionImageView().setColorFilter(customIconTintColor);
        return this;
    }

    /**
     * Returns the {@link GFMinimalNotification}'s view.
     */
    @NonNull
    public View getView() {
        return mView;
    }

    /**
     * Show the {@link GFMinimalNotification}.
     */
    public void show() {
        GFMinimalNotificationManager.getInstance().show(mDuration, mManagerCallback);
    }

    /**
     * Dismiss the {@link GFMinimalNotification}.
     */
    public void dismiss() {
        dispatchDismiss(Callback.DISMISS_EVENT_MANUAL);
    }

    private void dispatchDismiss(@Callback.DismissEvent int event) {
        GFMinimalNotificationManager.getInstance().dismiss(mManagerCallback, event);
    }

    /**
     * Set a callback to be called when this the visibility of this {@link GFMinimalNotification} changes.
     */
    @NonNull
    public GFMinimalNotification setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    /**
     * Return whether this {@link GFMinimalNotification} is currently being shown.
     */
    public boolean isShown() {
        return GFMinimalNotificationManager.getInstance().isCurrent(mManagerCallback);
    }

    /**
     * Returns whether this {@link GFMinimalNotification} is currently being shown, or is queued to be
     * shown next.
     */
    public boolean isShownOrQueued() {
        return GFMinimalNotificationManager.getInstance().isCurrentOrNext(mManagerCallback);
    }

    private final GFMinimalNotificationManager.Callback mManagerCallback = new GFMinimalNotificationManager.Callback() {
        @Override
        public void show() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, GFMinimalNotification.this));
        }

        @Override
        public void dismiss(int event) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, GFMinimalNotification.this));
        }
    };

    private void showView() {
        if (mView.getParent() == null) {
            final ViewGroup.LayoutParams lp = mView.getLayoutParams();

            if (lp instanceof CoordinatorLayout.LayoutParams) {
                // If our LayoutParams are from a CoordinatorLayout, we'll setup our Behavior

                final Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
                    @Override
                    public void onDismiss(View view) {
                        view.setVisibility(View.GONE);
                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE);
                    }

                    @Override
                    public void onDragStateChanged(int state) {
                        switch (state) {
                            case SwipeDismissBehavior.STATE_DRAGGING:
                            case SwipeDismissBehavior.STATE_SETTLING:
                                // If the view is being dragged or settling, cancel the timeout
                                GFMinimalNotificationManager.getInstance().cancelTimeout(mManagerCallback);
                                break;
                            case SwipeDismissBehavior.STATE_IDLE:
                                // If the view has been released and is idle, restore the timeout
                                GFMinimalNotificationManager.getInstance().restoreTimeout(mManagerCallback);
                                break;
                        }
                    }
                });
                ((CoordinatorLayout.LayoutParams) lp).setBehavior(behavior);
            }

            mTargetParent.addView(mView);
        }

        mView.setOnAttachStateChangeListener(new GFMinimalNotificationLayout.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {}

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isShownOrQueued()) {
                    // If we haven't already been dismissed then this event is coming from a
                    // non-user initiated action. Hence we need to make sure that we callback
                    // and keep our state up to date. We need to post the call since removeView()
                    // will call through to onDetachedFromWindow and thus overflow.
                    sHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onViewHidden(Callback.DISMISS_EVENT_MANUAL);
                        }
                    });
                }
            }
        });

        if (ViewCompat.isLaidOut(mView)) {
            if (shouldAnimate()) {
                // If animations are enabled, animate it in
                animateViewIn();
            } else {
                // Else if animations are disabled just call back now
                onViewShown();
            }
        } else {
            // Otherwise, add one of our layout change listeners and show it in when laid out
            mView.setOnLayoutChangeListener(new GFMinimalNotificationLayout.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom) {
                    mView.setOnLayoutChangeListener(null);

                    if (shouldAnimate()) {
                        // If animations are enabled, animate it in
                        animateViewIn();
                    } else {
                        // Else if animations are disabled just call back now
                        onViewShown();
                    }
                }
            });
        }
    }

    private void animateViewIn() {
        ViewCompat.setTranslationY(mView, mView.getHeight());
        ViewCompat.animate(mView).translationY(0f)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        mView.animateChildrenIn(ANIMATION_DURATION - ANIMATION_FADE_DURATION,
                                ANIMATION_FADE_DURATION);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        if (mCallback != null) {
                            mCallback.onShown(GFMinimalNotification.this);
                        }
                        GFMinimalNotificationManager.getInstance().onShown(mManagerCallback);
                    }
                }).start();
    }

    private void animateViewOut(final int event) {
        ViewCompat.animate(mView)
                .translationY(mView.getHeight())
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        mView.animateChildrenOut(0, ANIMATION_FADE_DURATION);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        onViewHidden(event);
                    }
                }).start();
    }

    private void hideView(@Callback.DismissEvent final int event) {
        if (shouldAnimate() && mView.getVisibility() == View.VISIBLE) {
            animateViewOut(event);
        } else {
            // If animations are disabled or the view isn't visible, just call back now
            onViewHidden(event);
        }
    }

    private void onViewShown() {
        GFMinimalNotificationManager.getInstance().onShown(mManagerCallback);
        if (mCallback != null) {
            mCallback.onShown(this);
        }
    }

    private void onViewHidden(int event) {
        // First tell the Manager that it has been dismissed
        GFMinimalNotificationManager.getInstance().onDismissed(mManagerCallback);
        // Now call the dismiss listener (if available)
        if (mCallback != null) {
            mCallback.onDismissed(this, event);
        }
        // Lastly, remove the view from the parent (if attached)
        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mView);
        }
    }

    /**
     * Returns true if we should animate the GFMinimalNotification view in/out.
     */
    private boolean shouldAnimate() {
        return !mAccessibilityManager.isEnabled();
    }

    private int getBackgroundColorFromType() {
        if (mCustomBackgroundColor != -1) {
            return mCustomBackgroundColor;
        }
        switch (mType) {
            default:
            case TYPE_DEFAULT:
                return COLOR_DEFAULT;
            case TYPE_ERROR:
                return COLOR_ERROR;
            case TYPE_WARNING:
                return COLOR_WARNING;
        }
    }

    private void setActionImageViewStateListDrawable() {
        ViewCompat.setBackgroundTintList(mView.getActionImageView(), new ColorStateList(new int[][] {{0}},
                new int[] {getBackgroundColorFromType()}));
    }

    public interface OnActionClickListener {

        /**
         * Called when the given {@link GFMinimalNotification} action view (text or image) has been clicked.
         * Will dismiss if returned true
         *
         * @param notification The notification which has received the action click (text or image)
         * @return true if you wish to dismiss the notification immediately or false to allow it to dismiss normally
         */
        boolean onActionClick(GFMinimalNotification notification);
    }

    private final class Behavior extends SwipeDismissBehavior<GFMinimalNotificationLayout> {
        @Override
        public boolean canSwipeDismissView(@NonNull View child) {
            return child instanceof GFMinimalNotificationLayout;
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, GFMinimalNotificationLayout child,
                                             MotionEvent event) {
            // We want to make sure that we disable any GFMinimalNotification timeouts if the user is
            // currently touching the GFMinimalNotification. We restore the timeout when complete
            if (parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY())) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        GFMinimalNotificationManager.getInstance().cancelTimeout(mManagerCallback);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        GFMinimalNotificationManager.getInstance().restoreTimeout(mManagerCallback);
                        break;
                }
            }

            return super.onInterceptTouchEvent(parent, child, event);
        }
    }
}
