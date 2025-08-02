package com.axine.dailyquiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.axine.dailyquiz.R;
import com.axine.dailyquiz.model.ApiQuestion;
import com.axine.dailyquiz.model.Question;
import com.axine.dailyquiz.model.QuestionResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import android.text.Html;

public class LoadingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RelativeLayout errorPanel;
    private TextView errorText;
    private Button retryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progress_bar);
        errorPanel = findViewById(R.id.error_panel);
        errorText = findViewById(R.id.error_text);
        retryButton = findViewById(R.id.retry_button);

        retryButton.setOnClickListener(v -> loadQuestions());

        loadQuestions();
    }

    private void loadQuestions() {
        progressBar.setVisibility(View.VISIBLE);
        errorPanel.setVisibility(View.GONE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuizApiService apiService = retrofit.create(QuizApiService.class);
        Call<QuestionResponse> call = apiService.getQuestions(5, "multiple"); // Теперь корректно

        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResponseCode() == 0) {
                    List<Question> questions = convertToQuestions(response.body().getQuestions());
                    startQuestionActivity(questions);
                } else {
                    showError("Ошибка загрузки вопросов");
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                showError("Нет соединения с интернетом");
            }
        });
    }

    private List<Question> convertToQuestions(List<ApiQuestion> apiQuestions) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < apiQuestions.size(); i++) {
            ApiQuestion apiQuestion = apiQuestions.get(i);
            // Декодируем текст вопроса
            String decodedQuestion = Html.fromHtml(apiQuestion.getQuestion(), Html.FROM_HTML_MODE_LEGACY).toString();
            // Декодируем варианты ответа
            List<String> options = new ArrayList<>();
            for (String answer : apiQuestion.getIncorrectAnswers()) {
                options.add(Html.fromHtml(answer, Html.FROM_HTML_MODE_LEGACY).toString());
            }
            options.add(Html.fromHtml(apiQuestion.getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY).toString());
            Collections.shuffle(options);
            int correctIndex = options.indexOf(Html.fromHtml(apiQuestion.getCorrectAnswer(), Html.FROM_HTML_MODE_LEGACY).toString());
            questions.add(new Question(
                    i + 1,
                    decodedQuestion,
                    options,
                    correctIndex,
                    apiQuestion.getCategory(),
                    apiQuestion.getDifficulty()
            ));
        }
        return questions;
    }

    private void showError(String message) {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setText(message);
        errorPanel.setVisibility(View.VISIBLE);
    }

    private void startQuestionActivity(List<Question> questions) {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putParcelableArrayListExtra("questions", new ArrayList<>(questions));
        startActivity(intent);
        finish();
    }

    interface QuizApiService {
        @GET("api.php")
        Call<QuestionResponse> getQuestions(
                @Query("amount") int amount,
                @Query("type") String type
        );
    }
}