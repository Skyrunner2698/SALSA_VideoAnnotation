package com.example.salsa_videoannotation;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

/**
 * Fragment inserted within the PlayerActivity class to edit Quiz Annotations
 */
public class QuizCreationFragmentEditing extends Fragment {
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
    public int nextAnnotationId;
    public int prevAnnotationId;

    public QuizCreationFragmentEditing() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for editing Quiz Annotations
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_quiz_creation_editing, container, false);
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

        // Acquires the details from the Bundle created on the AnnotationDisplayAdapter class or PlayerActivity to populate
        // the fields for viewing and editing and enabling onClick for next and previous arrows
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            String annotationWrapperId = bundle.getString("annotationWrapperId");
            int annotationId = bundle.getInt("annotationId");
            nextAnnotationId = bundle.getInt("nextAnnotationId");
            if(nextAnnotationId == -1)
            {
                ImageView nextAnnotation = view.findViewById(R.id.next_Annotation);
                nextAnnotation.setClickable(false);
                nextAnnotation.setEnabled(false);
            }
            prevAnnotationId = bundle.getInt("prevAnnotationId");
            if(prevAnnotationId == -1)
            {
                ImageView prevAnnotation = view.findViewById(R.id.prev_Annotation);
                prevAnnotation.setClickable(false);
                prevAnnotation.setEnabled(false);
            }
            currentAnnotationWrapper = annotationWrapperList.get(annotationWrapperId);
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(annotationId);
            categoryMultiSelectSpinner.setSelection(currentAnnotation.getCategory());
            bodypartMultiSelectSpinner.setSelection(currentAnnotation.getBodyPart());
            startTime.setText(HelperTool.createTimeRepresentation(currentAnnotation.getStartTime()));
            question.setText(currentAnnotation.getQuizQuestion().getQuestion());
            correctAnswer.setText(currentAnnotation.getQuizQuestion().getCorrectAnswer());
            answer1.setText(currentAnnotation.getQuizQuestion().getAnswer1());
            answer2.setText(currentAnnotation.getQuizQuestion().getAnswer2());
            answer3.setText(currentAnnotation.getQuizQuestion().getAnswer3());
            playerActivity.simpleExoPlayer.seekTo(currentAnnotation.getStartTime());
            playerActivity.simpleExoPlayer.setPlayWhenReady(false);
        }
        return view;
    }

    /**
     * Checks the required values are entered
     * Saves the edited annotation
     */
    public void editQuizAnnotationAndSave()
    {
        if(checkValues())
        {
            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            // Creates a new QuizQuestion object
            QuizQuestion quizQuestion = new QuizQuestion(question.getText().toString(), correctAnswer.getText().toString(),
                    answer1.getText().toString(), answer2.getText().toString(), answer3.getText().toString());
            // Calls an AnnotationWrapper method to update an Annotation Object
            currentAnnotationWrapper.handleAnnotationManipulation(AnnotationWrapper.UPDATE_TRANSACTION,
                    currentAnnotation.getId(), startTimeLong
                    , categoryMultiSelectSpinner.getSelectedStrings(),
                    bodypartMultiSelectSpinner.getSelectedStrings(),
                    null, currentAnnotation.getThumbnail(), quizQuestion);

            // Saves the AnnotationWrapper with the new annotation object to the SD card.
            saveAnnotation(currentAnnotationWrapper, currentAnnotation.getThumbnail(), currentAnnotation.getId());
            // Reloads the currentAnnotation variable to have the up to date version from the AnnotationWrapper
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(currentAnnotation.getId());
            // Loads a new Display Fragment
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(currentAnnotationWrapper,
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
     * Calls teh static StorageModule methods to serialize and save the AnnotationWrapper and Thumbnail respectively
     * @param annotation
     * @param thumbnail
     * @param annotationId
     */
    private void saveAnnotation(AnnotationWrapper annotation, Bitmap thumbnail, int annotationId)
    {
        StorageModule storageModule = new StorageModule();
        if (storageModule.isExternalStorageWritable())
        {
            if(storageModule.storeXML(getContext(), annotation) && StorageModule.storeThumbnail(getContext(), annotation.getId(), annotationId, thumbnail))
            {
                if(annotationWrapperList.containsKey(annotation.getId()))
                    annotationWrapperList.replace(annotation.getId(), annotation);
                else
                    annotationWrapperList.put(annotation.getId(), annotation);

                Toast.makeText(getContext(), "Annotations Saved", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed to Save Annotations", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Calls the StorageModule methods to delete the AnnotationThumbnail
     * Deletes the annotation from the AnnotationWrapper
     * Saves the new version of the AnnotationWrapper to get rid of the deleted Annotation
     */
    public void deleteAnnotation()
    {
        // Checking annotation thumbnail is deleted successfully
        if(StorageModule.deleteAnnotationThumbnail(getContext(), currentAnnotationWrapper.getId(), currentAnnotation.getId()))
        {
            currentAnnotationWrapper.deleteAnnotation(currentAnnotation.getId());
            // Checking if the AnnotationWrapper is saved successfully
            if(StorageModule.storeXML(getContext(), currentAnnotationWrapper))
            {
                // Replaces the AnnotationWrapper in the loaded static list
                annotationWrapperList.replace(currentAnnotationWrapper.getId(), currentAnnotationWrapper);
            }
            Toast.makeText(getContext(), "Annotation Deleted", Toast.LENGTH_SHORT).show();
            // Loads a new AnnotationDisplayFragment
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(currentAnnotationWrapper,
                            VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)).commit();
        }
        else
        {
            // Error Message displayed if there is an issue deleting the annotation
            Toast.makeText(getContext(), "Failed to Delete Annotation", Toast.LENGTH_SHORT).show();
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