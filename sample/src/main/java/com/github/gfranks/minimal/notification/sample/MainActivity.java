package com.github.gfranks.minimal.notification.sample;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.gfranks.minimal.notification.GFMinimalNotification;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.sample_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.sample_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sample_text)
    EditText mText;
    @BindView(R.id.sample_action)
    EditText mAction;
    @BindView(R.id.sample_use_action_text)
    Button mUseActionTextButton;

    private int mDirection = GFMinimalNotification.DIRECTION_BOTTOM;
    private int mType = GFMinimalNotification.TYPE_DEFAULT;
    private int mHelperResId = -1;
    private int mActionResId = -1;
    private boolean mUseActionText;

    private GFMinimalNotification mCurrentNotification;

    /*
     * Just to not, this is not necessarily the proper way to construct and show the GFMinimalNotification.
     * Normally, you would create and show inline and ignore the instance, not saving it to a property value.
     * However, it can be done this way, but after a notification has been shown, it cannot be re-shown.
     * Example: GFMinimalNotification.make(view, text, duration, type).show();
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
    }

    @OnClick({R.id.sample_show, R.id.sample_show_no_duration, R.id.sample_dismiss, R.id.sample_slide_from_top, R.id.sample_slide_from_bottom,
              R.id.sample_type_error, R.id.sample_type_default, R.id.sample_type_warning, R.id.sample_set_left_view,
              R.id.sample_remove_left_view, R.id.sample_set_right_view, R.id.sample_remove_right_view, R.id.sample_use_action_text,
              R.id.sample_use_custom_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sample_show:
                mCurrentNotification = GFMinimalNotification.make(mCoordinatorLayout, mText.getText().toString(), GFMinimalNotification.LENGTH_LONG, mType);
                mCurrentNotification.setDirection(mDirection);
                mCurrentNotification.setHelperImage(mHelperResId);
                mCurrentNotification.setActionImage(mActionResId, new GFMinimalNotification.OnActionClickListener() {
                    @Override
                    public boolean onActionClick(GFMinimalNotification notification) {
                        return true;
                    }
                });
                if (mUseActionText) {
                    mCurrentNotification.setAction(mAction.getText().toString(), new GFMinimalNotification.OnActionClickListener() {
                        @Override
                        public boolean onActionClick(GFMinimalNotification notification) {
                            return true;
                        }
                    });
                }
                mCurrentNotification.show();
                break;
            case R.id.sample_show_no_duration:
                mCurrentNotification = GFMinimalNotification.make(mCoordinatorLayout, mText.getText().toString(), GFMinimalNotification.LENGTH_INDEFINITE, mType);
                mCurrentNotification.setDirection(mDirection);
                mCurrentNotification.setHelperImage(mHelperResId);
                mCurrentNotification.setActionImage(mActionResId, new GFMinimalNotification.OnActionClickListener() {
                    @Override
                    public boolean onActionClick(GFMinimalNotification notification) {
                        return true;
                    }
                });
                if (mUseActionText) {
                    mCurrentNotification.setAction(mAction.getText().toString(), new GFMinimalNotification.OnActionClickListener() {
                        @Override
                        public boolean onActionClick(GFMinimalNotification notification) {
                            return true;
                        }
                    });
                }
                mCurrentNotification.show();
                break;
            case R.id.sample_slide_from_top:
                mDirection = GFMinimalNotification.DIRECTION_TOP;
                break;
            case R.id.sample_slide_from_bottom:
                mDirection = GFMinimalNotification.DIRECTION_BOTTOM;
                break;
            case R.id.sample_dismiss:
                if (mCurrentNotification != null) {
                    mCurrentNotification.dismiss();
                }
                break;
            case R.id.sample_type_error:
                mType = GFMinimalNotification.TYPE_ERROR;
                if (mCurrentNotification != null && mCurrentNotification.isShown()) {
                    mCurrentNotification.setType(mType);
                }
                break;
            case R.id.sample_type_default:
                mType = GFMinimalNotification.TYPE_DEFAULT;
                if (mCurrentNotification != null && mCurrentNotification.isShown()) {
                    mCurrentNotification.setType(mType);
                }
                break;
            case R.id.sample_type_warning:
                mType = GFMinimalNotification.TYPE_WARNING;
                if (mCurrentNotification != null && mCurrentNotification.isShown()) {
                    mCurrentNotification.setType(mType);
                }
                break;
            case R.id.sample_set_left_view:
                mHelperResId = R.drawable.ic_heart;
                if (mCurrentNotification != null && mCurrentNotification.isShown()) {
                    mCurrentNotification.setHelperImage(mHelperResId);
                }
                break;
            case R.id.sample_remove_left_view:
                mHelperResId = -1;
                if (mCurrentNotification != null && mCurrentNotification.isShown()) {
                    mCurrentNotification.setHelperImage(mHelperResId);
                }
                break;
            case R.id.sample_set_right_view:
                mActionResId = R.drawable.ic_done;
                mUseActionText = false;
                mUseActionTextButton.setText(R.string.use_action_text);
                if (mCurrentNotification != null && mCurrentNotification.isShown()) {
                    mCurrentNotification.setActionImage(mActionResId, new GFMinimalNotification.OnActionClickListener() {
                        @Override
                        public boolean onActionClick(GFMinimalNotification notification) {
                            return true;
                        }
                    });
                }
                break;
            case R.id.sample_remove_right_view:
                mActionResId = -1;
                mCurrentNotification.setActionImage(mActionResId, null);
                break;
            case R.id.sample_use_action_text:
                mUseActionText = !mUseActionText;
                if (mCurrentNotification != null && mCurrentNotification.isShown()) {
                    mCurrentNotification.setAction(mAction.getText().toString(), new GFMinimalNotification.OnActionClickListener() {
                        @Override
                        public boolean onActionClick(GFMinimalNotification notification) {
                            return true;
                        }
                    });
                }
                if (mUseActionText) {
                    mUseActionTextButton.setText(R.string.remove_action_text);
                } else {
                    mUseActionTextButton.setText(R.string.use_action_text);
                }
                break;
            case R.id.sample_use_custom_view:
                mCurrentNotification = GFMinimalNotification.make(mCoordinatorLayout, R.layout.layout_custom_view);
                mCurrentNotification.setDirection(mDirection);
                mCurrentNotification.setType(mType);
                mCurrentNotification.getView().findViewById(R.id.close_custom_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentNotification.dismiss();
                    }
                });
                mCurrentNotification.show();
                break;
        }
    }
}
