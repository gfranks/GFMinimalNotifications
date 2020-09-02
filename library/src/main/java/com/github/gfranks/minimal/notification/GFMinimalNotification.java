package com.github.gfranks.minimal.notification;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GFMinimalNotification {

    private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
    private static @ColorInt int COLOR_DEFAULT = 0xFF323232;
    private static @ColorInt int COLOR_ERROR = 0xFFE84D3B;
    private static @ColorInt int COLOR_WARNING = 0xFFEDC034;
    private static int DIRECTION_DEFAULT = GFMinimalNotification.DIRECTION_BOTTOM;

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
    @IntDef({TYPE_DEFAULT, TYPE_ERROR, TYPE_WARNING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    /**
     * @hide
     */
    @IntDef({DIRECTION_TOP, DIRECTION_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {}

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
     * Show the GFMinimalNotification for an extra long period of time.
     *
     * @see #setDuration
     */
    public static final int LENGTH_EXTRA_LONG = 1;

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

    /**
     * Show the notification from the top of the container layout found
     *
     * @see #setDirection
     */
    public static final int DIRECTION_TOP = 1;

    /**
     * Show the notification from the bottom of the container layout found
     *
     * @see #setDirection
     */
    public static final int DIRECTION_BOTTOM = 2;

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
    private @Type int mType;
    private @ColorInt int mCustomBackgroundColor = -1;
    private @Direction int mDirection = DIRECTION_DEFAULT;
    private Callback mCallback;

    private final AccessibilityManager mAccessibilityManager;

    private GFMinimalNotification(ViewGroup parent) {
        mTargetParent = parent;
        mContext = parent.getContext();

        TypedArray a = mContext.obtainStyledAttributes(new int[] { R.attr.colorPrimary });
        final boolean failed = !a.hasValue(0);
        a.recycle();
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = (GFMinimalNotificationLayout) inflater.inflate(R.layout.layout_minimal_notification, mTargetParent, false);

        mAccessibilityManager = (AccessibilityManager)
                mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);

        resolveThemesAttributes();
        setDuration(LENGTH_LONG);
        setType(TYPE_DEFAULT);
    }

    private GFMinimalNotification(ViewGroup parent, @LayoutRes int customViewResId) {
        this(parent);
        setCustomView(customViewResId);
    }

    private GFMinimalNotification(ViewGroup parent, View customView) {
        this(parent);
        setCustomView(customView);
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
     * @param view The view to find a parent from.
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view) {
        return new GFMinimalNotification(findSuitableParent(view));
    }

    /**
     * See {@link #make(View)}
     *
     * @param text     The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
     *                 #LENGTH_LONG}
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, @NonNull CharSequence text,
                                             int duration) {
        GFMinimalNotification notification = make(view);
        notification.setText(text);
        notification.setDuration(duration);
        return notification;
    }

    /**
     * See {@link #make(View)}
     *
     * @param resId    The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
     *                 #LENGTH_LONG}
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, @StringRes int resId, int duration) {
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
                                             int duration, @Type int type) {
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
    public static GFMinimalNotification make(@NonNull View view, @StringRes int resId, int duration,
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

    /**
     * See {@link #make(View)}
     *
     * @param view            The view to find a parent from.
     * @param customViewResId The custom view resource id to be inflated and used as the notification
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, @LayoutRes int customViewResId) {
        return new GFMinimalNotification(findSuitableParent(view), customViewResId);
    }

    /**
     * See {@link #make(View, int)}
     *
     * @param customView The custom view to be used as the notification
     */
    @NonNull
    public static GFMinimalNotification make(@NonNull View view, View customView) {
        return new GFMinimalNotification(findSuitableParent(view), customView);
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
     * See {@link #setAction(int, OnActionClickListener)}
     */
    @NonNull
    public GFMinimalNotification setAction(CharSequence text, final OnActionClickListener listener) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not set the action text when using a custom view");
        }

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
     * Set the text color of the action specified in
     * {@link #setAction(int, OnActionClickListener)}.
     *
     * @param colors ColorStateList to apply to the action image button
     */
    @NonNull
    public GFMinimalNotification setActionTextColor(ColorStateList colors) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not apply an action text color when using a custom view");
        }

        final TextView tv = mView.getActionTextView();
        tv.setTextColor(colors);
        return this;
    }

    /**
     * See {@link #setActionTextColor(ColorStateList)}
     */
    @NonNull
    public GFMinimalNotification setActionTextColor(@ColorInt int color) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not apply an action text color when using a custom view");
        }

        final TextView tv = mView.getActionTextView();
        tv.setTextColor(color);
        return this;
    }

	/**
	 * Set the default action text size to the given value, interpreted as "scaled
	 * pixel" units.  This size is adjusted based on the current density and
	 * user font size preference.
	 *
	 * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
	 *
	 * @param size The scaled pixel size.
	 *
	 */
	@NonNull
	public GFMinimalNotification setActionTextSize(float size) {
		if (mView.hasCustomView()) {
			throw new IllegalStateException("You may not apply a custom action text size when using a custom view");
		}

		final TextView tv = mView.getActionTextView();
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		return this;
	}

	/**
	 * Sets the typeface and style in which the action text should be displayed,
	 * and turns on the fake bold and italic bits in the Paint if the
	 *
	 * @param typeface Typeface that you provided does not have all the bits in the
	 */
	@NonNull
	public GFMinimalNotification setActionTypeface(Typeface typeface) {
		if (mView.hasCustomView()) {
			throw new IllegalStateException("You may not apply an action text typeface when using a custom view");
		}

		final TextView tv = mView.getActionTextView();
		tv.setTypeface(typeface);
		return this;
	}

	/**
	 * Sets the typeface and style in which the text should be displayed,
	 * and turns on the fake bold and italic bits in the Paint if the
	 *
	 * @param typeface Typeface that you provided does not have all the bits in the
	 * @param style that you specified.
	 */
	@NonNull
	public GFMinimalNotification setActionTypeface(Typeface typeface, int style) {
		if (mView.hasCustomView()) {
			throw new IllegalStateException("You may not apply an action text typeface when using a custom view");
		}

		final TextView tv = mView.getActionTextView();
		tv.setTypeface(typeface, style);
		return this;
	}

    /**
     * Set the action drawable resource to be displayed in this {@link GFMinimalNotification}.
     * Doing so removes the action text, if any
     *
     * @param actionResId Drawable resource to display
     * @param listener    Callback to be invoked when the action is clicked
     */
    @NonNull
    public GFMinimalNotification setActionImage(@DrawableRes int actionResId, OnActionClickListener listener) {
        try {
            return setActionImage(ContextCompat.getDrawable(mContext, actionResId), listener);
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
            mView.getActionImageView().setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * See {@link #setActionImage(int, OnActionClickListener)}
     */
    @NonNull
    public GFMinimalNotification setActionImage(Drawable drawable, final OnActionClickListener listener) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not apply an action image when using a custom view");
        }

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
     * See {@link #setHelperImage(int)}
     */
    @NonNull
    public GFMinimalNotification setHelperImage(Drawable drawable) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not apply a helper image when using a custom view");
        }

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
     * @param textResId The new text for the notification.
     */
    @NonNull
    public GFMinimalNotification setText(@StringRes int textResId) {
        try {
            return setText(mContext.getText(textResId));
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
        }
        return this;
    }

    /**
     * See {@link #setText(int)}
     */
    @NonNull
    public GFMinimalNotification setText(@NonNull CharSequence text) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not set the text when using a custom view");
        }

        final TextView tv = mView.getMessageView();
        tv.setText(text);
        return this;
    }

    /**
     * Update the text appearance in this {@link GFMinimalNotification}.
     *
     * @param resId The new text appearance for the notification.
     */
    @NonNull
    public GFMinimalNotification setTextAppearance(@StyleRes int resId) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not apply a custom text appearance when using a custom view");
        }

        try {
            TextView tv = mView.getMessageView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAppearance(resId);
            } else {
                tv.setTextAppearance(mContext, resId);
            }
            return this;
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
        }
        return this;
    }

	/**
	 * Set the default text size to the given value, interpreted as "scaled
	 * pixel" units.  This size is adjusted based on the current density and
	 * user font size preference.
	 *
	 * <p>Note: if this TextView has the auto-size feature enabled than this function is no-op.
	 *
	 * @param size The scaled pixel size.
	 *
	 */
	@NonNull
	public GFMinimalNotification setTextSize(float size) {
		if (mView.hasCustomView()) {
			throw new IllegalStateException("You may not apply a custom text size when using a custom view");
		}

		TextView tv = mView.getMessageView();
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
		return this;
	}

	/**
	 * Sets the typeface and style in which the text should be displayed,
	 * and turns on the fake bold and italic bits in the Paint if the
	 *
	 * @param typeface Typeface that you provided does not have all the bits in the
	 */
	@NonNull
	public GFMinimalNotification setTextTypeface(Typeface typeface) {
		if (mView.hasCustomView()) {
			throw new IllegalStateException("You may not apply a custom text typeface when using a custom view");
		}

		TextView tv = mView.getMessageView();
		tv.setTypeface(typeface);
		return this;
	}

	/**
	 * Sets the typeface and style in which the text should be displayed,
	 * and turns on the fake bold and italic bits in the Paint if the
	 *
	 * @param typeface Typeface that you provided does not have all the bits in the
	 * @param style that you specified.
	 */
	@NonNull
	public GFMinimalNotification setTextTypeface(Typeface typeface, int style) {
		if (mView.hasCustomView()) {
			throw new IllegalStateException("You may not apply a custom text typeface when using a custom view");
		}

		TextView tv = mView.getMessageView();
		tv.setTypeface(typeface, style);
		return this;
	}

    /**
     * Update the maximum number of lines allowed in the notification
     *
     * @param maxLines The new number of max lines (defaults to 2)
     */
    @NonNull
    public GFMinimalNotification setMaxLines(int maxLines) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not set max lines when using a custom view");
        }

        mView.getMessageView().setMaxLines(maxLines);

        return this;
    }

    /**
     * Apply a custom view to the notification. Doing so will remove all internal views, however, you may still set the
     * notification type or apply a custom background color.
     *
     * @param customViewResId The custom view resource id to be inflated and used as the notification
     */
    public GFMinimalNotification setCustomView(@LayoutRes int customViewResId) {
        mView.updateWithCustomView(customViewResId);

        return this;
    }

    /**
     * See {@link #setCustomView(int)}
     *
     * @param customView The custom view to be used as the notification
     */
    public GFMinimalNotification setCustomView(View customView) {
        mView.updateWithCustomView(customView);

        return this;
    }

    /**
     * Set how long to show the view for.
     *
     * @param duration Either one of the predefined durations:
     *                 {@link #LENGTH_SHORT}, {@link #LENGTH_LONG}, or a custom duration
     *                 in milliseconds.
     */
    @NonNull
    public GFMinimalNotification setDuration(int duration) {
        mDuration = duration;
        return this;
    }

    /**
     * Return the duration.
     *
     * @see #setDuration
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * Set the direction the notification should animate in from
     *
     * @param direction Either one of the predefined directions:
     *                  {@link #DIRECTION_TOP} or {@link #DIRECTION_BOTTOM}
     */
    public GFMinimalNotification setDirection(@Direction int direction) {
        if (direction == mDirection) {
            // no need to update, we are already matched
            return this;
        }

        ViewGroup.LayoutParams lp = mView.getLayoutParams();
        if (lp instanceof CoordinatorLayout.LayoutParams) {
            ((CoordinatorLayout.LayoutParams) lp).gravity = direction == DIRECTION_TOP ? Gravity.TOP : Gravity.BOTTOM;
        } else if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).gravity = direction == DIRECTION_TOP ? Gravity.TOP : Gravity.BOTTOM;
        } else if (lp instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) lp).gravity = direction == DIRECTION_TOP ? Gravity.TOP : Gravity.BOTTOM;
        } else if (lp instanceof RelativeLayout.LayoutParams) {
            if (direction == DIRECTION_TOP) {
                ((RelativeLayout.LayoutParams) lp).addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                ((RelativeLayout.LayoutParams) lp).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
        } else {
            // cannot set direction
            return this;
        }

        mView.setLayoutParams(lp);

        mDirection = direction;
        return this;
    }

    /**
     * Return the current direction
     *
     * @see #setDirection
     */
    @Direction
    public int getDirection() {
        return mDirection;
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
        return this;
    }

    /**
     * Set a custom tint color for the helper and action image views
     *
     * @param customIconTintColor The color to be set as the tint for the helper and action image views
     */
    public GFMinimalNotification setCustomIconTintColor(int customIconTintColor) {
        if (mView.hasCustomView()) {
            throw new IllegalStateException("You may not apply a custom icon tint color when using a custom view");
        }

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
     *
     * @return a boolean determining if this notification is using a custom view
     */
    public boolean isUsingCustomView() {
        return mView.hasCustomView();
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

    private void dispatchDismiss(@Callback.DismissEvent int event) {
        GFMinimalNotificationManager.getInstance().dismiss(mManagerCallback, event);
    }

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
        ViewCompat.setTranslationY(mView, mDirection == DIRECTION_TOP ? -mView.getHeight() : mView.getHeight());
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
                .translationY(mDirection == DIRECTION_TOP ? -mView.getHeight() : mView.getHeight())
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

    private void resolveThemesAttributes() {
        TypedArray a = mContext.obtainStyledAttributes(R.styleable.GFMinimalNotificationTheme);
        if (a != null) {
            if (a.hasValue(R.styleable.GFMinimalNotificationTheme_gf_notification_type_default)) {
                COLOR_DEFAULT = a.getColor(R.styleable.GFMinimalNotificationTheme_gf_notification_type_default, COLOR_DEFAULT);
            }
            if (a.hasValue(R.styleable.GFMinimalNotificationTheme_gf_notification_type_error)) {
                COLOR_ERROR = a.getColor(R.styleable.GFMinimalNotificationTheme_gf_notification_type_error, COLOR_ERROR);
            }
            if (a.hasValue(R.styleable.GFMinimalNotificationTheme_gf_notification_type_warning)) {
                COLOR_WARNING = a.getColor(R.styleable.GFMinimalNotificationTheme_gf_notification_type_warning, COLOR_WARNING);
            }
            if (a.hasValue(R.styleable.GFMinimalNotificationTheme_gf_notification_textAppearance)) {
                setTextAppearance(a.getResourceId(R.styleable.GFMinimalNotificationTheme_gf_notification_textAppearance, -1));
            }
            if (a.hasValue(R.styleable.GFMinimalNotificationTheme_gf_notification_maxLines)) {
                setMaxLines(a.getInt(R.styleable.GFMinimalNotificationTheme_gf_notification_maxLines, 2));
            }
            if (a.hasValue(R.styleable.GFMinimalNotificationTheme_gf_notification_direction)) {
                DIRECTION_DEFAULT = a.getInt(R.styleable.GFMinimalNotificationTheme_gf_notification_direction, DIRECTION_DEFAULT);
            }
            a.recycle();
        }

        setDirection(DIRECTION_DEFAULT == DIRECTION_TOP ? DIRECTION_TOP : DIRECTION_BOTTOM);
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
