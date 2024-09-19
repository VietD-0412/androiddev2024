package vn.edu.usth.weather;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HomeFragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(
                getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);

        /*
        //Practical 3: add code
        // Create a new Fragment to be placed in the activity l
        ForecastFragment firstFragment = new ForecastFragment();
        // Add the fragment to the 'container' FrameLayout
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_container, firstFragment).commit();
        // Practical 6: add WeatherFragment
        WeatherFragment secondFragment = new WeatherFragment();
        getSupportFragmentManager().beginTransaction().add(
                R.id.fragment_weather, secondFragment).commit();
        */
        Log.i("onCreate", "onCreate");
    }

    // Practical 2
    @Override
    public void onStart() {
        super.onStart();
        Log.i("onStart", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("onResume", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("onPause", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("onStop", "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("onDestroy", "onDestroy");
    }
}