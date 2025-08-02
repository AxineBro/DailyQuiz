package com.axine.dailyquiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.axine.dailyquiz.R;
import com.axine.dailyquiz.model.QuizResult;
import com.axine.dailyquiz.model.Question;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.result_text);
        TextView messageTextHeading = findViewById(R.id.message_text_heading);
        TextView messageText = findViewById(R.id.message_text);
        Button restartButton = findViewById(R.id.restart_button);
        Button reviewButton = findViewById(R.id.review_button);
        LinearLayout starContainer = findViewById(R.id.star_container);

        int score = getIntent().getIntExtra("score", 0);
        ArrayList<Question> questions = getIntent().getParcelableArrayListExtra("questions");
        ArrayList<Integer> selectedAnswers = getIntent().getIntegerArrayListExtra("selectedAnswers");

        resultText.setText(String.format("Результат: %d/5", score));

        String title, subtitle;
        switch (score) {
            case 5:
                title = "Идеально!";
                subtitle = "5/5 — вы ответили на всё правильно. Это блестящий результат!";
                break;
            case 4:
                title = "Почти идеально!";
                subtitle = "4/5 — очень близко к совершенству. Ещё один шаг!";
                break;
            case 3:
                title = "Хороший результат!";
                subtitle = "3/5 — вы на верном пути. Продолжайте тренироваться!";
                break;
            case 2:
                title = "Есть над чем поработать";
                subtitle = "2/5 — не расстраивайтесь, попробуйте ещё раз!";
                break;
            case 1:
                title = "Сложный вопрос?";
                subtitle = "1/5 — иногда просто не ваш день. Следующая попытка будет лучше!";
                break;
            default:
                title = "Бывает и так!";
                subtitle = "0/5 — не отчаивайтесь. Настройтесь и попробуйте снова!";
                break;
        }
        messageTextHeading.setText(title);
        messageText.setText(subtitle);

        starContainer.removeAllViews();
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this);
            star.setImageResource(i < score ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 8, 0);
            star.setLayoutParams(params);
            starContainer.addView(star);
        }

        if (selectedAnswers != null) {
            QuizResult.getInstance().saveQuizResult(this, selectedAnswers, score);
        }

        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        reviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putParcelableArrayListExtra("questions", questions);
            intent.putExtra("score", score);
            startActivity(intent);
        });
    }
}