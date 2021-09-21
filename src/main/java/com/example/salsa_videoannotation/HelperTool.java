package com.example.salsa_videoannotation;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import java.util.concurrent.TimeUnit;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;

/**
 * HelperTool containing static methods to provide additional functionality that did not fit within the concerns of
 * other classes
 */
public class HelperTool
{
    public HelperTool()
    {

    }

    /**
     * Creates a min:sec representation of the Millisecond time from the ExoPlayer
     * @param time
     * @return
     */
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

    /**
     * Method called to get the AnnotationWrapper by the VideoId
     * @param id
     * @return
     */
    public static AnnotationWrapper getAnnotationByVideoId(String id)
    {
        return getOrCreateAnnotationByVideoIdAndPath(id, AnnotationWrapper.PLACEHOLDER_VIDEOFILE_PATH);
    }

    /**
     * Method called to get or create a new AnnotationWrapper by the VideoId and video filepath
     * @param id
     * @param filepath
     * @return
     */
    public static AnnotationWrapper getOrCreateAnnotationByVideoIdAndPath(String id, String filepath)
    {
        // The Teacher name is temporary and would be replaced with a username when profiles would theoretically
        // get implemented
        if (annotationWrapperList.containsKey(id))
            return annotationWrapperList.get(id);
        else
            return new AnnotationWrapper(id, "Teacher", filepath);
    }

    /**
     * Acquires the videoFrame used as the thumbnail for an Annotation
     * @param time
     * @param filepath
     * @return
     */
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
