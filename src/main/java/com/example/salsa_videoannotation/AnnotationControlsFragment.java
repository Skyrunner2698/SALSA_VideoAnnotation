package com.example.salsa_videoannotation;

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

import static com.example.salsa_videoannotation.MainActivity.annotationsList;

public class AnnotationControlsFragment extends Fragment
{
    private MultiSelectionSpinner categoryMultiSelectSpinner;
    private MultiSelectionSpinner bodypartMultiSelectSpinner;
    private EditText content;
    private Button save;
    private EditText startTime;
    private EditText endTime;
    private PlayerActivity playerActivity;
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
        startTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
        endTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
        return view;
    }

    public void addAnnotationAndSave()
    {
        if(checkValues())
        {
            String id = playerActivity.myFiles.get(playerActivity.position).getId();
            Annotations annotations = HelperTool.getAnnotationByVideoId(id);

            annotations.handleAnnotationManipulation(Annotations.CREATE_TRANSACTION,
                    Annotations.PLACEHOLDER_VIDEO_ANNOTATION_ID, HelperTool.convertTimeToMilliseconds(startTime.getText().toString())
                    , HelperTool.convertTimeToMilliseconds(endTime.getText().toString()),
                    categoryMultiSelectSpinner.getSelectedStrings(), bodypartMultiSelectSpinner.getSelectedStrings(),
                    content.getText().toString());

            saveAnnotation(annotations);
            playerActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.annotation_section, new AnnotationDisplayFragment(annotations.getVideoAnnotations())).commit();
        }
        else
        {
            Toast.makeText(playerActivity, "Category, Bodypart and Annotation Text cannot be blank.", Toast.LENGTH_LONG).show();
        }
    }


    private void saveAnnotation(Annotations annotation)
    {
        StorageModule storageModule = new StorageModule();
        if (storageModule.isExternalStorageWritable())
        {
            if(storageModule.storeXML(getContext(), annotation))
            {
                int position = annotationsList.indexOf(annotation);
                if(position != -1)
                    annotationsList.set(position, annotation);
                else
                    annotationsList.add(annotation);

                Toast.makeText(getContext(), "Saved Bitches", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed Bitches", Toast.LENGTH_SHORT).show();
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
