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

/**
 * Adapter class used in conjunction with RecyclerView on the AnnotationDisplayFragment
 * Populates the RecyclerView and sets up the onClick event for the items
 */
public class AnnotationDisplayAdapter extends RecyclerView.Adapter<AnnotationDisplayAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<Annotations> annotations;
    private AnnotationWrapper annotationWrapper;
    View view;
    private int displayType;

    /**
     * Constructor for the Adapter
     * Gets the AnnotationWrapper to use and the display type to control the labelling
     * @param mContext
     * @param annotationWrapper
     * @param displayType
     */
    public AnnotationDisplayAdapter(Context mContext, AnnotationWrapper annotationWrapper, int displayType) {
        this.mContext = mContext;
        this.annotationWrapper = annotationWrapper;
        this.annotations = new ArrayList<Annotations>(annotationWrapper.getSortedVideoAnnotationsMap().values());
        this.displayType = displayType;
    }

    /**
     * Inflates the layout used for the items in the RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    // Inflates the my view holder using the annotation_item xml file
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.annotation_item, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Populates the annotation_item layout with the required values and sets onClick behaviour
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull AnnotationDisplayAdapter.MyViewHolder holder, int position) {
        // creates part of the filename for annotation items using startTime
        long startTime = annotations.get(position).getStartTime();
        String time = HelperTool.createTimeRepresentation(startTime);
        // Creates the correct name for the annotation_item (Feedback or Question depending on section)
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
                // Creates a bundle with the annotationId and annotationWrapperId to acquire these items on the
                // AnnotationDetailsFragmentEditing and QuizCreationFragmentEditing to populate data.
                // Adding the ids (keys) of the previous and next annotation in sorted order to the bundle to
                // be used with the prev and next arrows on the above mentioned fragments.
                Bundle bundle = new Bundle();
                bundle.putInt("annotationId", annotations.get(position).getId());
                if (position != 0)
                    bundle.putInt("prevAnnotationId", annotations.get(position-1).getId());
                else
                    bundle.putInt("prevAnnotationId", -1);
                if (position != annotations.size() -1)
                    bundle.putInt("nextAnnotationId", annotations.get(position+1).getId());
                else
                    bundle.putInt("nextAnnotationId", -1);
                bundle.putString("annotationWrapperId", annotationWrapper.getId());

                // Decides which layout to inflate based on the displayType of the current section
                // Feedback Creation, Feedback Viewing, Quiz Creation or Quiz Answering
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

    /**
     * Required method to override for creating an adapter
     * @return
     */
    @Override
    public int getItemCount() {
        return annotations.size();
    }

    /**
     * Class to represent the annotation_item layout when inflated for population
     */
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
