package com.example.salsa_videoannotation;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for loading and saving all Annotations and Thumbnails and loading all videos
 */
public class StorageModule
{
    private static final String FOLDER_NAME = "SALSAAnnotations";
    private static final String THUMBNAIL_FOLDER_NAME_PREFIX = "Thumbnails";
    private static final String FEEDBACK_FILENAME_STARTER = "FeedbackAnnotation";
    private static final String XML_FILE_EXTENSION = ".xml";
    private static final String THUMBNAIL_EXTENSION = ".png";
    private static final String FEEDBACK_FOLDER_NAME = "Feedback";
    private static final String QUIZ_FOLDER_NAME = "Quiz";

    public StorageModule()
    {

    }

    /**
     * Checks if external storage is available and writable
     * @return
     */
    public static boolean isExternalStorageWritable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Acquires the file which contains the External Storage area
     * @param context
     * @return
     */
    private static File getPrimaryExternalStorageVolume(Context context)
    {
        File[] externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null);
        return externalStorageVolumes[0];
    }

    /**
     * Serializes and saves the AnnotationWrapper objects in XML files
     * @param context
     * @param annotationWrapper
     * @return
     */
    public static boolean storeXML(Context context, AnnotationWrapper annotationWrapper)
    {
        try
        {
            // Opens a file in the path to save the XML in
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + FOLDER_NAME);
            // Creates the directory if it doesn't exist
            if(!directory.exists())
            {
                directory.mkdir();
            }
            // Creates the unique filename for the AnnotationWrapper
            String fileName = FEEDBACK_FILENAME_STARTER + annotationWrapper.getId() + XML_FILE_EXTENSION;
            Serializer serializer = new Persister();
            // Creates the new file to save
            File toSave = new File(directory, fileName);
            // Serializes the annotationWrapper content to the toSave file
            serializer.write(annotationWrapper, toSave);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Loads all the AnnotationWrapper XML details and reads them back into objects
     * @param context
     * @return
     */
    public static HashMap<String, AnnotationWrapper> loadXML(Context context)
    {
        HashMap<String, AnnotationWrapper> tempAnnotationsWrapper = new HashMap<>();
        try
        {
            // Opens a file in the path where the XML is saved
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + FOLDER_NAME);
            // Gets all files saved in the directory
            File[] files = directory.listFiles();

            // Loops the files to load them all
            for(File fileToLoad : files)
            {
                Serializer serializer = new Persister();
                // Creates a new annotationWrapper object from the file
                AnnotationWrapper annotationWrapper = serializer.read(AnnotationWrapper.class, fileToLoad);
                // Sorts the Annotation objects stored within the AnnotationWrapper object
                annotationWrapper.sortAnnotations();
                // Adds the annotationWrapper to the temporary Hashmap
                tempAnnotationsWrapper.put(annotationWrapper.getId(), annotationWrapper);
            }

            // Loops all AnnotationWrapper objects in temp list
            for(Map.Entry mapElement : tempAnnotationsWrapper.entrySet())
            {
                AnnotationWrapper annotationWrapper = (AnnotationWrapper) mapElement.getValue();
                // Constructs the filepath to the AnnotationWrapper's thumbnails folder
                String filepath = getPrimaryExternalStorageVolume(context) + "/" + THUMBNAIL_FOLDER_NAME_PREFIX + annotationWrapper.getId();
                // Loops all Annotations on AnnotationWrapper object
                for(Map.Entry innerMapElement : annotationWrapper.getVideoAnnotationsMap().entrySet())
                {
                    Annotations annotation = (Annotations) innerMapElement.getValue();
                    // Gets filepath to the required thumbnail
                    String finalfilepath = filepath + "/" + annotation.getId() + THUMBNAIL_EXTENSION;
                    // Decodes the file to a usable bitmap and sets the variable on the Annotation object
                    annotation.setThumbnail(BitmapFactory.decodeFile(finalfilepath));
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempAnnotationsWrapper;
    }

    /**
     * Stores the Annotation Thumbnail
     * @param context
     * @param annotationId
     * @param annotationDataId
     * @param thumbnail
     * @return
     */
    public static boolean storeThumbnail(Context context, String annotationId, int annotationDataId, Bitmap thumbnail)
    {
        try
        {
            // Constructs the folder name
            String folderName = THUMBNAIL_FOLDER_NAME_PREFIX + annotationId;
            // Creates a file with a path to the desired storage location
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + folderName);
            // Creates directory if it does not exist
            if(!directory.exists())
            {
                directory.mkdir();
            }
            // Creates the file name for the thumbnail
            String fileName = annotationDataId + THUMBNAIL_EXTENSION;
            OutputStream fOut = null;
            // Writes the file to save
            File file = new File(directory, fileName);
            fOut = new FileOutputStream(file);

            // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            // closes the output stream
            fOut.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Deletes a Thumbnail for an Annotation
     * @param context
     * @param annotationWrapperId
     * @param annotationId
     * @return
     */
    public static boolean deleteAnnotationThumbnail(Context context, String annotationWrapperId, int annotationId)
    {
        // Constructs the full filepath
        String filepath = getPrimaryExternalStorageVolume(context) + "/" + THUMBNAIL_FOLDER_NAME_PREFIX + annotationWrapperId + "/" + annotationId + THUMBNAIL_EXTENSION;
        //  Creates the file object
        File imageFile = new File(filepath);
        // Deletes the file
        return imageFile.delete();
    }

    /**
     * Loads all videos from the folders on the phone into the System.
     * Note all Videos to be used must be saved in user created folders in the phone's system called
     * "Feedback" and "Quiz". Only videos in these folders will be loaded. This was done due to time constraints
     * and not focussing on building a robust storage solution but rather testing the validity of the feature set
     * @param context
     */
    public static void getAllVideos(Context context)
    {
        // Gets URI to all Video media on the phone
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME
        };
        // Constructs a cursor to carry out the query set up above
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                String id = cursor.getString(0);
                String path = cursor.getString(1);
                String title = cursor.getString(2);
                String size = cursor.getString(3);
                String dateAdded = cursor.getString(4);
                String duration = HelperTool.createTimeRepresentation(cursor.getInt(5));
                String fileName = cursor.getString(6);


                // Gets the name of the folder from which this video was taken
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                int index = subString.lastIndexOf("/");
                String folder = subString.substring(index + 1);

                // Checks if the Folder matches the Feedback folder name variable
                if(folder.equals(FEEDBACK_FOLDER_NAME)) {
                    // Creates a new videoFiles object
                    VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration);
                    MainActivity.feedbackVideos.add(videoFiles);
                }
                // Checks if the Folder matches the Quiz folder name variable
                else if (folder.equals(QUIZ_FOLDER_NAME)) {
                    // Creates a new videoFiles object
                    VideoFiles videoFiles = new VideoFiles(id, path, title, fileName, size, dateAdded, duration);
                    MainActivity.quizVideos.add(videoFiles);
                }
            }
            cursor.close();
        }
    }
}
