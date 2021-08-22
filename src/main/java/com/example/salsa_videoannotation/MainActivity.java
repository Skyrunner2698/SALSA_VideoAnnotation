package com.example.salsa_videoannotation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    private static int REQUEST_CODE = 124352;
    BottomNavigationView bottomNav;
    public static ArrayList<VideoFiles> quizVideos = new ArrayList<>();
    public static ArrayList<VideoFiles> feedbackVideos = new ArrayList<>();
    static HashMap<String, Annotations> annotationWrapperList = new HashMap<>();
    private static String FEEDBACK_FOLDER_NAME = "Feedback";
    private static String QUIZ_FOLDER_NAME = "Quiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavView);
        permission();
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.studentQuiz:
                        Toast.makeText(MainActivity.this, "Quiz", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ));
                        fragmentTransaction.commit();
                        item.setChecked(true);
                        break;
                    case R.id.feedbackCreation:
                        Toast.makeText(MainActivity.this, "Feedback Creation", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new FeedbackVideosFragment());
                        fragmentTransaction2.commit();
                        item.setChecked(true);
                        break;
                    case R.id.quizCreation:
                        Toast.makeText(MainActivity.this, "Quiz Creation", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ_CREATION));
                        fragmentTransaction3.commit();
                        item.setChecked(true);
                        break;
                }
                return false;
            }
        });
    }

    private void permission()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        else
        {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            getAllVideos(this);
            annotationWrapperList = StorageModule.loadXML(this);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ));
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                getAllVideos(this);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, new QuizVideoFragment(VideoAdapter.VIDEO_TYPE_QUIZ));
                fragmentTransaction.commit();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    public void getAllVideos(Context context)
    {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String duration = HelperTool.createTimeRepresentation(cursor.getInt(5));
                String fileName = cursor.getString(6);
                VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration);


                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                int index = subString.lastIndexOf("/");
                String folder = subString.substring(index + 1);

                if(folder.equals(FEEDBACK_FOLDER_NAME))
                    feedbackVideos.add(videoFiles);
                else if (folder.equals(QUIZ_FOLDER_NAME))
                    quizVideos.add(videoFiles);
            }
            cursor.close();
        }
    }
}