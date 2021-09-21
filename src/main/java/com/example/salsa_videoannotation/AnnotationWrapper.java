package com.example.salsa_videoannotation;

import android.graphics.Bitmap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object to associate a collection of Annotations to a single video
 */
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
    private LinkedHashMap<Integer, Annotations> videoAnnotationsMap = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Annotations> sortedVideoAnnotationsMap = new LinkedHashMap<>();

    public AnnotationWrapper()
    {
    }

    public AnnotationWrapper(String id, String name, String videoFilePath)
    {
        this.id = id;
        this.name = name;
        this.videoFilePath = videoFilePath;
    }

    /**
     * Single method to abstract the creation and updating from users to protect insertion and updating
     * @param transactionType
     * @param videoAnnotationId
     * @param startTime
     * @param category
     * @param bodyPart
     * @param content
     * @param annotationThumbnail
     * @param quizQuestion
     */
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

    /**
     * Delete method for removing an annotation
     * @param annotationId
     */
    public void deleteAnnotation(int annotationId)
    {
        videoAnnotationsMap.remove(annotationId);
        sortedVideoAnnotationsMap.remove(annotationId);
    }

    /**
     * Private method for updating an Annotation
     * @param annotationId
     * @param startTime
     * @param category
     * @param bodyPart
     * @param content
     * @param annotationThumbnail
     * @param quizQuestion
     */
    private void updateAnnotation(int annotationId, long startTime, List<String> category, List<String> bodyPart, String content, Bitmap annotationThumbnail, QuizQuestion quizQuestion)
    {
        // Creates new Annotation object
        Annotations updatedAnnotation = new Annotations(annotationId, startTime, category, bodyPart, content, quizQuestion, annotationThumbnail);
        // Replaces the old Annotation object with the new one
        videoAnnotationsMap.replace(annotationId, updatedAnnotation);
        // Replaces the old Annotation object in the sorted list with the new one.
        sortedVideoAnnotationsMap.replace(annotationId, updatedAnnotation);
    }

    /**
     * Private method for creating an Annotation
     * @param newAnnotationId
     * @param startTime
     * @param category
     * @param bodyPart
     * @param content
     * @param annotationThumbnail
     * @param quizQuestion
     */
    private void addNewAnnotation(int newAnnotationId, long startTime, List<String> category, List<String> bodyPart, String content, Bitmap annotationThumbnail, QuizQuestion quizQuestion)
    {
        // Creates a new Annotation object using the predetermined ID
        Annotations newAnnotation = new Annotations(newAnnotationId, startTime, category, bodyPart, content, quizQuestion, annotationThumbnail);
        // Adds the new Annotation object to the LinkedHashMap
        videoAnnotationsMap.put(newAnnotation.getId(), newAnnotation);
        // Resorts the the Annotation LinkedHashMap
        sortAnnotations();
    }

    /**
     * Sorts the Annotation Objects by startTime ascending and puts them into the sortedVideoAnnotationMap
     */
    public void sortAnnotations()
    {
        Set<Map.Entry<Integer,Annotations>> entries = videoAnnotationsMap.entrySet();
        List<Map.Entry<Integer,Annotations>> listOfEntries = new ArrayList<>(entries);
        // Comparator method to sort list of annotations by startTime (ascending order)
        Collections.sort(listOfEntries, new Comparator<Map.Entry<Integer,Annotations>>() {
            @Override
            public int compare(Map.Entry<Integer, Annotations> o1, Map.Entry<Integer, Annotations> o2) {
                return (int) (o1.getValue().getStartTime() - o2.getValue().getStartTime());
            }
        });
        sortedVideoAnnotationsMap.clear();
        for(Map.Entry<Integer,Annotations> entry : listOfEntries)
        {
            sortedVideoAnnotationsMap.put(entry.getKey(), entry.getValue());
        }
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

    public LinkedHashMap<Integer, Annotations> getVideoAnnotationsMap() {
        return videoAnnotationsMap;
    }

    public void setVideoAnnotationsMap(LinkedHashMap<Integer, Annotations> videoAnnotationsMap) {
        this.videoAnnotationsMap = videoAnnotationsMap;
    }

    public String getVideoFilePath() {
        return videoFilePath;
    }

    public void setVideoFilePath(String videoFilePath) {
        this.videoFilePath = videoFilePath;
    }

    public LinkedHashMap<Integer, Annotations> getSortedVideoAnnotationsMap() {
        return sortedVideoAnnotationsMap;
    }

    public void setSortedVideoAnnotationsMap(LinkedHashMap<Integer, Annotations> sortedVideoAnnotationsMap) {
        this.sortedVideoAnnotationsMap = sortedVideoAnnotationsMap;
    }
}
