package com.example.salsa_videoannotation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.salsa_videoannotation.MainActivity.folderList;
import static com.example.salsa_videoannotation.MainActivity.videoFiles;

public class AnnotationControlsFragment extends Fragment
{
    MultiSelectionSpinner multiSelectionSpinner;
    private static final String[] CATEGORIES = {"Timing", "Hips", "Rhythm", "Arm/Hand Placement", "Footwork"};
    public AnnotationControlsFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annotation_controls, container, false);
        multiSelectionSpinner = view.findViewById(R.id.categorySelector);
        multiSelectionSpinner.setItems(CATEGORIES);
//        recyclerView = view.findViewById(R.id.folderRV);
//        if(folderList != null && folderList.size() > 0 && videoFiles != null)
//        {
//            folderAdapter = new com.example.salsa_videoannotation.FolderAdapter(folderList, videoFiles, getContext());
//            recyclerView.setAdapter(folderAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
//        }
        return view;
    }
}
