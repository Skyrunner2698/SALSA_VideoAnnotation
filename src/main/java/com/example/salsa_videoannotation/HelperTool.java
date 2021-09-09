package com.example.salsa_videoannotation;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import java.util.concurrent.TimeUnit;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

public class HelperTool
{
    public HelperTool()
    {

    }

    public static String createTimeRepresentation(long time)
    {
        time += 1000;
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
    }

    public static long convertTimeToMilliseconds(String time)
    {
        int pos = time.indexOf(":");
        String min = time.substring(0, pos);
        String sec = time.substring(pos + 1);
        return TimeUnit.MINUTES.toMillis(Long.parseLong(min)) + TimeUnit.SECONDS.toMillis(Long.parseLong(sec));
    }

    public static AnnotationWrapper getAnnotationByVideoId(String id)
    {
        return getOrCreateAnnotationByVideoIdAndPath(id, AnnotationWrapper.PLACEHOLDER_VIDEOFILE_PATH);
    }

    public static AnnotationWrapper getOrCreateAnnotationByVideoIdAndPath(String id, String filepath)
    {
        if (annotationWrapperList.containsKey(id))
            return annotationWrapperList.get(id);
        else
            return new AnnotationWrapper(id, "Teacher", filepath);
    }

    public static Bitmap getVideoFrame(long time, String filepath) {

        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {

            retriever.setDataSource(filepath);
            bitmap = retriever.getFrameAtTime(time*1000);

        } catch (RuntimeException ex) {
            ex.printStackTrace();

        } finally {

            try {

                retriever.release();

            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }

        return bitmap;
    }

}
