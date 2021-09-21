package com.example.salsa_videoannotation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Fragment inserted within the PlayerActivity class to display all Annotations for the Video
 */
public class AnnotationDisplayFragment extends Fragment {
    AnnotationDisplayAdapter annotationDisplayAdapter;
    RecyclerView recyclerView;
    AnnotationWrapper annotationsWrapper;
    private int displayType;

    public AnnotationDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor acquiring the AnnotationWrapper whose annotations must be displayed and the displayType variable
     * to determine which text to use to label the annotations
     * @param annotationsWrapper
     * @param displayType
     */
    public AnnotationDisplayFragment(AnnotationWrapper annotationsWrapper, int displayType)
    {
        this.annotationsWrapper = annotationsWrapper;
        this.displayType = displayType;
    }


    /**
     * Inflates the fragment_annotation_display view and populates the RecyclerView by using the AnnotationDisplayAdapter
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_annotation_display, container, false);
        recyclerView = view.findViewById(R.id.annotation_display_RV);

        // Makes the Create Annotation button visible if a "Creation" fragment is being created
        if (displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION || displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
            PlayerActivity playerActivity = (PlayerActivity) getActivity();
            ImageView createAnnotation = playerActivity.findViewById(R.id.new_annotation);
            createAnnotation.setVisibility(View.VISIBLE);
            ConstraintLayout parentLayout = playerActivity.findViewById(R.id.parent_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(parentLayout);
            constraintSet.connect(R.id.annotation_section, ConstraintSet.TOP, R.id.new_annotation, ConstraintSet.BOTTOM);
            constraintSet.applyTo(parentLayout);
        }

        // Creates the adapter and populates the RecyclerView
        if(annotationsWrapper.getVideoAnnotationsMap() != null && annotationsWrapper.getVideoAnnotationsMap().size() > 0)
        {
            annotationDisplayAdapter = new AnnotationDisplayAdapter(getContext(), annotationsWrapper, displayType);
            recyclerView.setAdapter(annotationDisplayAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}