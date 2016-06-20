package com.github.gfranks.minimal.notification.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.gfranks.minimal.notification.GFMinimalNotification;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.sample_coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @InjectView(R.id.sample_toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.sample_text)
    EditText mText;
    @InjectView(R.id.sample_action)
    EditText mAction;
    @InjectView(R.id.sample_use_action_text)
    Button mUseActionTextButton;

    private int mDuration = GFMinimalNotification.LENGTH_LONG;
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
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_fragment_sample) {
            startActivity(new Intent(this, MainFragmentActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.sample_show, R.id.sample_show_no_duration, R.id.sample_dismiss, R.id.sample_type_error, R.id.sample_type_default,
              R.id.sample_type_warning, R.id.sample_set_left_view, R.id.sample_remove_left_view, R.id.sample_set_right_view, R.id.sample_remove_right_view,
              R.id.sample_use_action_text})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sample_show:
                mDuration = GFMinimalNotification.LENGTH_LONG;
                mCurrentNotification = GFMinimalNotification.make(mCoordinatorLayout, mText.getText().toString(), mDuration, mType);
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
                mDuration = GFMinimalNotification.LENGTH_INDEFINITE;
                mCurrentNotification = GFMinimalNotification.make(mCoordinatorLayout, mText.getText().toString(), mDuration, mType);
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
        }
    }
}
