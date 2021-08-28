package com.example.salsa_videoannotation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class QuizQuestion
{
    @Element
    private String question;
    @Element
    private String correctAnswer;
    @Element
    private String answer1;
    @Element
    private String answer2;
    @Element
    private String answer3;

    public QuizQuestion()
    {
        
    }

    public QuizQuestion(String question, String correctAnswer, String answer1, String answer2, String answer3) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }
}
