package com.example.salsa_videoannotation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.salsa_videoannotation.MainActivity.feedbackVideos;

public class FeedbackVideosFragment extends Fragment {

    RecyclerView recyclerView;
    View view;
    com.example.salsa_videoannotation.VideoAdapter videoAdapter;
    public FeedbackVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback_video_list, container, false);
        recyclerView = view.findViewById(R.id.feedbackRV);
        if(feedbackVideos != null && feedbackVideos.size() > 0)
        {
            videoAdapter = new com.example.salsa_videoannotation.VideoAdapter(getContext(), feedbackVideos, VideoAdapter.VIDEO_TYPE_FEEDBACK);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}