package com.example.salsa_videoannotation;

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

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

import java.util.Map;
import java.util.Set;

public class AnnotationDetailsFragment extends Fragment
{
    private MultiSelectionSpinner categoryMultiSelectSpinner;
    private MultiSelectionSpinner bodypartMultiSelectSpinner;
    private EditText content;
    private Button save;
    private TextView startTime;
    private PlayerActivity playerActivity;
    private AnnotationWrapper currentAnnotationWrapper;
    private Annotations currentAnnotation;
    public AnnotationDetailsFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_annotation_details, container, false);
        categoryMultiSelectSpinner = view.findViewById(R.id.categorySelector);
        categoryMultiSelectSpinner.setItems(Annotations.CATEGORIES);
        bodypartMultiSelectSpinner = view.findViewById(R.id.bodypartSelector);
        bodypartMultiSelectSpinner.setItems(Annotations.BODYPARTS);
        content = view.findViewById(R.id.annotation_content);
        save = view.findViewById(R.id.save_button);
        startTime = view.findViewById(R.id.start_time);
        playerActivity = (PlayerActivity) getActivity();
        categoryMultiSelectSpinner.setSelected(true);
        bodypartMultiSelectSpinner.setSelected(true);

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

    public void addAnnotationAndSave()
    {
        if(checkValues())
        {
            String id = playerActivity.myFiles.get(playerActivity.position).getId();
            String path = playerActivity.myFiles.get(playerActivity.position).getPath();

            AnnotationWrapper annotationWrapper = HelperTool.getOrCreateAnnotationByVideoIdAndPath(id, path);

            long startTimeLong = playerActivity.simpleExoPlayer.getCurrentPosition();
            Bitmap annotationThumbnail = HelperTool.getVideoFrame(startTimeLong, path);

            int newAnnotationId = 0;
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
                    content.getText().toString(), annotationThumbnail, null);

            saveAnnotation(annotationWrapper, annotationThumbnail, newAnnotationId);
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotationWrapper,
                            VideoAdapter.VIDEO_TYPE_FEEDBACK)).commit();
            playerActivity.simpleExoPlayer.setPlayWhenReady(true);
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Annotation Text cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }

    private void saveAnnotation(AnnotationWrapper annotationWrapper, Bitmap thumbnail, int newAnnotationId)
    {
        StorageModule storageModule = new StorageModule();
        if (storageModule.isExternalStorageWritable())
        {
            if(storageModule.storeXML(getContext(), annotationWrapper) && StorageModule.storeThumbnail(getContext(), annotationWrapper.getId(), newAnnotationId, thumbnail))
            {
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
