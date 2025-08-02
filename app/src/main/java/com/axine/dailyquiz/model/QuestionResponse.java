package com.axine.dailyquiz.model;

import java.util.List;

public class QuestionResponse {
    private int response_code;
    private List<ApiQuestion> results;

    public int getResponseCode() { return response_code; }
    public List<ApiQuestion> getQuestions() { return results; }
}