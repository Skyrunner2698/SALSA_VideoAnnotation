package com.example.salsa_videoannotation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Object to hold the question and answers for a Quiz Annotation
 */
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
        String tempQuestion;
        // Checks if a question does contain a question mark and adds one if not
        if(!question.contains("?"))
            tempQuestion =  question + "?";
        else
            tempQuestion = question;
        // Capitalizes the first letter of every question and answer
        this.question = tempQuestion.substring(0,1).toUpperCase() + tempQuestion.substring(1);;
        this.correctAnswer = correctAnswer.substring(0,1).toUpperCase() + correctAnswer.substring(1);
        this.answer1 = answer1.substring(0,1).toUpperCase() + answer1.substring(1);
        this.answer2 = answer2.substring(0,1).toUpperCase() + answer2.substring(1);
        this.answer3 = answer3.substring(0,1).toUpperCase() + answer3.substring(1);
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
