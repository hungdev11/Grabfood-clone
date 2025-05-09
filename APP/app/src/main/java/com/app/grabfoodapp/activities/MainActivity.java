package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.app.grabfoodapp.R;
import com.app.grabfoodapp.adapter.ViewPageAdapter;
import com.app.grabfoodapp.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPagerPlay();
    }
    private void init() {
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }
    private void viewPagerPlay() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.search).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.profile).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    navigateToPage(0);
                } else if (itemId == R.id.search) {
                    navigateToPage(1);
                } else if (itemId == R.id.profile) {
                    navigateToPage(2);
                }
                return true;
            }
        });
    }
    private void navigateToPage(int pageIndex) {
        TokenManager tokenManager = new TokenManager(this);

        // Check if trying to access profile page without authentication
        if (pageIndex == 2 && !tokenManager.hasToken()) {
            // Show message that login is required
            Toast.makeText(this, "You need to log in to access your profile", Toast.LENGTH_SHORT).show();

            // Navigate to login screen
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        // Otherwise proceed with normal navigation
        viewPager.setCurrentItem(pageIndex);
    }
}