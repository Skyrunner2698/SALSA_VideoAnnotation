package com.example.salsa_videoannotation;

import android.content.Context;
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
import java.util.ArrayList;

public class StorageModule
{
    private static final String FOLDER_NAME = "SALSAAnnotations";
    private static final String FEEDBACK_FILENAME_STARTER = "FeedbackAnnotation";
    private static final String XML_FILE_EXTENSION = ".xml";

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

    public static ArrayList<Annotations> loadXML(Context context)
    {
        ArrayList<Annotations> tempAnnotationsList = new ArrayList<>();
        try
        {
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + FOLDER_NAME);
            File[] files = directory.listFiles();

            for(File fileToLoad : files)
            {
                Serializer serializer = new Persister();
                Annotations annotations = serializer.read(Annotations.class, fileToLoad);
                tempAnnotationsList.add(annotations);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tempAnnotationsList;
    }
}
