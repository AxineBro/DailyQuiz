package com.axine.dailyquiz.model;

import java.util.List;

public class ApiQuestion {
    private String category;
    private String type;
    private String difficulty;
    private String question;
    private List<String> incorrect_answers;
    private String correct_answer;

    public String getCategory() { return category; }
    public String getType() { return type; }
    public String getDifficulty() { return difficulty; }
    public String getQuestion() { return question; }
    public List<String> getIncorrectAnswers() { return incorrect_answers; }
    public String getCorrectAnswer() { return correct_answer; }
}