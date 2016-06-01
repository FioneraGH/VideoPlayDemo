package com.fionera.videoplayerdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by fionera on 16-6-1.
 */

public class MainActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public Fragment getItem(int position) {
                if (position == 2) {
                    return new ActiveVideoFragment();
                }
                return TestFragment.newInstance(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position + "";
            }
        });
        viewPager.setOffscreenPageLimit(3);
    }
}
