package com.example.salsa_videoannotation;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class AnnotationData
{
    @Attribute
    private String id;
    @Element
    private String startTime;
    @Element
    private String endTime;
    @ElementList
    private List<String> category;
    @ElementList
    private List<String> bodyPart;

    public AnnotationData()
    {

    }

    public AnnotationData(String id, String startTime, String endTime, List<String> category, List<String> bodyPart) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.bodyPart = bodyPart;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public List<String> getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(List<String> bodyPart) {
        this.bodyPart = bodyPart;
    }
}
