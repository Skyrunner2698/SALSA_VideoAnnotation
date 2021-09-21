package com.example.salsa_videoannotation;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter class used in conjunction with RecyclerView on the QuizVideoFragment and FeedbackVideosFragment
 * Populates the RecyclerView and sets up the onClick event for the items
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    public static final int VIDEO_TYPE_FEEDBACK = 0;
    public static final int VIDEO_TYPE_QUIZ = 1;
    public static final int VIDEO_TYPE_QUIZ_CREATION = 2;
    public static final int VIDEO_TYPE_FEEDBACK_VIEWING = 3;
    private Context mContext;
    static ArrayList<VideoFiles> videoFiles;
    View view;
    private int videoType;

    /**
     * Constructor for the adapter
     * Gets the list of VideoFiles and the videoType for labelling
     * @param mContext
     * @param videoFiles
     * @param videoType
     */
    public VideoAdapter(Context mContext, ArrayList<VideoFiles> videoFiles, int videoType) {
        this.mContext = mContext;
        this.videoFiles = videoFiles;
        this.videoType = videoType;
    }

    /**
     * Inflates the layout used for the items in the RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Populates the video_item layout with the required values and sets onClick behaviour
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Sets the fileName to the title of the videoFile
        holder.fileName.setText(videoFiles.get(position).getTitle());
        holder.fileName.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.fileName.setSelected(true);
            }
        }, 1000);
        // Sets the duration of the video
        holder.videoDuration.setText(videoFiles.get(position).getDuration());
        // Thumbnail for videoFile is set using glide and a image stored on the video in Android
        Glide.with(mContext).load(new File(videoFiles.get(position).getPath())).into(holder.thumbNail);
        // Handles onClick functionality of RecyclerView items
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creates an Intent the position and videoType to start the PlayerActivity
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("videoType", videoType);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * Required method to override for creating an adapter
     * @return
     */
    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    /**
     * Class to represent the video_item layout when inflated for population
     */
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView thumbNail;
        TextView fileName, videoDuration;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            thumbNail = itemView.findViewById(R.id.thumbnail);
            fileName = itemView.findViewById(R.id.video_file_name);
            videoDuration = itemView.findViewById(R.id.video_duration);
        }
    }
}
