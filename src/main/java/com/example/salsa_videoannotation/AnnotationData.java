package com.example.salsa_videoannotation;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class AnnotationData
{
    @Attribute
    private String id;
    @Element
    private String category;
    @Element
    private String timeStamp;
    @Element
    private String content;

    public AnnotationData()
    {

    }

    public AnnotationData(String id, String category, String timeStamp, String content) {
        this.id = id;
        this.category = category;
        this.timeStamp = timeStamp;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
