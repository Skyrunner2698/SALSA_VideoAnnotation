package com.example.salsa_videoannotation;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ViewFeedbackFragment extends Fragment {
    private TextView categoryDisplay;
    private TextView bodypartDisplay;
    private TextView content;
    private TextView startTime;
    private PlayerActivity playerActivity;
    private AnnotationWrapper currentAnnotationWrapper;
    private Annotations currentAnnotation;

    public ViewFeedbackFragment()
    {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_view_feedback, container, false);
        categoryDisplay = view.findViewById(R.id.category_display);
        bodypartDisplay = view.findViewById(R.id.bodypart_display);
        content = view.findViewById(R.id.annotation_display);
        startTime = view.findViewById(R.id.start_time);
        playerActivity = (PlayerActivity) getActivity();

        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            String annotationWrapperId = bundle.getString("annotationWrapperId");
            int annotationId = bundle.getInt("annotationId");
            currentAnnotationWrapper = annotationWrapperList.get(annotationWrapperId);
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(annotationId);
            content.setText(currentAnnotation.getContent());
            startTime.setText(HelperTool.createTimeRepresentation(currentAnnotation.getStartTime()));
            categoryDisplay.setText(formatListStrings(currentAnnotation.getCategory()));
            bodypartDisplay.setText(formatListStrings(currentAnnotation.getBodyPart()));
            playerActivity.simpleExoPlayer.seekTo(currentAnnotation.getStartTime());
            playerActivity.simpleExoPlayer.setPlayWhenReady(false);
        }
        else
        {
            startTime.setText(HelperTool.createTimeRepresentation(playerActivity.simpleExoPlayer.getCurrentPosition()));
        }
        return view;
    }


    private String formatListStrings(List<String> toFormat)
    {
        String stringList = toFormat.toString();
        stringList = stringList.substring(1, stringList.length() -1 );
        return stringList;
    }
}