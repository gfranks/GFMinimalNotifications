package com.github.gfranks.minimal.notification.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.minimal.notification.GFMinimalNotificationStyle;
import com.github.gfranks.minimal.notification.OnGFMinimalNotificationClickListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GFMinimalNotification mNotification;
    private FrameLayout mNotificationRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sample_toolbar);
        setSupportActionBar(toolbar);

        mNotificationRoot = (FrameLayout) findViewById(R.id.sample_notification_container);
        Button show = (Button) findViewById(R.id.sample_show);
        Button showNoDuration = (Button) findViewById(R.id.sample_show_no_duration);
        Button dismiss = (Button) findViewById(R.id.sample_dismiss);
        Button slideInTop = (Button) findViewById(R.id.sample_slide_from_top);
        Button slideInBottom = (Button) findViewById(R.id.sample_slide_from_bottom);
        Button styleError = (Button) findViewById(R.id.sample_style_error);
        Button styleSuccess = (Button) findViewById(R.id.sample_style_success);
        Button styleInfo = (Button) findViewById(R.id.sample_style_info);
        Button styleDefault = (Button) findViewById(R.id.sample_style_default);
        Button styleWarning = (Button) findViewById(R.id.sample_style_warning);
        Button setLeftView = (Button) findViewById(R.id.sample_set_left_view);
        Button removeLeftView = (Button) findViewById(R.id.sample_remove_left_view);
        Button setRightView = (Button) findViewById(R.id.sample_set_right_view);
        Button removeRightView = (Button) findViewById(R.id.sample_remove_right_view);

        show.setOnClickListener(this);
        showNoDuration.setOnClickListener(this);
        dismiss.setOnClickListener(this);
        slideInTop.setOnClickListener(this);
        slideInBottom.setOnClickListener(this);
        styleError.setOnClickListener(this);
        styleSuccess.setOnClickListener(this);
        styleInfo.setOnClickListener(this);
        styleDefault.setOnClickListener(this);
        styleWarning.setOnClickListener(this);
        setLeftView.setOnClickListener(this);
        removeLeftView.setOnClickListener(this);
        setRightView.setOnClickListener(this);
        removeRightView.setOnClickListener(this);

        /**
         * Create Notification
         */
        mNotification = GFMinimalNotification.with(this);

        /**
         * Sample right image resource
         */
        ImageView rightImageView = new ImageView(this);
        rightImageView.setImageResource(R.drawable.batman);
        mNotification.setRightView(rightImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setTitle(getString(R.string.activity_name));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sample_show:
                mNotification.setTitleText(((EditText) findViewById(R.id.sample_title)).getText().toString());
                mNotification.setSubtitleText(((EditText) findViewById(R.id.sample_subtitle)).getText().toString());
                mNotification.setDuration(GFMinimalNotification.LENGTH_SHORT);
                /**
                 * Show Notification
                 */
                mNotification.show(mNotificationRoot);
                break;
            case R.id.sample_show_no_duration:
                mNotification.setTitleText(((EditText) findViewById(R.id.sample_title)).getText().toString());
                mNotification.setSubtitleText(((EditText) findViewById(R.id.sample_subtitle)).getText().toString());
                mNotification.setDuration(0);
                mNotification.setOnGFMinimalNotificationClickListener(new OnGFMinimalNotificationClickListener() {
                    @Override
                    public void onClick(GFMinimalNotification notification) {
                        notification.setOnGFMinimalNotificationClickListener(null);
                        notification.dismiss();
                    }
                });
                /**
                 * Show Notification
                 */
                mNotification.show(mNotificationRoot);
                break;
            case R.id.sample_dismiss:
                /**
                 * Dismiss Notification
                 */
                mNotification.dismiss();
                break;
            case R.id.sample_slide_from_top:
                mNotification.setSlideDirection(GFMinimalNotification.SLIDE_TOP);
                break;
            case R.id.sample_slide_from_bottom:
                mNotification.setSlideDirection(GFMinimalNotification.SLIDE_BOTTOM);
                break;
            case R.id.sample_style_error:
                mNotification.setStyle(GFMinimalNotificationStyle.ERROR);
                break;
            case R.id.sample_style_success:
                mNotification.setStyle(GFMinimalNotificationStyle.SUCCESS);
                break;
            case R.id.sample_style_info:
                mNotification.setStyle(GFMinimalNotificationStyle.INFO);
                break;
            case R.id.sample_style_default:
                mNotification.setStyle(GFMinimalNotificationStyle.DEFAULT);
                break;
            case R.id.sample_style_warning:
                mNotification.setStyle(GFMinimalNotificationStyle.WARNING);
                break;
            case R.id.sample_set_left_view:
                mNotification.setLeftImageVisible(true);
                break;
            case R.id.sample_remove_left_view:
                mNotification.setLeftImageVisible(false);
                break;
            case R.id.sample_set_right_view:
                mNotification.setRightImageVisible(true);
                break;
            case R.id.sample_remove_right_view:
                mNotification.setRightImageVisible(false);
                break;
        }
    }
}
