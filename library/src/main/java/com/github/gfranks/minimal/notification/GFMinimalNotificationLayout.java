package com.github.gfranks.minimal.notification;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

class GFMinimalNotificationLayout extends LinearLayout {

    private ImageView mHelperImageView;
    private TextView mMessageView;
    private Button mActionTextView;
    private ImageButton mActionImageView;

    private int mMaxWidth;
    private int mMaxInlineActionWidth;

    interface OnLayoutChangeListener {
        void onLayoutChange(View view, int left, int top, int right, int bottom);
    }

    interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View v);
        void onViewDetachedFromWindow(View v);
    }

    private OnLayoutChangeListener mOnLayoutChangeListener;
    private OnAttachStateChangeListener mOnAttachStateChangeListener;

    public GFMinimalNotificationLayout(Context context) {
        this(context, null);
    }

    public GFMinimalNotificationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GFMinimalNotificationLayout);
        mMaxWidth = a.getDimensionPixelSize(R.styleable.GFMinimalNotificationLayout_android_maxWidth, -1);
        mMaxInlineActionWidth = a.getDimensionPixelSize(
                R.styleable.GFMinimalNotificationLayout_fbl_maxActionInlineWidth, -1);
        if (a.hasValue(R.styleable.GFMinimalNotificationLayout_elevation)) {
            ViewCompat.setElevation(this, a.getDimensionPixelSize(
                    R.styleable.GFMinimalNotificationLayout_elevation, 0));
        }
        a.recycle();

        setClickable(true);

        // Now inflate our content. We need to do this manually rather than using an <include>
        // in the layout since older versions of the Android do not inflate includes with
        // the correct Context.
        LayoutInflater.from(context).inflate(R.layout.layout_minimal_notification_include, this);

        ViewCompat.setAccessibilityLiveRegion(this,
                ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
        ViewCompat.setImportantForAccessibility(this,
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHelperImageView = (ImageView) findViewById(R.id.notification_helper_image);
        mMessageView = (TextView) findViewById(R.id.notification_text);
        mActionTextView = (Button) findViewById(R.id.notification_action_text);
        mActionImageView = (ImageButton) findViewById(R.id.notification_action_image);
    }

    ImageView getHelperImageView() {
        return mHelperImageView;
    }

    TextView getMessageView() {
        return mMessageView;
    }

    Button getActionTextView() {
        return mActionTextView;
    }

    ImageButton getActionImageView() {
        return mActionImageView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMaxWidth > 0 && getMeasuredWidth() > mMaxWidth) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        float density = getResources().getDisplayMetrics().density;
        final int multiLineVPadding = (int) (24f * density);
        final int singleLineVPadding = (int) (14f * density);
        final boolean isMultiLine = mMessageView.getLayout().getLineCount() > 1;

        boolean remeasure = false;
        if (isMultiLine && mMaxInlineActionWidth > 0
                && mHelperImageView.getMeasuredWidth() > mMaxInlineActionWidth
                && (mActionTextView.getMeasuredWidth() > mMaxInlineActionWidth
                || mActionImageView.getMeasuredWidth() > mMaxInlineActionWidth)) {
            if (updateViewsWithinLayout(VERTICAL, multiLineVPadding,
                    multiLineVPadding - singleLineVPadding)) {
                remeasure = true;
            }
        } else {
            final int messagePadding = isMultiLine ? multiLineVPadding : singleLineVPadding;
            if (updateViewsWithinLayout(HORIZONTAL, messagePadding, messagePadding)) {
                remeasure = true;
            }
        }

        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    void animateChildrenIn(int delay, int duration) {
        ViewCompat.setAlpha(mMessageView, 0f);
        ViewCompat.animate(mMessageView).alpha(1f).setDuration(duration)
                .setStartDelay(delay).start();

        if (mHelperImageView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mHelperImageView, 0f);
            ViewCompat.animate(mHelperImageView).alpha(1f).setDuration(duration)
                    .setStartDelay(delay).start();
        }

        if (mActionTextView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mActionTextView, 0f);
            ViewCompat.animate(mActionTextView).alpha(1f).setDuration(duration)
                    .setStartDelay(delay).start();
        }

        if (mActionImageView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mActionImageView, 0f);
            ViewCompat.animate(mActionImageView).alpha(1f).setDuration(duration)
                    .setStartDelay(delay).start();
        }
    }

    void animateChildrenOut(int delay, int duration) {
        ViewCompat.setAlpha(mMessageView, 1f);
        ViewCompat.animate(mMessageView).alpha(0f).setDuration(duration)
                .setStartDelay(delay).start();

        if (mHelperImageView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mHelperImageView, 1f);
            ViewCompat.animate(mHelperImageView).alpha(0f).setDuration(duration)
                    .setStartDelay(delay).start();
        }

        if (mActionTextView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mActionTextView, 1f);
            ViewCompat.animate(mActionTextView).alpha(0f).setDuration(duration)
                    .setStartDelay(delay).start();
        }

        if (mActionImageView.getVisibility() == VISIBLE) {
            ViewCompat.setAlpha(mActionImageView, 1f);
            ViewCompat.animate(mActionImageView).alpha(0f).setDuration(duration)
                    .setStartDelay(delay).start();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mOnLayoutChangeListener != null) {
            mOnLayoutChangeListener.onLayoutChange(this, l, t, r, b);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOnAttachStateChangeListener != null) {
            mOnAttachStateChangeListener.onViewAttachedToWindow(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mOnAttachStateChangeListener != null) {
            mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
        }
    }

    void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
        mOnLayoutChangeListener = onLayoutChangeListener;
    }

    void setOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        mOnAttachStateChangeListener = listener;
    }

    private boolean updateViewsWithinLayout(final int orientation,
                                            final int messagePadTop, final int messagePadBottom) {
        boolean changed = false;
        if (orientation != getOrientation()) {
            setOrientation(orientation);
            changed = true;
        }
        if (mMessageView.getPaddingTop() != messagePadTop
                || mMessageView.getPaddingBottom() != messagePadBottom) {
            updateTopBottomPadding(mMessageView, messagePadTop, messagePadBottom);
            changed = true;
        }
        return changed;
    }

    private static void updateTopBottomPadding(View view, int topPadding, int bottomPadding) {
        if (ViewCompat.isPaddingRelative(view)) {
            ViewCompat.setPaddingRelative(view,
                    ViewCompat.getPaddingStart(view), topPadding,
                    ViewCompat.getPaddingEnd(view), bottomPadding);
        } else {
            view.setPadding(view.getPaddingLeft(), topPadding,
                    view.getPaddingRight(), bottomPadding);
        }
    }
}
