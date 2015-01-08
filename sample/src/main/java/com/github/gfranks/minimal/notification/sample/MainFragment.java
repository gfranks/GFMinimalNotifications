package com.github.gfranks.minimal.notification.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.gfranks.minimal.notification.GFMinimalNotification;
import com.github.gfranks.minimal.notification.GFMinimalNotificationStyle;
import com.github.gfranks.minimal.notification.OnGFMinimalNotificationClickListener;

public class MainFragment extends Fragment implements View.OnClickListener {

    private GFMinimalNotification mNotification;
    private FrameLayout mNotificationRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNotificationRoot = (FrameLayout) view.findViewById(R.id.sample_notification_container);
        Button show = (Button) view.findViewById(R.id.sample_show);
        Button showNoDuration = (Button) view.findViewById(R.id.sample_show_no_duration);
        Button dismiss = (Button) view.findViewById(R.id.sample_dismiss);
        Button slideInTop = (Button) view.findViewById(R.id.sample_slide_from_top);
        Button slideInBottom = (Button) view.findViewById(R.id.sample_slide_from_bottom);
        Button styleError = (Button) view.findViewById(R.id.sample_style_error);
        Button styleSuccess = (Button) view.findViewById(R.id.sample_style_success);
        Button styleInfo = (Button) view.findViewById(R.id.sample_style_info);
        Button styleDefault = (Button) view.findViewById(R.id.sample_style_default);
        Button styleWarning = (Button) view.findViewById(R.id.sample_style_warning);
        Button setLeftView = (Button) view.findViewById(R.id.sample_set_left_view);
        Button removeLeftView = (Button) view.findViewById(R.id.sample_remove_left_view);
        Button setRightView = (Button) view.findViewById(R.id.sample_set_right_view);
        Button removeRightView = (Button) view.findViewById(R.id.sample_remove_right_view);

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
        mNotification = GFMinimalNotification.with(getActivity());

        /**
         * Sample right image resource
         */
        ImageView rightImageView = new ImageView(getActivity());
        rightImageView.setImageResource(R.drawable.batman);
        mNotification.setRightView(rightImageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sample_show:
                mNotification.setTitleText(((EditText) getView().findViewById(R.id.sample_title)).getText().toString());
                mNotification.setSubtitleText(((EditText) getView().findViewById(R.id.sample_subtitle)).getText().toString());
                mNotification.setDuration(GFMinimalNotification.LENGTH_SHORT);
                /**
                 * Show Notification
                 */
                mNotification.show(mNotificationRoot);
                break;
            case R.id.sample_show_no_duration:
                mNotification.setTitleText(((EditText) getView().findViewById(R.id.sample_title)).getText().toString());
                mNotification.setSubtitleText(((EditText) getView().findViewById(R.id.sample_subtitle)).getText().toString());
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
