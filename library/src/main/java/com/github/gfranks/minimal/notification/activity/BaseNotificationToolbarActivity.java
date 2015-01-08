package com.github.gfranks.minimal.notification.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.minimal.notification.R;

public class BaseNotificationToolbarActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base_notification_toolbar);
        ViewGroup content = ((ViewGroup) findViewById(R.id.activity_content));
        getLayoutInflater().inflate(layoutResID, content);

        initializeToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.activity_base_notification_toolbar);
        ViewGroup content = ((ViewGroup) findViewById(R.id.activity_content));
        content.addView(view);

        initializeToolbar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(R.layout.activity_base_notification_toolbar);
        ViewGroup content = ((ViewGroup) findViewById(R.id.activity_content));
        content.addView(view, params);

        initializeToolbar();
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
    }
}
