package com.github.gfranks.minimal.notification.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.minimal.notification.R;

public class BaseNotificationFragmentActivity extends FragmentActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base_notification);
        ViewGroup content = ((ViewGroup) findViewById(R.id.activity_content));
        getLayoutInflater().inflate(layoutResID, content);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.activity_base_notification);
        ViewGroup content = ((ViewGroup) findViewById(R.id.activity_content));
        content.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(R.layout.activity_base_notification);
        ViewGroup content = ((ViewGroup) findViewById(R.id.activity_content));
        content.addView(view, params);
    }
}
