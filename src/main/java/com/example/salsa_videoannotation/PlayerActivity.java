package com.example.salsa_videoannotation;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
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
import java.util.HashMap;
import java.util.Map;

import static com.example.salsa_videoannotation.VideoAdapter.videoFiles;

public class PlayerActivity extends AppCompatActivity {
    public PlayerView playerView;
    public SimpleExoPlayer simpleExoPlayer;
    public int position = -1;
    public ArrayList<VideoFiles> myFiles = new ArrayList<>();
    public int displayType;
    private AnnotationWrapper annotationWrapperForDisplay;
    public static HashMap<Integer, QuizAnswers> answersHashMap = new HashMap<>();
    public static int quizScore = 0;
    public static int noQuizAnswersCompleted = 0;
    public static boolean quizCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // creating activity, assigning gui elements and acquiring extra information from intent
        super.onCreate(savedInstanceState);
        setFullScreenMethod();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_player);
        playerView = findViewById(R.id.exoplayer_dance);
        position = getIntent().getIntExtra("position", -1);
        displayType = getIntent().getIntExtra("videoType", -1);
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

            // creating the annotation display fragment for the current videofile selected
            annotationWrapperForDisplay = HelperTool.getAnnotationByVideoId(myFiles.get(position).getId());

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
                setUpHashMapAnswers();
            }
            else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
            {
                simpleExoPlayer.setPlayWhenReady(true);
                ImageView createAnnotation = findViewById(R.id.new_annotation);
                createAnnotation.setOnClickListener(this::onCreateNewQuizAnnotationClick);
            }
            else if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK_VIEWING)
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

            // creating the annotation display fragment for the current videofile selected
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotationWrapperForDisplay,
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
        noQuizAnswersCompleted = 0;
        quizCompleted = false;
        quizScore = 0;
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
    public void onSaveChangesAnnotationButtonClick(View v)
    {
        if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
            AnnotationDetailsFragmentEditing fragment = (AnnotationDetailsFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.editAnnotationAndSave();
        }
        else
        {
            QuizCreationFragmentEditing fragment = (QuizCreationFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.editQuizAnnotationAndSave();
        }
    }

    public void onDeleteButtonClick(View view)
    {
        if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
            AnnotationDetailsFragmentEditing fragment = (AnnotationDetailsFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.deleteAnnotation();
        }
        else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
        {
            QuizCreationFragmentEditing fragment = (QuizCreationFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.deleteAnnotation();
        }
        simpleExoPlayer.setPlayWhenReady(true);
    }

    public void onHintButtonClicked(View view)
    {
        QuizAnsweringFragment fragment = (QuizAnsweringFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
        fragment.hintCreation();
    }

    private void setUpHashMapAnswers()
    {
        for(Map.Entry mapElement : annotationWrapperForDisplay.getVideoAnnotationsMap().entrySet())
        {
            Annotations annotation = (Annotations) mapElement.getValue();
            answersHashMap.put(annotation.getId(), new QuizAnswers(QuizAnsweringFragment.UNANSWERED));
        }
    }

    public void onQuizComplete()
    {
        if (quizCompleted == false) {
            simpleExoPlayer.setPlayWhenReady(false);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            alertDialogBuilder.setMessage("You have completed the quiz! \nYour score is " + quizScore + "/" + answersHashMap.size()
            + ". \nYou can continue to open the previous questions to check your answers or exit the quiz to retake it!");
            alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryText, null));
            quizCompleted = true;
        }
    }
}