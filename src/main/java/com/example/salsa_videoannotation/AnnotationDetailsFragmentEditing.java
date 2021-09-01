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

public class AnnotationDetailsFragmentEditing extends Fragment
{
    private MultiSelectionSpinner categoryMultiSelectSpinner;
    private MultiSelectionSpinner bodypartMultiSelectSpinner;
    private EditText content;
    private Button save;
    private TextView startTime;
    private PlayerActivity playerActivity;
    private Annotations currentAnnotationWrapper;
    private AnnotationData currentAnnotation;
    public AnnotationDetailsFragmentEditing()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_annotation_details_editing, container, false);
        categoryMultiSelectSpinner = view.findViewById(R.id.categorySelector);
        categoryMultiSelectSpinner.setItems(AnnotationData.CATEGORIES);
        bodypartMultiSelectSpinner = view.findViewById(R.id.bodypartSelector);
        bodypartMultiSelectSpinner.setItems(AnnotationData.BODYPARTS);
        content = view.findViewById(R.id.annotation_content);
        save = view.findViewById(R.id.save_button);
        startTime = view.findViewById(R.id.start_time);
        playerActivity = (PlayerActivity) getActivity();

        ImageView createAnnotation = playerActivity.findViewById(R.id.new_annotation);
        createAnnotation.setVisibility(View.INVISIBLE);
        ConstraintLayout parentLayout = playerActivity.findViewById(R.id.parent_layout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parentLayout);
        constraintSet.connect(R.id.annotation_section, ConstraintSet.TOP, R.id.exoplayer_dance, ConstraintSet.BOTTOM);
        constraintSet.applyTo(parentLayout);
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
            playerActivity.simpleExoPlayer.seekTo(currentAnnotation.getStartTime());
            playerActivity.simpleExoPlayer.setPlayWhenReady(false);
            categoryMultiSelectSpinner.setSelected(true);
        }
        else
        {
            startTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
        }
        return view;
    }

    public void editAnnotationAndSave()
    {
        if(checkValues())
        {
            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, currentAnnotationWrapper.getVideoFilePath());

            currentAnnotationWrapper.handleAnnotationManipulation(Annotations.UPDATE_TRANSACTION,
                    currentAnnotation.getId(), startTimeLong
                    , categoryMultiSelectSpinner.getSelectedStrings(),
                    bodypartMultiSelectSpinner.getSelectedStrings(),
                    content.getText().toString(), annotationThumbnail, null);

            saveAnnotation(currentAnnotationWrapper, annotationThumbnail, currentAnnotation.getId());
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(currentAnnotation.getId());
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(currentAnnotationWrapper,
                            VideoAdapter.VIDEO_TYPE_FEEDBACK)).commit();
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Annotation Text cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }


    private void saveAnnotation(Annotations annotation, Bitmap thumbnail, int annotationId)
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

    public void deleteAnnotation()
    {
        if(StorageModule.deleteAnnotationThumbnail(getContext(), currentAnnotationWrapper.getId(), currentAnnotation.getId()))
        {
            currentAnnotationWrapper.deleteAnnotation(currentAnnotation.getId());
            if(StorageModule.storeXML(getContext(), currentAnnotationWrapper))
            {
                annotationWrapperList.replace(currentAnnotationWrapper.getId(), currentAnnotationWrapper);
            }
            Toast.makeText(getContext(), "Annotation Deleted", Toast.LENGTH_SHORT).show();
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(currentAnnotationWrapper,
                            VideoAdapter.VIDEO_TYPE_FEEDBACK)).commit();
        }
        else
        {
            Toast.makeText(getContext(), "Failed to Delete Annotation", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkValues()
    {
        if (categoryMultiSelectSpinner.getSelectedStrings().size() == 0 ||
        bodypartMultiSelectSpinner.getSelectedStrings().size() == 0 ||
        content.getText().toString().equals(""))
            return false;
        else
            return true;
    }
}
