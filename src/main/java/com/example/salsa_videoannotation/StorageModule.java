package com.example.salsa_videoannotation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StorageModule
{
    private static final String FOLDER_NAME = "SALSAAnnotations";
    private static final String THUMBNAIL_FOLDER_NAME_PREFIX = "Thumbnails";
    private static final String FEEDBACK_FILENAME_STARTER = "FeedbackAnnotation";
    private static final String XML_FILE_EXTENSION = ".xml";
    private static final String THUMBNAIL_EXTENSION = ".png";

    public StorageModule()
    {

    }

    public static boolean isExternalStorageWritable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private static File getPrimaryExternalStorageVolume(Context context)
    {
        File[] externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null);
        return externalStorageVolumes[0];
    }

    public static boolean storeXML(Context context, Annotations annotation)
    {
        try
        {
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + FOLDER_NAME);
            if(!directory.exists())
            {
                directory.mkdir();
            }
            String fileName = FEEDBACK_FILENAME_STARTER + annotation.getId() + XML_FILE_EXTENSION;
            Serializer serializer = new Persister();
            File toSave = new File(directory, fileName);
            serializer.write(annotation, toSave);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public static HashMap<String, Annotations> loadXML(Context context)
    {
        HashMap<String, Annotations> tempAnnotationsWrapper = new HashMap<>();
        try
        {
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + FOLDER_NAME);
            File[] files = directory.listFiles();

            for(File fileToLoad : files)
            {
                Serializer serializer = new Persister();
                Annotations annotations = serializer.read(Annotations.class, fileToLoad);
                tempAnnotationsWrapper.put(annotations.getId(), annotations);
            }

            for(Map.Entry mapElement : tempAnnotationsWrapper.entrySet())
            {
                Annotations annotationWrapper = (Annotations) mapElement.getValue();
                String filepath = getPrimaryExternalStorageVolume(context) + "/" + THUMBNAIL_FOLDER_NAME_PREFIX + annotationWrapper.getId();
                for(Map.Entry innerMapElement : annotationWrapper.getVideoAnnotationsMap().entrySet())
                {
                    AnnotationData annotation = (AnnotationData) innerMapElement.getValue();
                    String finalfilepath = filepath + "/" + annotation.getId() + THUMBNAIL_EXTENSION;
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

    public static boolean storeThumbnail(Context context, String annotationId, int annotationDataId, Bitmap thumbnail)
    {
        try
        {
            String folderName = THUMBNAIL_FOLDER_NAME_PREFIX + annotationId;
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + folderName);
            if(!directory.exists())
            {
                directory.mkdir();
            }
            String fileName = annotationDataId + THUMBNAIL_EXTENSION;
            OutputStream fOut = null;
            File file = new File(directory, fileName); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            fOut = new FileOutputStream(file);

            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
