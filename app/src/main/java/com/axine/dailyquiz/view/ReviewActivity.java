package com.axine.dailyquiz.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.axine.dailyquiz.R;
import com.axine.dailyquiz.model.Question;
import com.axine.dailyquiz.model.QuizResult;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView resultText = findViewById(R.id.result_text);
        TextView messageTextHeading = findViewById(R.id.message_text_heading);
        TextView messageText = findViewById(R.id.message_text);
        LinearLayout starContainer = findViewById(R.id.star_container);
        LinearLayout questionsContainer = findViewById(R.id.questions_container);
        Button restartButton = findViewById(R.id.restart_button);

        int score = getIntent().getIntExtra("score", 0);
        ArrayList<Question> questions = getIntent().getParcelableArrayListExtra("questions");
        ArrayList<Integer> selectedAnswers = QuizResult.getInstance().getSelectedAnswers();

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

        if (questions != null && selectedAnswers != null) {
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                int selectedIndex = selectedAnswers.get(i);
                boolean isCorrect = (selectedIndex != -1 && selectedIndex == question.getCorrectOptionIndex());

                LinearLayout questionLayout = new LinearLayout(this);
                questionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                questionLayout.setBackgroundResource(R.drawable.rounded_white_background);
                questionLayout.setPadding(16, 16, 16, 16);
                questionLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout headerLayout = new LinearLayout(this);
                headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                headerLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView questionNumber = new TextView(this);
                questionNumber.setText(String.format("Вопрос %d из 5", i + 1));
                questionNumber.setTextSize(16);
                questionNumber.setTextColor(Color.BLACK);

                ImageView statusIcon = new ImageView(this);
                statusIcon.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                ShapeDrawable circle = new ShapeDrawable(new OvalShape());
                circle.setIntrinsicWidth(24);
                circle.setIntrinsicHeight(24);
                circle.getPaint().setColor(isCorrect ? Color.GREEN : Color.RED);
                statusIcon.setBackground(circle);
                statusIcon.setImageResource(isCorrect ? R.drawable.ic_check : R.drawable.ic_cross);
                statusIcon.setPadding(4, 4, 16, 4);

                headerLayout.addView(questionNumber);
                headerLayout.addView(statusIcon);
                questionLayout.addView(headerLayout);

                TextView questionText = new TextView(this);
                questionText.setText(question.getText());
                questionText.setTextSize(18);
                questionText.setTypeface(null, Typeface.BOLD);
                questionText.setTextColor(Color.BLACK);
                questionLayout.addView(questionText);

                TextView userAnswer = new TextView(this);
                if (selectedIndex == -1) {
                    userAnswer.setText("Не отвечено");
                    userAnswer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross, 0, 0, 0);
                    TextView correctAnswer = new TextView(this);
                    correctAnswer.setText(String.format("Правильный ответ: %s", question.getOptions().get(question.getCorrectOptionIndex())));
                    correctAnswer.setTextSize(16);
                    correctAnswer.setTextColor(Color.GREEN);
                    questionLayout.addView(correctAnswer);
                } else {
                    userAnswer.setText(String.format("Ваш ответ: %s", question.getOptions().get(selectedIndex)));
                    if (isCorrect) {
                        userAnswer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                    } else {
                        userAnswer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cross, 0, 0, 0);
                        TextView correctAnswer = new TextView(this);
                        correctAnswer.setText(String.format("Правильный ответ: %s", question.getOptions().get(question.getCorrectOptionIndex())));
                        correctAnswer.setTextSize(16);
                        correctAnswer.setTextColor(Color.GREEN);
                        questionLayout.addView(correctAnswer);
                    }
                }
                userAnswer.setCompoundDrawablePadding(8);
                questionLayout.addView(userAnswer);

                questionsContainer.addView(questionLayout);
            }
        }

        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}