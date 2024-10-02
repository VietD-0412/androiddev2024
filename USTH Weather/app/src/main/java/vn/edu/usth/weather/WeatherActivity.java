package vn.edu.usth.weather;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class WeatherActivity extends AppCompatActivity {
    private Handler handler;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        HomeFragmentPagerAdapter adapter = new HomeFragmentPagerAdapter(
                getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);

        // Initialize the Handler
        handler = new Handler(getMainLooper());

        // Button to simulate network request
        Button simulateRequestButton = findViewById(R.id.simulateRequestButton);
        simulateRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simulateNetworkRequest();
            }
        });

        //extractAndPlayMusic(); not working properly anymore

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

        private void simulateNetworkRequest() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Simulating network delay
                    try {
                        Thread.sleep(2000); // Simulate a 2-second network delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Simulate fetched data
                    final String simulatedResponse = "Weather data refreshed successfully!";

                    // Use the Handler to post a Toast on the main thread
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this, simulatedResponse, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }


    @RequiresApi(Build.VERSION_CODES.Q)
    private void extractAndPlayMusic() {
        try {
            // Get a write request for the music file
            Uri audioFileUri = createWriteRequest();
            if (audioFileUri == null) {
                return;
            }

            // Copy the music file to shared storage
            InputStream inputStream = getResources().openRawResource(R.raw.music);
            OutputStream outputStream = getContentResolver().openOutputStream(audioFileUri);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            // Play the audio file
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, audioFileUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri createWriteRequest() {
        ContentResolver contentResolver = getContentResolver();
        Uri audioFileUri = null;

        // Check if an existing audio file is pending for write
        String[] projection = {MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.IS_PENDING + " != ?";
        String[] selectionArgs = {"1"};
        Uri queryUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Cursor existingAudioFiles = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

        try {
            if (existingAudioFiles != null && existingAudioFiles.moveToFirst()) {
                int idColumnIndex = existingAudioFiles.getColumnIndex(MediaStore.Audio.Media._ID);
                if (idColumnIndex != -1) {
                    long id = existingAudioFiles.getLong(idColumnIndex);
                    audioFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                }
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, "music.mp3");
                contentValues.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
                contentValues.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
                contentValues.put(MediaStore.Audio.Media.IS_PENDING, 1);
                audioFileUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
        } finally {
            if (existingAudioFiles != null) {
                existingAudioFiles.close();
            }
        }

        return audioFileUri;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == R.id.action_refresh) {
//            Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if (itemId == R.id.action_settings) {
//            startActivity(new Intent(this, PrefActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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