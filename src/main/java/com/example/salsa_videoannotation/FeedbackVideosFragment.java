package com.example.salsa_videoannotation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.salsa_videoannotation.MainActivity.feedbackVideos;

/**
 * Fragment inserted within the MainActivity class to view the videos associated with Feedback Viewing and Creation
 */
public class FeedbackVideosFragment extends Fragment {

    RecyclerView recyclerView;
    View view;
    com.example.salsa_videoannotation.VideoAdapter videoAdapter;
    int videoType;
    public FeedbackVideosFragment() {
        // Required empty public constructor
    }

    /**
     * Acquires the videoType to handle the title for the section
     * @param videoType
     */
    public FeedbackVideosFragment(int videoType)
    {
        this.videoType = videoType;
    }

    /**
     * Inflates the layout for displaying videos associated with Feedback
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback_video_list, container, false);
        recyclerView = view.findViewById(R.id.feedbackRV);
        TextView heading = view.findViewById(R.id.feedback_creation_Heading);

        // Determines which heading to give the section
        if(videoType == VideoAdapter.VIDEO_TYPE_FEEDBACK)
        {
            heading.setText(getResources().getText(R.string.feedback_creation_heading));
        }
        else if (videoType == VideoAdapter.VIDEO_TYPE_FEEDBACK_VIEWING)
        {
            heading.setText(getResources().getText(R.string.feedback_view_heading));
        }

        // Creates the adapter and populates the RecyclerView
        if(feedbackVideos != null && feedbackVideos.size() > 0)
        {
            videoAdapter = new com.example.salsa_videoannotation.VideoAdapter(getContext(), feedbackVideos, videoType);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}