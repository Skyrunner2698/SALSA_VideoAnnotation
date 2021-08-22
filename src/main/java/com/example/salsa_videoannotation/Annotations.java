package com.example.salsa_videoannotation;

import android.graphics.Bitmap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Root
public class Annotations
{
    public static final int CREATE_TRANSACTION = 1;
    public static final int UPDATE_TRANSACTION = 2;
    public static final int DELETE_TRANSACTION = 3;
    public static final int PLACEHOLDER_VIDEO_ANNOTATION_ID = 0;
    public static final String PLACEHOLDER_VIDEOFILE_PATH = "Holder";
    public static final int DELETE_WRAPPER_ID_KEY = 0;
    public static final int DELETE_ANNOTATION_ID_KEY = 1;
    @Attribute
    private String id;
    @Element
    private String name;
    @Element
    private String videoFilePath;
    @ElementMap
    private HashMap<Integer, AnnotationData> videoAnnotationsMap = new HashMap<>();

    public Annotations()
    {
    }

    public Annotations(String id, String name, String videoFilePath)
    {
        this.id = id;
        this.name = name;
        this.videoFilePath = videoFilePath;
    }

    public void handleAnnotationManipulation(int transactionType, int videoAnnotationId, long startTime, List<String> category, List<String> bodyPart, String content, Bitmap annotationThumbnail, QuizQuestion quizQuestion)
    {
        switch (transactionType)
        {
            case CREATE_TRANSACTION:
                addNewAnnotation(startTime, category, bodyPart, content, annotationThumbnail, quizQuestion);
                break;
            case UPDATE_TRANSACTION:
                updateAnnotation(videoAnnotationId, startTime, category, bodyPart, content, annotationThumbnail, quizQuestion);
                break;
        }
    }

    public void deleteAnnotation(int annotationId)
    {
        videoAnnotationsMap.remove(annotationId);
    }

    private void updateAnnotation(int annotationId, long startTime, List<String> category, List<String> bodyPart, String content, Bitmap annotationThumbnail, QuizQuestion quizQuestion)
    {
        AnnotationData updatedAnnotation = new AnnotationData(annotationId, startTime, category, bodyPart, content, quizQuestion, annotationThumbnail);
        videoAnnotationsMap.replace(annotationId, updatedAnnotation);
    }

    private void addNewAnnotation(long startTime, List<String> category, List<String> bodyPart, String content, Bitmap annotationThumbnail, QuizQuestion quizQuestion)
    {
        AnnotationData newAnnotation = new AnnotationData(videoAnnotationsMap.size() + 1, startTime, category, bodyPart, content, quizQuestion, annotationThumbnail);
        videoAnnotationsMap.put(newAnnotation.getId(), newAnnotation);
    }

    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Integer, AnnotationData> getVideoAnnotationsMap() {
        return videoAnnotationsMap;
    }

    public void setVideoAnnotationsMap(HashMap<Integer, AnnotationData> videoAnnotationsMap) {
        this.videoAnnotationsMap = videoAnnotationsMap;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }
}
