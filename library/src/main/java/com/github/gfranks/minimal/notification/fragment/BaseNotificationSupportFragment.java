package com.github.gfranks.minimal.notification.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.gfranks.minimal.notification.R;

public abstract class BaseNotificationSupportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_notification, container, false);

        ViewGroup content = ((ViewGroup) view.findViewById(R.id.fragment_content));
        inflater.inflate(getContentView(), content);

        return view;
    }

    public abstract int getContentView();
}
