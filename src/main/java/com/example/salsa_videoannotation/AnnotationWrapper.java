package com.example.salsa_videoannotation;

import android.graphics.Bitmap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.List;

@Root
public class AnnotationWrapper
{
    public static final int CREATE_TRANSACTION = 1;
    public static final int UPDATE_TRANSACTION = 2;
    public static final String PLACEHOLDER_VIDEOFILE_PATH = "Holder";
    @Attribute
    private String id;
    @Element
    private String name;
    @Element
    private String videoFilePath;
    @ElementMap
    private HashMap<Integer, Annotations> videoAnnotationsMap = new HashMap<>();

    public AnnotationWrapper()
    {
    }

    public AnnotationWrapper(String id, String name, String videoFilePath)
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
                addNewAnnotation(videoAnnotationId, startTime, category, bodyPart, content, annotationThumbnail, quizQuestion);
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
        Annotations updatedAnnotation = new Annotations(annotationId, startTime, category, bodyPart, content, quizQuestion, annotationThumbnail);
        videoAnnotationsMap.replace(annotationId, updatedAnnotation);
    }

    private void addNewAnnotation(int newAnnotationId, long startTime, List<String> category, List<String> bodyPart, String content, Bitmap annotationThumbnail, QuizQuestion quizQuestion)
    {
        Annotations newAnnotation = new Annotations(newAnnotationId, startTime, category, bodyPart, content, quizQuestion, annotationThumbnail);
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

    public HashMap<Integer, Annotations> getVideoAnnotationsMap() {
        return videoAnnotationsMap;
    }

    public void setVideoAnnotationsMap(HashMap<Integer, Annotations> videoAnnotationsMap) {
        this.videoAnnotationsMap = videoAnnotationsMap;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }
}
