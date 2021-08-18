package com.example.salsa_videoannotation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Optional;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

public class AnnotationControlsFragment extends Fragment
{
    private MultiSelectionSpinner categoryMultiSelectSpinner;
    private MultiSelectionSpinner bodypartMultiSelectSpinner;
    private EditText content;
    private Button save;
    private EditText startTime;
    private EditText endTime;
    private PlayerActivity playerActivity;
    private Annotations currentAnnotationWrapper;
    private AnnotationData currentAnnotation;
    private static final String[] CATEGORIES = {"Step Direction", "Foot Position", "Step Size", "Weight Transfer", "Movement Quality", "Timing", "Rhythm", "Suggested Moves to try"};
    private static final String[] BODYPARTS = {"Head", "Shoulders", "Arms", "Hands", "Torso", "Hips", "Legs", "Feet"};
    public AnnotationControlsFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_annotation_controls, container, false);
        categoryMultiSelectSpinner = view.findViewById(R.id.categorySelector);
        categoryMultiSelectSpinner.setItems(CATEGORIES);
        bodypartMultiSelectSpinner = view.findViewById(R.id.bodypartSelector);
        bodypartMultiSelectSpinner.setItems(BODYPARTS);
        content = view.findViewById(R.id.annotation_content);
        save = view.findViewById(R.id.save_button);
        startTime = view.findViewById(R.id.start_time);
        endTime = view.findViewById(R.id.end_time);
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
            content.setText(currentAnnotation.getContent());
            startTime.setText(HelperTool.createTimeRepresentation(currentAnnotation.getStartTime()));
            endTime.setText(HelperTool.createTimeRepresentation(currentAnnotation.getEndTime()));
            playerActivity.simpleExoPlayer.seekTo(currentAnnotation.getStartTime());
            playerActivity.simpleExoPlayer.setPlayWhenReady(false);
            save.setOnClickListener(playerActivity.onSaveChangesAnnotationButtonClick);
            save.setText("Save Changes");
        }
        else
        {
            startTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
            endTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
        }
        return view;
    }

    public void addAnnotationAndSave()
    {
        if(checkValues())
        {
            String id = playerActivity.myFiles.get(playerActivity.position).getId();
            String path = playerActivity.myFiles.get(playerActivity.position).getPath();

            Annotations annotations = HelperTool.getOrCreateAnnotationByVideoIdAndPath(id, path);

            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, path);

            annotations.handleAnnotationManipulation(Annotations.CREATE_TRANSACTION,
                    Annotations.PLACEHOLDER_VIDEO_ANNOTATION_ID, startTimeLong
                    , HelperTool.convertTimeToMilliseconds(endTime.getText().toString()),
                    categoryMultiSelectSpinner.getSelectedStrings(), bodypartMultiSelectSpinner.getSelectedStrings(),
                    content.getText().toString(), annotationThumbnail);

            saveAnnotation(annotations, annotationThumbnail);
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotations)).commit();
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Annotation Text cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }

    public void editAnnotationAndSave()
    {
        if(checkValues())
        {
            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, currentAnnotationWrapper.getVideoFilePath());

            currentAnnotationWrapper.handleAnnotationManipulation(Annotations.UPDATE_TRANSACTION,
                    currentAnnotation.getId(), startTimeLong
                    , HelperTool.convertTimeToMilliseconds(endTime.getText().toString()),
                    categoryMultiSelectSpinner.getSelectedStrings(), bodypartMultiSelectSpinner.getSelectedStrings(),
                    content.getText().toString(), annotationThumbnail);

            saveAnnotation(currentAnnotationWrapper, annotationThumbnail);
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(currentAnnotation.getId());
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(currentAnnotationWrapper)).commit();
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Annotation Text cannot be blank.", Toast.LENGTH_LONG).show();
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

    public boolean checkValues()
    {
        if (categoryMultiSelectSpinner.getSelectedStrings().size() == 0 ||
        bodypartMultiSelectSpinner.getSelectedStrings().size() == 0 ||
        content.getText().equals(""))
            return false;
        else
            return true;
    }
}
