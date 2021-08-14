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
    private int id;
    @Element
    private long startTime;
    @Element
    private long endTime;
    @ElementList
    private List<String> category;
    @ElementList
    private List<String> bodyPart;
    @Element
    private String content;

    public AnnotationData()
    {

    }

    public AnnotationData(int id, long startTime, long endTime, List<String> category, List<String> bodyPart, String content) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.bodyPart = bodyPart;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
