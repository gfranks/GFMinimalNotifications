package com.gf.minimal.notifications.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gf.minimal.notifications.activity.BaseNotificationActionBarActivity;
import com.gf.minimal.notifications.notification.GFMinimalNotification;
import com.gf.minimal.notifications.notification.GFMinimalNotificationStyle;
import com.gf.minimal.notifications.notification.OnGFMinimalNotificationClickListener;

/**
 * To use the GFMinimalNotification, you must extends one of the provided activity or fragment classes
 */
public class MainActivity extends BaseNotificationActionBarActivity implements View.OnClickListener {

    private GFMinimalNotification mNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        EditText title = (EditText) findViewById(R.id.sample_title);
        EditText subtitle = (EditText) findViewById(R.id.sample_subtitle);

        /**
         * Create Notification
         */
        mNotification = new GFMinimalNotification(this, GFMinimalNotificationStyle.DEFAULT,
                title.getText().toString(), subtitle.getText().toString());

        /**
         * Sample right image resource
         */
        mNotification.setRightImageResource(R.drawable.batman);

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
                mNotification.show(this);
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
                mNotification.show(this);
                break;
            case R.id.sample_dismiss:
                /**
                 * Dismiss Notification
                 */
                mNotification.dismiss();
                break;
            case R.id.sample_slide_from_top:
                mNotification.setSlideInFromTop();
                break;
            case R.id.sample_slide_from_bottom:
                mNotification.setSlideInFromBottom();
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
