package com.example.healthtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HeartActivity extends FragmentActivity {
    private static final int NUM_PAGES = 2;
    private static final String TAG = "HeartActivity";

    private FragmentStateAdapter pagerAdapter;
    private ViewPager2 viewPager;
    TabLayout tablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        viewPager = (ViewPager2) findViewById(R.id.pager);
        pagerAdapter = new HeartActivity.ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        tablayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tablayout, viewPager, (tab, position) -> tab.setText(
                (position==0 ? "List" : "Chart") + "")
        ).attach();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fm) {
            super(fm);
        }

        @Override
        public Fragment createFragment(int position) {
            switch(position){
                case 0: return HeartListFragment.newInstance();
//                case 1: return HeartChartFragment.newInstance();
                default: return HeartListFragment.newInstance();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}