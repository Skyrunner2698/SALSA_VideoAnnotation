package com.example.salsa_videoannotation;


import android.graphics.Bitmap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class Annotations
{
    public static final String[] CATEGORIES = {"Step Direction", "Foot Position", "Step Size", "Weight Transfer", "Movement Quality", "Timing", "Rhythm", "Suggested Moves to try"};
    public static final String[] BODYPARTS = {"Head", "Shoulders", "Arms", "Hands", "Torso", "Hips", "Legs", "Feet"};
    @Attribute
    private int id;
    @Element
    private long startTime;
    @ElementList
    private List<String> category;
    @ElementList
    private List<String> bodyPart;
    @Element(required = false)
    private String content;
    @Element(required = false)
    private QuizQuestion quizQuestion;
    private Bitmap thumbnail;

    public Annotations()
    {

    }

    public Annotations(int id, long startTime, List<String> category, List<String> bodyPart, String content, Bitmap thumbnail) {
        this(id, startTime, category, bodyPart, content, null, thumbnail);
    }

    public Annotations(int id, long startTime, List<String> category, List<String> bodyPart, String content, QuizQuestion quizQuestion, Bitmap thumbnail) {
        this.id = id;
        this.startTime = startTime;
        this.category = category;
        this.bodyPart = bodyPart;
        this.content = content;
        this.quizQuestion = quizQuestion;
        this.thumbnail = thumbnail;
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

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public QuizQuestion getQuizQuestion() {
        return quizQuestion;
    }

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
    }
}
