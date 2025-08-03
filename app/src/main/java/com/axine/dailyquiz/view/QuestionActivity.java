package com.axine.dailyquiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.axine.dailyquiz.R;
import com.axine.dailyquiz.database.AppDatabase;
import com.axine.dailyquiz.model.Question;
import com.axine.dailyquiz.model.QuizAttempt;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    private TextView currentTime, totalTime, questionText, progressText, resultText;
    private RadioGroup optionsGroup;
    private Button nextButton, retryButton;
    private View timeUpOverlay;
    private ImageView btnBack;
    private ArrayList<Question> questions;
    private ArrayList<Integer> selectedAnswers = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private boolean isResultShown = false;
    private CountDownTimer countDownTimer;
    private ProgressBar timerProgress;
    private int score = 0;

    private static final int QUIZ_TIME_SEC = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);
        questionText = findViewById(R.id.question_text);
        progressText = findViewById(R.id.progress_text);
        optionsGroup = findViewById(R.id.options_group);
        resultText = findViewById(R.id.result_text);
        nextButton = findViewById(R.id.next_button);
        timerProgress = findViewById(R.id.timer_progress);
        timeUpOverlay = findViewById(R.id.time_up_overlay);
        retryButton = findViewById(R.id.retry_button);
        btnBack = findViewById(R.id.btn_back);

        questions = getIntent().getParcelableArrayListExtra("questions");
        if (questions == null || questions.isEmpty()) {
            finish();
            return;
        }

        totalTime.setText(formatTime(QUIZ_TIME_SEC));

        btnBack.setOnClickListener(v -> finish());

        startTimer();
        loadQuestion();

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            nextButton.setEnabled(true);
            nextButton.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
            isResultShown = false;
            resultText.setVisibility(View.GONE);
        });

        nextButton.setOnClickListener(v -> {
            if (!isResultShown) {
                showResult();
            } else {
                nextQuestion();
            }
        });

        retryButton.setOnClickListener(v -> restartQuiz());
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(QUIZ_TIME_SEC * 1000L, 1000) {
            int timeLeft = QUIZ_TIME_SEC;

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft--;
                currentTime.setText(formatTime(timeLeft));
                timerProgress.setProgress(QUIZ_TIME_SEC - timeLeft);
            }

            @Override
            public void onFinish() {
                showTimeUpOverlay();
            }
        }.start();
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void loadQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        Question question = questions.get(currentQuestionIndex);
        progressText.setText("Вопрос " + (currentQuestionIndex + 1) + " из " + questions.size());
        questionText.setText(question.getText());

        for (int i = 0; i < optionsGroup.getChildCount(); i++) {
            optionsGroup.getChildAt(i).setBackgroundResource(R.drawable.option_background);
        }

        ((RadioButton) findViewById(R.id.option1)).setText(question.getOptions().get(0));
        ((RadioButton) findViewById(R.id.option2)).setText(question.getOptions().get(1));
        ((RadioButton) findViewById(R.id.option3)).setText(question.getOptions().get(2));
        ((RadioButton) findViewById(R.id.option4)).setText(question.getOptions().get(3));

        optionsGroup.clearCheck();
        optionsGroup.setEnabled(true);
        nextButton.setEnabled(false);
        nextButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        nextButton.setText(currentQuestionIndex == questions.size() - 1 ? "ЗАВЕРШИТЬ" : "ДАЛЕЕ");

        isResultShown = false;
        resultText.setVisibility(View.GONE);
    }

    private void showResult() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) return;

        RadioButton selectedRadioButton = findViewById(selectedId);
        int selectedIndex = optionsGroup.indexOfChild(selectedRadioButton);
        selectedAnswers.add(selectedIndex);

        Question question = questions.get(currentQuestionIndex);
        boolean isCorrect = selectedIndex == question.getCorrectOptionIndex();

        resultText.setText(isCorrect ? "Верно" : "Неверно");
        resultText.setVisibility(View.VISIBLE);

        int correctIndex = question.getCorrectOptionIndex();
        RadioButton correctRadio = (RadioButton) optionsGroup.getChildAt(correctIndex);
        correctRadio.setBackgroundResource(R.drawable.correct_option_background);

        if (!isCorrect) {
            selectedRadioButton.setBackgroundResource(R.drawable.incorrect_option_background);
        } else {
            score++;
        }

        isResultShown = true;
        optionsGroup.setEnabled(false);
        nextButton.setEnabled(false);

        new Handler().postDelayed(() -> {
            if (isResultShown) {
                nextQuestion();
            }
        }, 2000);
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        loadQuestion();
    }

    private void showTimeUpOverlay() {
        timeUpOverlay.setVisibility(View.VISIBLE);
        optionsGroup.setEnabled(false);
        nextButton.setEnabled(false);
        new Handler().postDelayed(this::finishQuiz, 2000);
    }

    private void finishQuiz() {
        while (selectedAnswers.size() < questions.size()) {
            selectedAnswers.add(-1);
        }

        new Thread(() -> {
            AppDatabase.getInstance(getApplicationContext())
                    .quizAttemptDao()
                    .insert(new QuizAttempt(score, System.currentTimeMillis()));

            runOnUiThread(() -> {
                Intent intent = new Intent(QuestionActivity.this, ResultActivity.class);
                intent.putExtra("score", score);
                intent.putParcelableArrayListExtra("questions", questions);
                intent.putIntegerArrayListExtra("selectedAnswers", selectedAnswers);
                startActivity(intent);
            });
        }).start();
    }

    private void restartQuiz() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}