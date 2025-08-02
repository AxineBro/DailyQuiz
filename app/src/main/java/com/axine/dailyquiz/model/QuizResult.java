package com.axine.dailyquiz.model;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Date;

public class QuizResult {
    private static QuizResult instance;
    private ArrayList<Integer> selectedAnswers;
    private int correctAnswers;
    private Date quizDate;

    private static final String PREFS_NAME = "QuizHistory";
    private static final String KEY_ANSWERS = "SelectedAnswers";
    private static final String KEY_CORRECT = "CorrectAnswers";
    private static final String KEY_DATE = "QuizDate";

    private QuizResult() {
        selectedAnswers = new ArrayList<>();
        correctAnswers = 0;
        quizDate = new Date();
    }

    public static synchronized QuizResult getInstance() {
        if (instance == null) {
            instance = new QuizResult();
        }
        return instance;
    }

    public void saveQuizResult(Context context, ArrayList<Integer> answers, int correct) {
        selectedAnswers.clear();
        selectedAnswers.addAll(answers);
        correctAnswers = correct;
        quizDate = new Date();

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CORRECT, correctAnswers);
        editor.putLong(KEY_DATE, quizDate.getTime());
        String answersString = android.text.TextUtils.join(",", selectedAnswers);
        editor.putString(KEY_ANSWERS, answersString);
        editor.apply();
    }

    public ArrayList<Integer> getSelectedAnswers() {
        return selectedAnswers;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public Date getQuizDate() {
        return quizDate;
    }

    public void loadQuizResult(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        correctAnswers = prefs.getInt(KEY_CORRECT, 0);
        quizDate = new Date(prefs.getLong(KEY_DATE, System.currentTimeMillis()));
        String answersString = prefs.getString(KEY_ANSWERS, "");
        if (!answersString.isEmpty()) {
            String[] answerArray = answersString.split(",");
            selectedAnswers.clear();
            for (String answer : answerArray) {
                selectedAnswers.add(Integer.parseInt(answer));
            }
        }
    }
}