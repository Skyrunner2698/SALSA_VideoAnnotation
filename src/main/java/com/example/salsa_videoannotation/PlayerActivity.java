package com.example.salsa_videoannotation;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import static com.example.salsa_videoannotation.VideoAdapter.videoFiles;
import static com.example.salsa_videoannotation.VideoFolderAdapter.folderVideoFiles;

public class PlayerActivity extends AppCompatActivity {
    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    Button saveButton;
    TextView textToTest;
    Button loadButton;
    ImageView createAnnotation;
    int position = -1;
    ArrayList<VideoFiles> myFiles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMethod();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_player);
        playerView = findViewById(R.id.exoplayer_dance);
        createAnnotation = findViewById(R.id.new_annotation);
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender.equals("FolderIsSending"))
        {
            myFiles = folderVideoFiles;
        }
        else
        {
            myFiles = videoFiles;
        }
        String path = myFiles.get(position).getPath();
        if(path != null)
        {
            Uri uri = Uri.parse(path);
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Dance Videos"));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri);
            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
//            simpleExoPlayer.createMessage(new PlayerMessage.Target() {
//                @Override
//                public void handleMessage(int messageType, Object payload) throws ExoPlaybackException {
//                    Toast.makeText(PlayerActivity.this, "5 Seconds", Toast.LENGTH_SHORT).show();
//                }
//            }).setPosition(5000).send();

            createAnnotation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PlayerActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                            .replace(R.id.annotation_controls, new AnnotationControlsFragment());
                    fragmentTransaction.commit();
                }
            });

//            saveButton.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    StorageModule storageModule = new StorageModule();
//                    if (storageModule.isExternalStorageWritable())
//                    {
//                        if(storageModule.storeXML(PlayerActivity.this))
//                        {
//                            Toast.makeText(PlayerActivity.this, "Saved Bitches", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            Toast.makeText(PlayerActivity.this, "Failed Bitches", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            });
//
//            loadButton.setOnClickListener(new OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                StorageModule storageModule = new StorageModule();
//                if (storageModule.isExternalStorageWritable())
//                {
//                    textToTest.setText(storageModule.loadXML(PlayerActivity.this));
//                }
//            }
//        });
        }
    }

    private void setFullScreenMethod()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        updateTrackSelectorParameters();
//        updateStartPosition();
//        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
//        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
//        outState.putInt(KEY_WINDOW, startWindow);
//        outState.putLong(KEY_POSITION, startPosition);
//    }
//
//    private void updateStartPosition() {
//        if (player != null) {
//            startAutoPlay = player.getPlayWhenReady();
//            startWindow = player.getCurrentWindowIndex();
//            startPosition = Math.max(0, player.getContentPosition());
//        }
//    }
}