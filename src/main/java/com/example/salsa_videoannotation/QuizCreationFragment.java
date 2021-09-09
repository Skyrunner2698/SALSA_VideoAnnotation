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

    public void addQuizAnnotationAndSave()
    {
        if(checkValues())
        {
            String id = playerActivity.myFiles.get(playerActivity.position).getId();
            String path = playerActivity.myFiles.get(playerActivity.position).getPath();

            AnnotationWrapper annotationWrapper = HelperTool.getOrCreateAnnotationByVideoIdAndPath(id, path);

            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, path);
            QuizQuestion quizQuestion = new QuizQuestion(question.getText().toString(), correctAnswer.getText().toString(),
                    answer1.getText().toString(), answer2.getText().toString(), answer3.getText().toString());

            int newAnnotationId = 1;
            if (annotationWrapper.getVideoAnnotationsMap() != null && annotationWrapper.getVideoAnnotationsMap().size() != 0) {
                Set<Map.Entry<Integer, Annotations>> mapValues = annotationWrapper.getVideoAnnotationsMap().entrySet();
                Map.Entry<Integer, Annotations>[] forId = new Map.Entry[mapValues.size()];
                mapValues.toArray(forId);
                Annotations lastEntry = forId[mapValues.size()-1].getValue();
                newAnnotationId = lastEntry.getId() + 1;
            }

            annotationWrapper.handleAnnotationManipulation(AnnotationWrapper.CREATE_TRANSACTION,
                    newAnnotationId, startTimeLong
                    , categoryMultiSelectSpinner.getSelectedStrings(),
                    bodypartMultiSelectSpinner.getSelectedStrings(),
                    null, annotationThumbnail, quizQuestion);

            saveAnnotation(annotationWrapper, annotationThumbnail, newAnnotationId);
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotationWrapper,
                           VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)).commit();
            playerActivity.simpleExoPlayer.setPlayWhenReady(true);
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Quiz Content cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }

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