package com.example.salsa_videoannotation;

import static com.example.salsa_videoannotation.VideoAdapter.videoFiles;
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
import androidx.fragment.app.Fragment;
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

/**
 * Class run when a video is selected from a fragment on the MainActivity
 * Sets up videoplayer and swaps fragments to allow selecting Annotations on videos,
 * creating Feedback and Quizzes and viewing Feedback and taking Quizzes
 * Holds the code for many onClick buttons on fragments associated with it
 */
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

    /**
     * Inflates the activity_player layout, sets up the videoplayer and creates an AnnotationDisplayFragment
     * to resolve which fragment is shown
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Creating activity, assigning gui elements and acquiring extra information from intent
        super.onCreate(savedInstanceState);
        setFullScreenMethod();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_player);
        playerView = findViewById(R.id.exoplayer_dance);
        position = getIntent().getIntExtra("position", -1);
        displayType = getIntent().getIntExtra("videoType", -1);
        myFiles = videoFiles;

        String path = myFiles.get(position).getPath();
        if(path != null)
        {
            // Setting up the simpleexoplayer for playback
            Uri uri = Uri.parse(path);
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Dance Videos"));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri);
            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);
            simpleExoPlayer.prepare(mediaSource);

            // Gets or creates the annotationWrapper class for the associated video
            annotationWrapperForDisplay = HelperTool.getAnnotationByVideoId(myFiles.get(position).getId());

            // Checks which section type is being used and adapts the GUI as per section
            if (displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK)
            {
                // Sets the video player to autoplay
                simpleExoPlayer.setPlayWhenReady(true);
            }
            else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ)
            {
                // Sets the video player to not autoplay
                simpleExoPlayer.setPlayWhenReady(false);
                // Hides the create annotation image
                ImageView createAnnotation = findViewById(R.id.new_annotation);
                createAnnotation.setVisibility(View.INVISIBLE);
                ConstraintLayout parentLayout = findViewById(R.id.parent_layout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);
                constraintSet.connect(R.id.annotation_section, ConstraintSet.TOP, R.id.exoplayer_dance, ConstraintSet.BOTTOM);
                constraintSet.applyTo(parentLayout);
                // Sets up the hashmap of answers to track answered questions
                setUpHashMapAnswers();
            }
            else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
            {
                // Sets the video player to autoplay
                simpleExoPlayer.setPlayWhenReady(true);
                ImageView createAnnotation = findViewById(R.id.new_annotation);
                // Sets the onClick behaviour for the createAnnotation button
                createAnnotation.setOnClickListener(this::onCreateNewQuizAnnotationClick);
            }
            else if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK_VIEWING)
            {
                // Sets the video player to not autoplay
                simpleExoPlayer.setPlayWhenReady(false);
                // Hides the create annotation image
                ImageView createAnnotation = findViewById(R.id.new_annotation);
                createAnnotation.setVisibility(View.INVISIBLE);
                ConstraintLayout parentLayout = findViewById(R.id.parent_layout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);
                constraintSet.connect(R.id.annotation_section, ConstraintSet.TOP, R.id.exoplayer_dance, ConstraintSet.BOTTOM);
                constraintSet.applyTo(parentLayout);
            }

            // Creates an AnnotationDisplayFragment for the current AnnotationWrapper
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotationWrapperForDisplay,
                            displayType)).commit();
        }
    }

    /**
     * Sets the full screen feature for the screen
     */
    private void setFullScreenMethod()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Sets the behaviour for the onDestroy method of the activity to reset additional variables
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
        noQuizAnswersCompleted = 0;
        quizCompleted = false;
        quizScore = 0;
    }

    /**
     * onClick to create a new Feedback Annotation. Assigned to the createAnnotation image in XML
     * @param view
     */
    public void onCreateNewFeedbackAnnotationClick(View view)
    {
        // stops playback of video
        simpleExoPlayer.setPlayWhenReady(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.annotation_section, new AnnotationDetailsFragment()).commit();
    }

    /**
     * onClick to create a new Quiz Annotation. Assigned to createAnnotation in code above
     * @param view
     */
    public void onCreateNewQuizAnnotationClick(View view)
    {
        // stops playback of video
        simpleExoPlayer.setPlayWhenReady(false);
        getSupportFragmentManager().beginTransaction().replace(
                R.id.annotation_section, new QuizCreationFragment()).commit();
    }

    /**
     * onClick method to save a new Annotation. Assigned to the save button in XML
     * @param view
     */
    public void onSaveAnnotationButtonClick(View view) {
        if (displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
            // Gets the AnnotationDetailsFragment currently in use to call the save method
            AnnotationDetailsFragment fragment = (AnnotationDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.addAnnotationAndSave();
        } else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION) {
            // Gets the QuizCreationFragment currently in use to call the save method
            QuizCreationFragment fragment = (QuizCreationFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.addQuizAnnotationAndSave();
        }
    }

    /**
     * onClick method for exiting the creation or editing of an annotation, assigned to back button by default in XML
     * @param view
     */
    public void onBackButtonClick(View view)
    {
        // Replaces the fragment with an AnnotationDisplayFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.annotation_section, new AnnotationDisplayFragment(HelperTool.getAnnotationByVideoId(
                        myFiles.get(position).getId()),
                        displayType)).commit();
        // Sets the video player to start playing
        simpleExoPlayer.setPlayWhenReady(true);
    }

    /**
     * onClick listener to be assigned to Save Button when used for editing existing annotations
     * Assigned in XML on AnnotationDetailsFragmentEditing and QuizCreationFragmentEditing
     * @param v
     */
    public void onSaveChangesAnnotationButtonClick(View v)
    {
        if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
            // Gets the AnnotationDetailsFragmentEditing currently in use to call the edit and save method
            AnnotationDetailsFragmentEditing fragment = (AnnotationDetailsFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.editAnnotationAndSave();
        }
        else
        {
            // Gets the QuizCreationFragmentEditing currently in use to call the edit and save method
            QuizCreationFragmentEditing fragment = (QuizCreationFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.editQuizAnnotationAndSave();
        }
    }

    /**
     * onClick method to delete an Annotation object from the AnnotationWrapper object
     * @param view
     */
    public void onDeleteButtonClick(View view)
    {
        if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
            // Gets the AnnotationDetailsFragmentEditing currently in use to call the delete method
            AnnotationDetailsFragmentEditing fragment = (AnnotationDetailsFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.deleteAnnotation();
        }
        else if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
        {
            // Gets the QuizCreationFragmentEditing currently in use to call the delete method
            QuizCreationFragmentEditing fragment = (QuizCreationFragmentEditing) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
            fragment.deleteAnnotation();
        }
        // Sets the video player to start playing
        simpleExoPlayer.setPlayWhenReady(true);
    }

    /**
     * onClick method to show a Hint when the Hint button is clicked
     * @param view
     */
    public void onHintButtonClicked(View view)
    {
        // Gets the QuizAnsweringFragment currently in use to call the hintCreation method
        QuizAnsweringFragment fragment = (QuizAnsweringFragment) getSupportFragmentManager().findFragmentById(R.id.annotation_section);
        fragment.hintCreation();
    }

    /**
     * Sets up the Answer hashmap before any questions are answered
     * Creates an equivalent number of QuizAnswer objects as there are Annotations
     * Sets the QuizAnswer objects to the Unanswered state
     */
    private void setUpHashMapAnswers()
    {
        for(Map.Entry mapElement : annotationWrapperForDisplay.getVideoAnnotationsMap().entrySet())
        {
            Annotations annotation = (Annotations) mapElement.getValue();
            answersHashMap.put(annotation.getId(), new QuizAnswers(QuizAnsweringFragment.UNANSWERED));
        }
    }

    /**
     * onClick method for when next button is clicked
     * @param view
     */
    public void onNextAnnotationClick(View view)
    {
        // calls general method for next and previous methods
        doNextAndPreviousButtonClick(true);
    }

    /**
     * onClick method for when previous button is clicked
     * @param view
     */
    public void onPreviousAnnotationClick(View view)
    {
        // calls general method for next and previous methods
        doNextAndPreviousButtonClick(false);
    }

    /**
     * General method to handle the behaviour of the next and previous annotation button click
     * @param isNext
     */
    private void doNextAndPreviousButtonClick(boolean isNext)
    {
        // Gets the current fragment in the fragment section
        Fragment testFragment = getSupportFragmentManager().findFragmentById(R.id.annotation_section);
        int annotationId = 0;
        // Only one of the ifs will be commented as they all function identically
        // Checks which type of fragment the testFragment is
        if(testFragment instanceof ViewFeedbackFragment)
        {
            // Checks if the change is next or previous
            if(isNext)
                // gets the new current annotation id from the fragment
                annotationId = ((ViewFeedbackFragment) testFragment).nextAnnotationId;
            else
                // gets the new current annotation id from the fragment
                annotationId = ((ViewFeedbackFragment) testFragment).prevAnnotationId;
            // Creates a new bundle and fragment for displaying the next or previous annotation
            Bundle bundle = createBundle(annotationWrapperForDisplay.getId(), annotationId);
            ViewFeedbackFragment fragment = new ViewFeedbackFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.annotation_section, fragment).commit();
        }
        else if(testFragment instanceof AnnotationDetailsFragmentEditing)
        {
            if(isNext)
                annotationId = ((AnnotationDetailsFragmentEditing) testFragment).nextAnnotationId;
            else
                annotationId = ((AnnotationDetailsFragmentEditing) testFragment).prevAnnotationId;
            Bundle bundle = createBundle(annotationWrapperForDisplay.getId(), annotationId);
            AnnotationDetailsFragmentEditing fragment = new AnnotationDetailsFragmentEditing();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.annotation_section, fragment).commit();
        }
        else if(testFragment instanceof QuizAnsweringFragment)
        {
            if(isNext)
                annotationId = ((QuizAnsweringFragment) testFragment).nextAnnotationId;
            else
                annotationId = ((QuizAnsweringFragment) testFragment).prevAnnotationId;
            Bundle bundle = createBundle(annotationWrapperForDisplay.getId(), annotationId);
            QuizAnsweringFragment fragment = new QuizAnsweringFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.annotation_section, fragment).commit();
        }
        else if(testFragment instanceof QuizCreationFragmentEditing)
        {
            if(isNext)
                annotationId = ((QuizCreationFragmentEditing) testFragment).nextAnnotationId;
            else
                annotationId = ((QuizCreationFragmentEditing) testFragment).prevAnnotationId;
            Bundle bundle = createBundle(annotationWrapperForDisplay.getId(), annotationId);
            QuizCreationFragmentEditing fragment = new QuizCreationFragmentEditing();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.annotation_section, fragment).commit();
        }
    }

    /**
     * Creates a bundle for use with setting up variables for current, previous and next annotations and the
     * AnnotationWrappers
     * @param wrapperId
     * @param annotationId
     * @return
     */
    private Bundle createBundle(String wrapperId, int annotationId)
    {
        // Gets an arraylist of the sorted annotations
        ArrayList<Annotations> sortedAnnotations = new ArrayList<Annotations>(annotationWrapperForDisplay.getSortedVideoAnnotationsMap().values());
        // Gets the new current annotation
        Annotations newCurrent = annotationWrapperForDisplay.getVideoAnnotationsMap().get(annotationId);
        // Gets the position of the current annotation in the sorted list
        int position = sortedAnnotations.indexOf(newCurrent);
        // Gets the values for the previous and next annotation ids and adds them to the bundle
        Bundle bundle = new Bundle();
        bundle.putInt("annotationId", annotationId);
        if (position != 0)
            bundle.putInt("prevAnnotationId", sortedAnnotations.get(position-1).getId());
        else
            bundle.putInt("prevAnnotationId", -1);
        if (position != sortedAnnotations.size() -1)
            bundle.putInt("nextAnnotationId", sortedAnnotations.get(position+1).getId());
        else
            bundle.putInt("nextAnnotationId", -1);
        bundle.putString("annotationWrapperId", wrapperId);
        return bundle;
    }

    /**
     * Method run when a quiz is completed to display completion message
     */
    public void onQuizComplete()
    {
        // Checks if quiz has been in completed quiz state already
        if (quizCompleted == false) {
            // Pauses video playback
            simpleExoPlayer.setPlayWhenReady(false);
            // Constructs the AlertDialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            alertDialogBuilder.setMessage("You have completed the quiz! \nYour score is " + quizScore + "/" + answersHashMap.size()
            + ". \nYou can continue to open the previous questions to check your answers or exit the quiz to retake it!");
            // Sets behaviour for AlertDialog onClick
            alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // Displays the AlertDialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryText, null));
            quizCompleted = true;
        }
    }
}