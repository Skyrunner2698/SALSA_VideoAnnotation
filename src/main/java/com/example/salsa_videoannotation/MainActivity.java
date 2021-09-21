package com.example.salsa_videoannotation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * First class run when booting up the application. Opens up the main screen with section selection.
 * Defaults to starting on the Quiz Answering Section.
 */
public class MainActivity extends AppCompatActivity {
    private static int REQUEST_CODE = 124352;
    private BottomNavigationView bottomNav;
    public static ArrayList<VideoFiles> quizVideos = new ArrayList<>();
    public static ArrayList<VideoFiles> feedbackVideos = new ArrayList<>();
    static HashMap<String, AnnotationWrapper> annotationWrapperList = new HashMap<>();

    /**
     * Inflates the activity_main layout and creates the bottom navigation menu used to switch between sections
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavView);
        // checking for permission to access external storage
        permission();
        // setting up the onClick buttons for the different MenuItems in the bottom navigation menu
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.studentQuiz:
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ));
                        fragmentTransaction.commit();
                        item.setChecked(true);
                        break;
                    case R.id.feedbackCreation:
                        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new FeedbackVideosFragment(VideoAdapter.VIDEO_TYPE_FEEDBACK));
                        fragmentTransaction2.commit();
                        item.setChecked(true);
                        break;
                    case R.id.quizCreation:
                        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ_CREATION));
                        fragmentTransaction3.commit();
                        item.setChecked(true);
                        break;
                    case R.id.viewFeedback:
                        FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new FeedbackVideosFragment(VideoAdapter.VIDEO_TYPE_FEEDBACK_VIEWING));
                        fragmentTransaction4.commit();
                        item.setChecked(true);
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Checks permission for external storage access and loads new QuizVideoFragment as default start state
     */
    private void permission()
    {
        // Checking if external storage access permission already exists
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            // Requests permission if non-existent
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        else
        {
            // Reports successful permission granting
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            // Loads all videos and all AnnotationWrappers
            StorageModule.getAllVideos(this);
            annotationWrapperList = StorageModule.loadXML(this);
            // Loads Student Video Quiz Fragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ));
            fragmentTransaction.commit();
        }
    }

    /**
     * Overrides the getting of the result of the PermissionsResult to allow for loading of videos and Annotations
     * after getting the result
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Gets the result of the Permission request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            // Checks if the result matches to the Permission Granted key
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Reports successful permission granting
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // Loads all videos and all AnnotationWrappers
                StorageModule.getAllVideos(this);
                annotationWrapperList = StorageModule.loadXML(this);
                // Loads Student Video Quiz Fragment
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ));
                fragmentTransaction.commit();
            }
            else
            {
                // Re-requests external storage access
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }
}