package com.example.salsa_videoannotation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AnnotationDisplayAdapter extends RecyclerView.Adapter<AnnotationDisplayAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<AnnotationData> annotations;
    private Annotations annotationWrapper;
    View view;

    public AnnotationDisplayAdapter(Context mContext, Annotations annotationWrapper) {
        this.mContext = mContext;
        this.annotationWrapper = annotationWrapper;
        this.annotations = new ArrayList<AnnotationData>(annotationWrapper.getVideoAnnotationsMap().values());

        Collections.sort(annotations, new Comparator<AnnotationData>() {
            @Override
            public int compare(AnnotationData o1, AnnotationData o2) {
                return (int) (o1.getStartTime() - o2.getStartTime());
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.annotation_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnotationDisplayAdapter.MyViewHolder holder, int position) {
        long startTime = annotations.get(position).getStartTime();
        String time = HelperTool.createTimeRepresentation(startTime);
        holder.fileName.setText(time + " - Feedback " + (position + 1));
        Glide.with(mContext).load(annotations.get(position).getThumbnail()).into(holder.thumbNail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("annotationId", annotations.get(position).getId());
                bundle.putString("annotationWrapperId", annotationWrapper.getId());

                AnnotationControlsFragment fragment = new AnnotationControlsFragment();
                fragment.setArguments(bundle);

                FragmentManager manager = ((PlayerActivity)mContext).getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.annotation_section, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return annotations.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView thumbNail;
        TextView fileName;
        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            thumbNail = itemView.findViewById(R.id.annotation_thumbnail);
            fileName = itemView.findViewById(R.id.annotation_feedback_name);
        }
    }
}
