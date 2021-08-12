package com.example.salsa_videoannotation;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

@Root
public class Annotations
{
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

    public void handleAnnotationManipulation()
    {

    }

    public void deleteAnnotation()
    {

    }

    public void updateAnnotation()
    {

    }

    public void addNewAnnotation()
    {

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
