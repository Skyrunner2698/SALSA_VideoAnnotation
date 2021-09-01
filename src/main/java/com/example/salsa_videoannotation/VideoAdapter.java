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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    public static final int VIDEO_TYPE_FEEDBACK = 0;
    public static final int VIDEO_TYPE_QUIZ = 1;
    public static final int VIDEO_TYPE_QUIZ_CREATION = 2;
    public static final int VIDEO_TYPE_FEEDBACK_VIEWING = 3;
    private Context mContext;
    static ArrayList<VideoFiles> videoFiles;
    View view;
    private int videoType;

    public VideoAdapter(Context mContext, ArrayList<VideoFiles> videoFiles, int videoType) {
        this.mContext = mContext;
        this.videoFiles = videoFiles;
        this.videoType = videoType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileName.setText(videoFiles.get(position).getTitle());
        holder.fileName.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.fileName.setSelected(true);
            }
        }, 1000);
        holder.videoDuration.setText(videoFiles.get(position).getDuration());
        Glide.with(mContext).load(new File(videoFiles.get(position).getPath())).into(holder.thumbNail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("videoType", videoType);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

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
