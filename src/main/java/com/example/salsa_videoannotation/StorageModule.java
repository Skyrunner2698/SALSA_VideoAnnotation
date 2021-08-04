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

public class StorageModule
{
    private static final String FOLDER_NAME = "SALSAAnnotations";
    private static final String FILENAME = "XmlTest1.xml";

    public StorageModule()
    {

    }

    public boolean isExternalStorageWritable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private File getPrimaryExternalStorageVolume(Context context)
    {
        File[] externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null);
        return externalStorageVolumes[0];
    }

    public boolean storeXML(Context context)
    {
        try
        {
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + FOLDER_NAME);
            if(!directory.exists())
            {
                directory.mkdir();
            }
            Annotations annotations = new Annotations("1", "Test1", "Critique");

            Serializer serializer = new Persister();
            File toSave = new File(directory, FILENAME);
            serializer.write(annotations, toSave);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    public String loadXML(Context context)
    {
        try
        {
            File directory = new File(getPrimaryExternalStorageVolume(context) + "/" + FOLDER_NAME);
            File[] files = directory.listFiles();
            File fileToLoad = files[0];

            Serializer serializer = new Persister();
            Annotations annotations = serializer.read(Annotations.class, fileToLoad);
            return annotations.getType();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "Not Found";
    }
}
