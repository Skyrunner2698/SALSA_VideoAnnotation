package com.example.salsa_videoannotation;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * Fragment inserted within the PlayerActivity class for students to view Feedback
 */
public class ViewFeedbackFragment extends Fragment {
    private TextView categoryDisplay;
    private TextView bodypartDisplay;
    private TextView content;
    private TextView startTime;
    private PlayerActivity playerActivity;
    private AnnotationWrapper currentAnnotationWrapper;
    private Annotations currentAnnotation;
    public int nextAnnotationId;
    public int prevAnnotationId;

    public ViewFeedbackFragment()
    {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for viewing Feedback
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_view_feedback, container, false);
        categoryDisplay = view.findViewById(R.id.category_display);
        bodypartDisplay = view.findViewById(R.id.bodypart_display);
        content = view.findViewById(R.id.annotation_display);
        startTime = view.findViewById(R.id.start_time);
        playerActivity = (PlayerActivity) getActivity();

        // Acquires the details from the Bundle created on the AnnotationDisplayAdapter class or PlayerActivity to populate
        // the fields for viewing and enabling onClick for next and previous arrows
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            String annotationWrapperId = bundle.getString("annotationWrapperId");
            int annotationId = bundle.getInt("annotationId");
            nextAnnotationId = bundle.getInt("nextAnnotationId");
            if(nextAnnotationId == -1)
            {
                ImageView nextAnnotation = view.findViewById(R.id.next_Annotation);
                nextAnnotation.setClickable(false);
                nextAnnotation.setEnabled(false);
            }
            prevAnnotationId = bundle.getInt("prevAnnotationId");
            if(prevAnnotationId == -1)
            {
                ImageView prevAnnotation = view.findViewById(R.id.prev_Annotation);
                prevAnnotation.setClickable(false);
                prevAnnotation.setEnabled(false);
            }
            currentAnnotationWrapper = annotationWrapperList.get(annotationWrapperId);
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(annotationId);
            content.setText(currentAnnotation.getContent());
            startTime.setText(HelperTool.createTimeRepresentation(currentAnnotation.getStartTime()));
            categoryDisplay.setText(formatListStrings(currentAnnotation.getCategory()));
            bodypartDisplay.setText(formatListStrings(currentAnnotation.getBodyPart()));
            // Moves the video player to the Annotation's position
            playerActivity.simpleExoPlayer.seekTo(currentAnnotation.getStartTime());
            // Pauses playback
            playerActivity.simpleExoPlayer.setPlayWhenReady(false);
        }
        return view;
    }

    /**
     * Method to remove "[" and "]" from the Category and Bodypart lists
     * @param toFormat
     * @return
     */
    private String formatListStrings(List<String> toFormat)
    {
        String stringList = toFormat.toString();
        stringList = stringList.substring(1, stringList.length() -1 );
        return stringList;
    }
}