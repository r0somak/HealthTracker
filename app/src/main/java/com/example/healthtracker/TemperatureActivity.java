package com.example.healthtracker;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TemperatureActivity extends FragmentActivity {
    private static final int NUM_PAGES = 2;
    private static final String TAG = "TemperatureActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FragmentStateAdapter pagerAdapter;
    private ViewPager2 viewPager;
    TabLayout tablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        viewPager = (ViewPager2) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        tablayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tablayout, viewPager, (tab, position) -> tab.setText("TAB " + (position+1))).attach();
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
                case 0: return TempListFragment.newInstance();
                case 1: return TempChartFragment.newInstance();
                default: return TempListFragment.newInstance();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}