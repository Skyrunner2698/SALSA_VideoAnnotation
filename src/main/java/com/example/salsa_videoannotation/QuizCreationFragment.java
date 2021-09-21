package com.example.salsa_videoannotation;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

import java.util.Map;
import java.util.Set;

/**
 * Fragment inserted within the PlayerActivity class to create new Quiz Annotations
 */
public class QuizCreationFragment extends Fragment {
    private MultiSelectionSpinner categoryMultiSelectSpinner;
    private MultiSelectionSpinner bodypartMultiSelectSpinner;
    private Button save;
    private TextView startTime;
    private EditText question;
    private EditText correctAnswer;
    private EditText answer1;
    private EditText answer2;
    private EditText answer3;
    private PlayerActivity playerActivity;
    private AnnotationWrapper currentAnnotationWrapper;
    private Annotations currentAnnotation;

    public QuizCreationFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for creating Quiz Annotations
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_quiz_creation, container, false);
        categoryMultiSelectSpinner = view.findViewById(R.id.categorySelector);
        categoryMultiSelectSpinner.setItems(Annotations.CATEGORIES);
        bodypartMultiSelectSpinner = view.findViewById(R.id.bodypartSelector);
        bodypartMultiSelectSpinner.setItems(Annotations.BODYPARTS);
        save = view.findViewById(R.id.save_button_quiz);
        startTime = view.findViewById(R.id.start_time);
        question = view.findViewById(R.id.annotation_question);
        correctAnswer = view.findViewById(R.id.annotation_correct_answer);
        answer1 = view.findViewById(R.id.annotation_answer_1);
        answer2 = view.findViewById(R.id.annotation_answer_2);
        answer3 = view.findViewById(R.id.annotation_answer_3);
        playerActivity = (PlayerActivity) getActivity();

        // Hides the "Create Annotation" button to allow for more space for the creation fragment on the screen.
        ImageView createAnnotation = playerActivity.findViewById(R.id.new_annotation);
        createAnnotation.setVisibility(View.INVISIBLE);
        ConstraintLayout parentLayout = playerActivity.findViewById(R.id.parent_layout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parentLayout);
        constraintSet.connect(R.id.annotation_section, ConstraintSet.TOP, R.id.exoplayer_dance, ConstraintSet.BOTTOM);
        constraintSet.applyTo(parentLayout);

        startTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
        return view;
    }

    /**
     * Checks the required values are entered
     * Creates the new id for the annotation object
     * Saves the annotation
     */
    public void addQuizAnnotationAndSave()
    {
        if(checkValues())
        {
            String id = playerActivity.myFiles.get(playerActivity.position).getId();
            String path = playerActivity.myFiles.get(playerActivity.position).getPath();

            //Acquires the correct annotationWrapper based on the id of the video being played
            AnnotationWrapper annotationWrapper = HelperTool.getOrCreateAnnotationByVideoIdAndPath(id, path);

            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            // Extracts videoframe to use as thumbnail for annotation
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, path);
            // Creates a new QuizQuestion object from inputs on the fragment
            QuizQuestion quizQuestion = new QuizQuestion(question.getText().toString(), correctAnswer.getText().toString(),
                    answer1.getText().toString(), answer2.getText().toString(), answer3.getText().toString());

            //Constructs the new id for the annotation by getting the id of the last created annotation and adding one
            int newAnnotationId = 1;
            if (annotationWrapper.getVideoAnnotationsMap() != null && annotationWrapper.getVideoAnnotationsMap().size() != 0) {
                Set<Map.Entry<Integer, Annotations>> mapValues = annotationWrapper.getVideoAnnotationsMap().entrySet();
                Map.Entry<Integer, Annotations>[] forId = new Map.Entry[mapValues.size()];
                mapValues.toArray(forId);
                Annotations lastEntry = forId[mapValues.size()-1].getValue();
                newAnnotationId = lastEntry.getId() + 1;
            }

            // Calls an AnnotationWrapper method to create the Annotation Object
            annotationWrapper.handleAnnotationManipulation(AnnotationWrapper.CREATE_TRANSACTION,
                    newAnnotationId, startTimeLong
                    , categoryMultiSelectSpinner.getSelectedStrings(),
                    bodypartMultiSelectSpinner.getSelectedStrings(),
                    null, annotationThumbnail, quizQuestion);

            // Saves the AnnotationWrapper with the new annotation object to the SD card.
            saveAnnotation(annotationWrapper, annotationThumbnail, newAnnotationId);
            // Loads a new Display Fragment
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotationWrapper,
                           VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)).commit();
            // Restarts the video playback
            playerActivity.simpleExoPlayer.setPlayWhenReady(true);
        }
        else
        {
            // Displays error message if fields are incomplete
            Toast.makeText(playerActivity, "Category, Bodypart and Quiz Content cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Calls the static StorageModule methods to serialize and save the AnnotationWrapper and Thumbnail respectively
     * @param annotationWrapper
     * @param thumbnail
     * @param newAnnotationId
     */
    private void saveAnnotation(AnnotationWrapper annotationWrapper, Bitmap thumbnail, int newAnnotationId)
    {
        StorageModule storageModule = new StorageModule();
        if (storageModule.isExternalStorageWritable())
        {
            if(storageModule.storeXML(getContext(), annotationWrapper) && StorageModule.storeThumbnail(getContext(), annotationWrapper.getId(), newAnnotationId, thumbnail))
            {
                // Updates the static list of AnnotationWrappers loaded into the application
                if(annotationWrapperList.containsKey(annotationWrapper.getId()))
                    annotationWrapperList.replace(annotationWrapper.getId(), annotationWrapper);
                else
                    annotationWrapperList.put(annotationWrapper.getId(), annotationWrapper);

                Toast.makeText(getContext(), "Annotations Saved", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed to Save Annotations", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Checks if any required fields are left incomplete
     * @return
     */
    public boolean checkValues()
    {
        if (categoryMultiSelectSpinner.getSelectedStrings().size() == 0 ||
                bodypartMultiSelectSpinner.getSelectedStrings().size() == 0 ||
                question.getText().toString().equals("") || correctAnswer.getText().toString().equals("") ||
            answer1.getText().toString().equals("") || answer2.getText().toString().equals("") || answer3.getText().toString().equals(""))
            return false;
        else
            return true;
    }
}