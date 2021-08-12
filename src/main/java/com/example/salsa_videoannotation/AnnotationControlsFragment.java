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

    public void addNewAnnotationAndSave(View view)
    {
        String id = playerActivity.myFiles.get(playerActivity.position).getId();
        Optional<Annotations> existingAnnotationOptional = annotationsList.stream().filter(p -> p.getId().equals(id)).findFirst();
        Annotations annotations;
        if (existingAnnotationOptional.isPresent())
        {
            annotations = existingAnnotationOptional.get();
        }
        else
        {
            annotations = new Annotations(id, "Teacher");
        }
    }


    public void saveAnnotation()
    {
        StorageModule storageModule = new StorageModule();
        if (storageModule.isExternalStorageWritable())
        {
            if(storageModule.storeXML(getContext()))
            {
                Toast.makeText(getContext(), "Saved Bitches", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed Bitches", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
