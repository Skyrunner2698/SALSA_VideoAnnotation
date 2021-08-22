package com.example.salsa_videoannotation;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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

public class PlayerActivity extends AppCompatActivity {
    public PlayerView playerView;
    public SimpleExoPlayer simpleExoPlayer;
    public int position = -1;
    public ArrayList<VideoFiles> myFiles = new ArrayList<>();
    public int displayType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // creating activity, assigning gui elements and acquiring extra information from intent
        super.onCreate(savedInstanceState);
        setFullScreenMethod();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_player);
        playerView = findViewById(R.id.exoplayer_dance);
        position = getIntent().getIntExtra("position", -1);
        displayType = getIntent().getIntExtra("sender", -1);
        myFiles = videoFiles;
        // determines where the videofiles are coming from to get the correct positioning in arraylist

        String path = myFiles.get(position).getPath();
        if(path != null)
        {
            // setting up the simpleexoplayer to get ready for playback
            Uri uri = Uri.parse(path);
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Dance Videos"));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri);
            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);
            simpleExoPlayer.prepare(mediaSource);

            if (displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK)
            {
                simpleExoPlayer.setPlayWhenReady(true);
            }
            else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ)
            {
                simpleExoPlayer.setPlayWhenReady(false);
                ImageView createAnnotation = findViewById(R.id.new_annotation);
                createAnnotation.setVisibility(View.INVISIBLE);
                ConstraintLayout parentLayout = findViewById(R.id.parent_layout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);
                constraintSet.connect(R.id.annotation_section, ConstraintSet.TOP, R.id.exoplayer_dance, ConstraintSet.BOTTOM);
                constraintSet.applyTo(parentLayout);
            }
            else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
            {
                simpleExoPlayer.setPlayWhenReady(true);
                ImageView createAnnotation = findViewById(R.id.new_annotation);
                createAnnotation.setOnClickListener(this::onCreateNewQuizAnnotationClick);
            }

            // creating the annotation display fragment for the current videofile selected
            Annotations annotationsForDisplay = HelperTool.getAnnotationByVideoId(myFiles.get(position).getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotationsForDisplay,
                            displayType)).commit();
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

    // onClick method to create a new annotation by switching to the AnnotationControlsFragment, assigned to create annotation image in XML
    public void onCreateNewFeedbackAnnotationClick(View view)
    {
        simpleExoPlayer.setPlayWhenReady(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.annotation_section, new AnnotationDetailsFragment()).commit();
    }

    public void onCreateNewQuizAnnotationClick(View view)
    {
        simpleExoPlayer.setPlayWhenReady(false);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.annotation_section, new QuizCreationFragment()).commit();
    }


    // OnClick method for saving a new annotation, assigned to the Save button by default in XML
    public void onSaveAnnotationButtonClick(View view)
    {
        if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
            AnnotationDetailsFragment fragment = (AnnotationDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.addAnnotationAndSave();
        }
        else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
        {
            QuizCreationFragment fragment = (QuizCreationFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.addQuizAnnotationAndSave();
        }
        simpleExoPlayer.setPlayWhenReady(true);
    }

    // onClick method for exiting the creation or editing of an annotation, assigned to back button by default in XML
    public void onBackButtonClick(View view)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.annotation_section, new AnnotationDisplayFragment(HelperTool.getAnnotationByVideoId(
                        myFiles.get(position).getId()),
                        displayType)).commit();
        simpleExoPlayer.setPlayWhenReady(true);
    }

    // onClick listener to be assigned to Save Button when used for editing existing annotations, Assigned in code on AnnotationControlsFragment
    public OnClickListener onSaveChangesAnnotationButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
                AnnotationDetailsFragment fragment = (AnnotationDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
                fragment.editAnnotationAndSave();
            }
            else
            {
                QuizCreationFragment fragment = (QuizCreationFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
                fragment.editQuizAnnotationAndSave();
            }
            simpleExoPlayer.setPlayWhenReady(true);
        }
    };

//    public void showAnnotationPopUpMenu(View view)
//    {
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        MenuInflater menuInflater = popupMenu.getMenuInflater();
//        menuInflater.inflate(R.menu.popup_delete_menu, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
//        MenuItem deleteItem = (MenuItem) popupMenu.getMenu().findItem(R.id.delete_annotation);
//        Intent intent = new Intent();
//        deleteItem.setIntent(intent);
//        popupMenu.show();
//    }
//
//    public boolean onMenuItemClick(MenuItem item)
//    {
//        switch(item.getItemId())
//        {
//            case R.id.delete_annotation:
//                Intent intent = item.getIntent();
//                AnnotationDisplayFragment fragment = (AnnotationDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
//
//                //                AnnotationControlsFragment fragment = (AnnotationControlsFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
//                fragment.deleteAnnotation(intent.getStringExtra("annotationWrapperId"), intent.getIntExtra("annotationId", -1));
//                return true;
//
//            default:
//                return false;
//        }
//    }


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
}