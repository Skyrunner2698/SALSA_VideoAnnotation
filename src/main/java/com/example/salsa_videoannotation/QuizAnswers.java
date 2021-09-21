package com.example.salsa_videoannotation;

/**
 * Object to hold the answer status and answer text for an answered quiz question
 * Used to track the answers given
 */
public class QuizAnswers
{
    private int answerStatus;
    private String answerText;

    public QuizAnswers(int answerStatus)
    {
        this.answerStatus = answerStatus;
    }

    public QuizAnswers(int answerStatus, String answerText) {
        this.answerStatus = answerStatus;
        this.answerText = answerText;
    }

    public int getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(int answerStatus) {
        this.answerStatus = answerStatus;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
