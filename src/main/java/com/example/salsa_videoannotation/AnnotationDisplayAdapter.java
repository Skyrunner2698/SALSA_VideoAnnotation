package com.example.salsa_videoannotation;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AnnotationDisplayAdapter extends RecyclerView.Adapter<AnnotationDisplayAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<AnnotationData> annotations;
    private Annotations annotationWrapper;
    View view;
    private int displayType;

    public AnnotationDisplayAdapter(Context mContext, Annotations annotationWrapper, int displayType) {
        this.mContext = mContext;
        this.annotationWrapper = annotationWrapper;
        this.annotations = new ArrayList<AnnotationData>(annotationWrapper.getVideoAnnotationsMap().values());
        this.displayType = displayType;

        // Comparator method to sort list of annotations by startTime (ascending order)
        Collections.sort(annotations, new Comparator<AnnotationData>() {
            @Override
            public int compare(AnnotationData o1, AnnotationData o2) {
                return (int) (o1.getStartTime() - o2.getStartTime());
            }
        });
    }

    @NonNull
    @Override
    // Inflates the my view holder using the annotation_item xml file
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.annotation_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnotationDisplayAdapter.MyViewHolder holder, int position) {
        // assigns the filename for annotation items using startTime and a Feedback and number naming system
        long startTime = annotations.get(position).getStartTime();
        String time = HelperTool.createTimeRepresentation(startTime);
        if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK || displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK_VIEWING)
            holder.fileName.setText(time + " - Feedback " + (position + 1));
        else
            holder.fileName.setText(time + " - Question " + (position + 1));
        // Thumbnail for annotation is set using glide and a bitmap image stored on the annotationData object
        Glide.with(mContext).load(annotations.get(position).getThumbnail()).into(holder.thumbNail);
        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.Menus, null));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating a bundle with the annotationId and annotationWrapperId to acquire these items on the
                // AnnotationControlFragment for populating the existing data to edit
                Bundle bundle = new Bundle();
                bundle.putInt("annotationId", annotations.get(position).getId());
                bundle.putString("annotationWrapperId", annotationWrapper.getId());

                if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK) {
                    AnnotationDetailsFragmentEditing fragment = new AnnotationDetailsFragmentEditing();
                    fragment.setArguments(bundle);
                    FragmentManager manager = ((PlayerActivity) mContext).getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.annotation_section, fragment).commit();
                }
                else if(displayType == VideoAdapter.VIDEO_TYPE_QUIZ_CREATION)
                {
                    QuizCreationFragmentEditing fragment = new QuizCreationFragmentEditing();
                    fragment.setArguments(bundle);
                    FragmentManager manager = ((PlayerActivity) mContext).getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.annotation_section, fragment).commit();
                }
                else if(displayType == VideoAdapter.VIDEO_TYPE_QUIZ)
                {
                    QuizAnsweringFragment fragment = new QuizAnsweringFragment();
                    fragment.setArguments(bundle);
                    FragmentManager manager = ((PlayerActivity) mContext).getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.annotation_section, fragment).commit();
                }
                else if(displayType == VideoAdapter.VIDEO_TYPE_FEEDBACK_VIEWING)
                {
                    ViewFeedbackFragment fragment = new ViewFeedbackFragment();
                    fragment.setArguments(bundle);
                    FragmentManager manager = ((PlayerActivity) mContext).getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.annotation_section, fragment).commit();
                }
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

        public void onDeleteClick(View view)
        {
            AnnotationData annotationToDelete = annotations.get(getAdapterPosition());
        }
    }
}
