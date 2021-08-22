package com.example.salsa_videoannotation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class AnnotationDisplayFragment extends Fragment {

    public static final int ANNOTATION_QUIZ_TYPE = 0;
    public static final int ANNOTATION_FEEDBACK_TYPE = 1;
    AnnotationDisplayAdapter annotationDisplayAdapter;
    RecyclerView recyclerView;
    Annotations annotationsWrapper;
    private int displayType;

    public AnnotationDisplayFragment() {
        // Required empty public constructor
    }

    public AnnotationDisplayFragment(Annotations annotationsWrapper, int displayType)
    {
        this.annotationsWrapper = annotationsWrapper;
        this.displayType = displayType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_annotation_display, container, false);
        recyclerView = view.findViewById(R.id.annotation_display_RV);
        if(annotationsWrapper.getVideoAnnotationsMap() != null && annotationsWrapper.getVideoAnnotationsMap().size() > 0)
        {
            annotationDisplayAdapter = new AnnotationDisplayAdapter(getContext(), annotationsWrapper, displayType);
            recyclerView.setAdapter(annotationDisplayAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}