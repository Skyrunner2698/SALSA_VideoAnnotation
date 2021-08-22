//package com.example.salsa_videoannotation;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.io.File;
//import java.util.ArrayList;
//
//public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.MyViewHolder> {
//    private Context mContext;
//    static ArrayList<VideoFiles> folderVideoFiles;
//    View view;
//
//    public VideoFolderAdapter(Context mContext, ArrayList<VideoFiles> folderVideoFiles) {
//        this.mContext = mContext;
//        this.folderVideoFiles = folderVideoFiles;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.fileName.setText(folderVideoFiles.get(position).getTitle());
//        holder.videoDuration.setText(folderVideoFiles.get(position).getDuration());
//        Glide.with(mContext).load(new File(folderVideoFiles.get(position).getPath())).into(holder.thumbNail);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, com.example.salsa_videoannotation.PlayerActivity.class);
//                intent.putExtra("position", position);
//                intent.putExtra("sender", "FolderIsSending");
//                mContext.startActivity(intent);
//            }
//        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return false;
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return folderVideoFiles.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder
//    {
//        ImageView thumbNail;
//        TextView fileName, videoDuration;
//        public MyViewHolder(@NonNull View itemView)
//        {
//            super(itemView);
//            thumbNail = itemView.findViewById(R.id.thumbnail);
//            fileName = itemView.findViewById(R.id.video_file_name);
//            videoDuration = itemView.findViewById(R.id.video_duration);
//        }
//    }
//}
