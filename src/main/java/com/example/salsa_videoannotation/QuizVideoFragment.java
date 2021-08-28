package com.example.salsa_videoannotation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.salsa_videoannotation.MainActivity.quizVideos;

public class QuizVideoFragment extends Fragment {

    VideoAdapter videoAdapter;
    RecyclerView recyclerView;
    int videoType;
    public QuizVideoFragment() {
        // Required empty public constructor
    }

    public QuizVideoFragment(int videoType)
    {
        this.videoType = videoType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_video_list, container, false);
        TextView heading = view.findViewById(R.id.quiz_heading);
        if(videoType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
        {
            heading.setText("Quiz Creation");
        }
        else if (videoType == VideoAdapter.VIDEO_TYPE_QUIZ)
        {
            heading.setText("Student Quiz");
        }

        recyclerView = view.findViewById(R.id.quizRV);
        if(quizVideos != null && quizVideos.size() > 0)
        {
            videoAdapter = new VideoAdapter(getContext(), quizVideos, videoType);
            recyclerView.setAdapter(videoAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}