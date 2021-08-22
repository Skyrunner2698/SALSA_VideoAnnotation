package com.example.salsa_videoannotation;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

public class QuizCreationFragment extends Fragment {
    private MultiSelectionSpinner categoryMultiSelectSpinner;
    private MultiSelectionSpinner bodypartMultiSelectSpinner;
    private Button save;
    private EditText startTime;
    private EditText question;
    private EditText correctAnswer;
    private EditText answer1;
    private EditText answer2;
    private EditText answer3;
    private PlayerActivity playerActivity;
    private Annotations currentAnnotationWrapper;
    private AnnotationData currentAnnotation;

    public QuizCreationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_quiz_creation, container, false);
        categoryMultiSelectSpinner = view.findViewById(R.id.categorySelector);
        categoryMultiSelectSpinner.setItems(AnnotationData.CATEGORIES);
        bodypartMultiSelectSpinner = view.findViewById(R.id.bodypartSelector);
        bodypartMultiSelectSpinner.setItems(AnnotationData.BODYPARTS);
        save = view.findViewById(R.id.save_button_quiz);
        startTime = view.findViewById(R.id.start_time);
        question = view.findViewById(R.id.annotation_question);
        correctAnswer = view.findViewById(R.id.annotation_correct_answer);
        answer1 = view.findViewById(R.id.annotation_answer_1);
        answer2 = view.findViewById(R.id.annotation_answer_2);
        answer3 = view.findViewById(R.id.annotation_answer_3);
        playerActivity = (PlayerActivity) getActivity();
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            String annotationWrapperId = bundle.getString("annotationWrapperId");
            int annotationId = bundle.getInt("annotationId");
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
            save.setOnClickListener(playerActivity.onSaveChangesAnnotationButtonClick);
            save.setText("Save Changes");
        }
        else
        {
            startTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
        }
        return view;
    }

    public void addQuizAnnotationAndSave()
    {
        if(checkValues())
        {
            String id = playerActivity.myFiles.get(playerActivity.position).getId();
            String path = playerActivity.myFiles.get(playerActivity.position).getPath();

            Annotations annotations = HelperTool.getOrCreateAnnotationByVideoIdAndPath(id, path);

            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, path);
            QuizQuestion quizQuestion = new QuizQuestion(question.getText().toString(), correctAnswer.getText().toString(),
                    answer1.getText().toString(), answer2.getText().toString(), answer3.getText().toString());
            annotations.handleAnnotationManipulation(Annotations.CREATE_TRANSACTION,
                    Annotations.PLACEHOLDER_VIDEO_ANNOTATION_ID, startTimeLong
                    , categoryMultiSelectSpinner.getSelectedStrings(),
                    bodypartMultiSelectSpinner.getSelectedStrings(),
                    null, annotationThumbnail, quizQuestion);

            saveAnnotation(annotations, annotationThumbnail);
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotations,
                            AnnotationDisplayFragment.ANNOTATION_FEEDBACK_TYPE)).commit();
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Quiz Content cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }

    public void editQuizAnnotationAndSave()
    {
        if(checkValues())
        {
            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, currentAnnotationWrapper.getVideoFilePath());
            QuizQuestion quizQuestion = new QuizQuestion(question.getText().toString(), correctAnswer.getText().toString(),
                    answer1.getText().toString(), answer2.getText().toString(), answer3.getText().toString());
            currentAnnotationWrapper.handleAnnotationManipulation(Annotations.UPDATE_TRANSACTION,
                    currentAnnotation.getId(), startTimeLong
                    , categoryMultiSelectSpinner.getSelectedStrings(),
                    bodypartMultiSelectSpinner.getSelectedStrings(),
                    null, annotationThumbnail, quizQuestion);

            saveAnnotation(currentAnnotationWrapper, annotationThumbnail);
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(currentAnnotation.getId());
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(currentAnnotationWrapper,
                            AnnotationDisplayFragment.ANNOTATION_FEEDBACK_TYPE)).commit();
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Quiz Content cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }


    private void saveAnnotation(Annotations annotation, Bitmap thumbnail)
    {
        StorageModule storageModule = new StorageModule();
        if (storageModule.isExternalStorageWritable())
        {
            if(storageModule.storeXML(getContext(), annotation) && StorageModule.storeThumbnail(getContext(), annotation.getId(), annotation.getVideoAnnotationsMap().size(), thumbnail))
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

    public void deleteAnnotation(String annotationWrapperId, int annotationId)
    {
        currentAnnotationWrapper = annotationWrapperList.get(annotationWrapperId);
        if(StorageModule.deleteAnnotationThumbnail(getContext(), annotationWrapperId, annotationId))
        {
            currentAnnotationWrapper.deleteAnnotation(annotationId);
            if(StorageModule.storeXML(getContext(), currentAnnotationWrapper))
            {
                annotationWrapperList.replace(annotationWrapperId, currentAnnotationWrapper);
            }
            Toast.makeText(getContext(), "Delete Annotation", Toast.LENGTH_SHORT).show();
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(currentAnnotationWrapper,
                            AnnotationDisplayFragment.ANNOTATION_FEEDBACK_TYPE)).commit();
        }
        else
        {

        }
    }

    public boolean checkValues()
    {
        if (categoryMultiSelectSpinner.getSelectedStrings().size() == 0 ||
                bodypartMultiSelectSpinner.getSelectedStrings().size() == 0 ||
                question.getText().equals("") || correctAnswer.getText().equals("") ||
            answer1.getText().equals("") || answer2.getText().equals("") || answer3.getText().equals(""))
            return false;
        else
            return true;
    }
}