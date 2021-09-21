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

/**
 * Fragment inserted within the PlayerActivity class to create new Feedback Annotations
 */
public class AnnotationDetailsFragment extends Fragment
{
    private MultiSelectionSpinner categoryMultiSelectSpinner;
    private MultiSelectionSpinner bodypartMultiSelectSpinner;
    private EditText content;
    private Button save;
    private TextView startTime;
    private PlayerActivity playerActivity;
    public AnnotationDetailsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for creating Feedback Annotations
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
    public void addAnnotationAndSave()
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
                    content.getText().toString(), annotationThumbnail, null);

            // Saves the AnnotationWrapper with the new annotation object to the SD card.
            saveAnnotation(annotationWrapper, annotationThumbnail, newAnnotationId);
            // Loads a new Display Fragment
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotationWrapper,
                            VideoAdapter.VIDEO_TYPE_FEEDBACK)).commit();
            // Restarts the video playback
            playerActivity.simpleExoPlayer.setPlayWhenReady(true);
        }
        else
        {
            // Displays error message if fields are incomplete
            Toast.makeText(playerActivity, "Category, Bodypart and Annotation Text cannot be blank.", Toast.LENGTH_LONG).show();
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
        content.getText().toString().equals(""))
            return false;
        else
            return true;
    }
}
