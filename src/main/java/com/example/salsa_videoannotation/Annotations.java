package com.example.salsa_videoannotation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root
public class Annotations
{
    public static final int CREATE_TRANSACTION = 1;
    public static final int UPDATE_TRANSACTION = 2;
    public static final int DELETE_TRANSACTION = 3;
    public static final int PLACEHOLDER_VIDEO_ANNOTATION_ID = 0;
    @Attribute
    private String id;
    @ElementList
    private ArrayList<AnnotationData> videoAnnotations = new ArrayList<>();
    @Element
    private String name;

    public Annotations()
    {
    }

    public Annotations(String id, String name)
    {
        this.id = id;
        this.name = name;

//        AnnotationData data1 = new AnnotationData(id + "-1", "Games", "10000", "This game needs more work.");
//        AnnotationData data2 = new AnnotationData(id + "-2", "Bugs", "1342", "Clipping.");
//
//        videoAnnotations.add(data1);
//        videoAnnotations.add(data2);
    }

    public void handleAnnotationManipulation(int transactionType, int videoAnnotationId, long startTime, long endTime, List<String> category, List<String> bodyPart, String content)
    {
        switch (transactionType)
        {
            case CREATE_TRANSACTION:
                addNewAnnotation(startTime, endTime, category, bodyPart, content);
                break;
            case UPDATE_TRANSACTION:
                updateAnnotation();
                break;
            case DELETE_TRANSACTION:
                deleteAnnotation();
                break;
        }
    }

    private void deleteAnnotation()
    {

    }

    private void updateAnnotation()
    {

    }

    private void addNewAnnotation(long startTime, long endTime, List<String> category, List<String> bodyPart, String content)
    {
        AnnotationData newAnnotation = new AnnotationData(videoAnnotations.size() + 1, startTime, endTime, category, bodyPart, content);
        videoAnnotations.add(newAnnotation);
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

    public ArrayList<AnnotationData> getVideoAnnotations()
    {
        return videoAnnotations;
    }


    public void setVideoAnnotations(ArrayList<AnnotationData> videoAnnotations)
    {
        this.videoAnnotations = videoAnnotations;
    }
}
