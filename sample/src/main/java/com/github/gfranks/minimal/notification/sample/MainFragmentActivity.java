package com.github.gfranks.minimal.notification.sample;

import android.os.Bundle;
import android.view.MenuItem;

import com.github.gfranks.minimal.notification.activity.BaseNotificationToolbarActivity;

/**
 * Sample Activity using a Fragment to handle the notification
 */
public class MainFragmentActivity extends BaseNotificationToolbarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.fragment_activity_name));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
