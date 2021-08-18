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
    static ArrayList<VideoFiles> videoFiles = new ArrayList<>();
    static ArrayList<String> folderList = new ArrayList<>();
    static HashMap<String, Annotations> annotationWrapperList = new HashMap<>();

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
                    case R.id.folderList:
                        Toast.makeText(MainActivity.this, "Folder", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new FolderFragment());
                        fragmentTransaction.commit();
                        item.setChecked(true);
                        break;
                    case R.id.filesList:
                        Toast.makeText(MainActivity.this, "Files", Toast.LENGTH_SHORT).show();
                        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainFragment, new FilesFragment());
                        fragmentTransaction2.commit();
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
            videoFiles = getAllVideos(this);
            annotationWrapperList = StorageModule.loadXML(this);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragment, new FolderFragment());
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
                videoFiles = getAllVideos(this);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, new FolderFragment());
                fragmentTransaction.commit();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    public ArrayList<VideoFiles> getAllVideos(Context context)
    {
        ArrayList<VideoFiles> tempVideoFiles = new ArrayList<>();
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
                Log.e("Path", path);
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                if(!folderList.contains(subString))
                    folderList.add(subString);
                tempVideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return tempVideoFiles;
    }
}