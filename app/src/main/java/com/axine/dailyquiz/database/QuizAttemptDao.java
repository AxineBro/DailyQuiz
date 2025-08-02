package com.axine.dailyquiz.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.axine.dailyquiz.model.QuizAttempt;

import java.util.List;

@Dao
public interface QuizAttemptDao {

    @Insert
    void insert(QuizAttempt quizAttempt);

    @Query("SELECT * FROM quiz_attempt")
    List<QuizAttempt> getAllAttempts();

    @Query("DELETE FROM quiz_attempt WHERE id = :id")
    void deleteAttempt(Long id);
}