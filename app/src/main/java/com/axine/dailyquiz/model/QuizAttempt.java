package com.axine.dailyquiz.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quiz_attempt")
public class QuizAttempt {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private int score;
    private long timestamp;

    public QuizAttempt(int score, long timestamp) {
        this.score = score;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}