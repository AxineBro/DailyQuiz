package com.axine.dailyquiz.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

        Button restartButtonTop = findViewById(R.id.restart_button_top);
        Button restartButtonBottom = findViewById(R.id.restart_button_bottom);

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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(64, 64); // размер иконки
            params.setMargins(4, 0, 4, 0);
            star.setLayoutParams(params);
            starContainer.addView(star);
        }

        if (questions != null && selectedAnswers != null) {
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                int selectedIndex = selectedAnswers.get(i);

                LinearLayout questionLayout = new LinearLayout(this);
                questionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                questionLayout.setBackgroundResource(R.drawable.white_card_bg);
                questionLayout.setPadding(16, 16, 16, 16);
                questionLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout headerLayout = new LinearLayout(this);
                headerLayout.setOrientation(LinearLayout.HORIZONTAL);
                headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                TextView questionNumber = new TextView(this);
                questionNumber.setText(String.format("Вопрос %d из %d", i + 1, questions.size()));
                questionNumber.setTextSize(14);
                questionNumber.setTextColor(Color.DKGRAY);
                headerLayout.addView(questionNumber);

                LinearLayout spacer = new LinearLayout(this);
                LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(0, 0, 1);
                spacer.setLayoutParams(spacerParams);
                headerLayout.addView(spacer);

                ImageView statusIcon = new ImageView(this);
                statusIcon.setImageResource(
                        selectedIndex == question.getCorrectOptionIndex() ?
                                R.drawable.ic_check : R.drawable.ic_cross
                );
                GradientDrawable circleBg = new GradientDrawable();
                circleBg.setShape(GradientDrawable.OVAL);
                circleBg.setSize(64, 64);
                if (selectedIndex == question.getCorrectOptionIndex()) {
                    circleBg.setColor(Color.parseColor("#4CAF50"));
                } else {
                    circleBg.setColor(Color.parseColor("#F44336"));
                }
                statusIcon.setBackground(circleBg);
                int padding = 12;
                statusIcon.setPadding(padding, padding, padding, padding);

                headerLayout.addView(statusIcon);

                questionLayout.addView(headerLayout);

                TextView questionText = new TextView(this);
                questionText.setText(question.getText());
                questionText.setTextSize(18);
                questionText.setTypeface(null, Typeface.BOLD);
                questionText.setTextColor(Color.BLACK);
                questionText.setPadding(0, 8, 0, 8);
                questionLayout.addView(questionText);

                for (int j = 0; j < question.getOptions().size(); j++) {
                    TextView optionView = new TextView(this);
                    optionView.setText(question.getOptions().get(j));
                    optionView.setTextSize(16);
                    optionView.setPadding(12, 12, 12, 12);
                    optionView.setBackgroundResource(R.drawable.option_background);
                    optionView.setTextColor(Color.BLACK);

                    if (j == selectedIndex && j == question.getCorrectOptionIndex()) {
                        optionView.setBackgroundResource(R.drawable.correct_option_background);
                    } else if (j == selectedIndex && j != question.getCorrectOptionIndex()) {
                        optionView.setBackgroundResource(R.drawable.incorrect_option_background);
                    } else if (j == question.getCorrectOptionIndex()) {
                        optionView.setBackgroundResource(R.drawable.correct_option_background);
                    }

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    lp.setMargins(0, 8, 0, 0);
                    optionView.setLayoutParams(lp);

                    questionLayout.addView(optionView);
                }

                LinearLayout.LayoutParams questionLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                questionLp.setMargins(0, 0, 0, 16);
                questionLayout.setLayoutParams(questionLp);

                questionsContainer.addView(questionLayout);
            }
        }

        restartButtonTop.setOnClickListener(v -> restartQuiz());
        restartButtonBottom.setOnClickListener(v -> restartQuiz());
    }

    private void restartQuiz() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
