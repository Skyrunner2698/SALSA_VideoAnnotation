package com.example.salsa_videoannotation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;



public class AnnotationDisplayFragment extends Fragment {

    AnnotationDisplayAdapter annotationDisplayAdapter;
    RecyclerView recyclerView;
    Annotations annotationsWrapper;

    public AnnotationDisplayFragment() {
        // Required empty public constructor
    }

    public AnnotationDisplayFragment(Annotations annotationsWrapper)
    {
        this.annotationsWrapper = annotationsWrapper;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_annotation_display, container, false);
        recyclerView = view.findViewById(R.id.annotation_display_RV);
        if(annotationsWrapper.getVideoAnnotationsMap() != null && annotationsWrapper.getVideoAnnotationsMap().size() > 0)
        {
            annotationDisplayAdapter = new AnnotationDisplayAdapter(getContext(), annotationsWrapper);
            recyclerView.setAdapter(annotationDisplayAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}