package com.fionera.videoplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * TestFragment
 * Created by fionera on 16-6-1.
 */

public class TestFragment
        extends Fragment {

    public static TestFragment newInstance(int pos) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", pos);
        TestFragment testFragment = new TestFragment();
        testFragment.setArguments(bundle);
        return testFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    private int position;
    private TextView textView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
        }
        textView = (TextView) view.findViewById(R.id.tv_test);
        textView.setText(" " + position);
        textView.setOnClickListener(
                v -> startActivity(new Intent(getContext(), VideoPlayActivity.class)));
    }
}
