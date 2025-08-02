package com.axine.dailyquiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.axine.dailyquiz.R;
import com.axine.dailyquiz.database.AppDatabase;
import com.axine.dailyquiz.database.QuizAttemptDao;
import com.axine.dailyquiz.model.QuizAttempt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private View mContextMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Button backToMenuButton = findViewById(R.id.back_to_menu_button);
        LinearLayout historyContainer = findViewById(R.id.history_container);

        AppDatabase db = AppDatabase.getInstance(this);
        QuizAttemptDao dao = db.quizAttemptDao();
        List<QuizAttempt> attempts = dao.getAllAttempts();

        historyContainer.removeAllViews();

        if (attempts.isEmpty()) {
            TextView noAttemptsText = new TextView(this);
            noAttemptsText.setText("Вы еще не проходили викторины");
            noAttemptsText.setTextSize(16);
            noAttemptsText.setPadding(16, 16, 16, 16);
            historyContainer.addView(noAttemptsText);
        } else {
            for (QuizAttempt attempt : attempts) {
                View historyItem = getLayoutInflater().inflate(R.layout.history_item, null);
                TextView quizTitle = historyItem.findViewById(R.id.quiz_title);
                LinearLayout starContainer = historyItem.findViewById(R.id.star_container);
                TextView quizTime = historyItem.findViewById(R.id.quiz_time);
                TextView quizDate = historyItem.findViewById(R.id.quiz_date);

                quizTitle.setText("Викторина");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date quizDateObj = new Date(attempt.getTimestamp());
                quizTime.setText(timeFormat.format(quizDateObj));
                quizDate.setText(dateFormat.format(quizDateObj));

                starContainer.removeAllViews();
                for (int i = 0; i < 5; i++) {
                    ImageView star = new ImageView(this);
                    star.setImageResource(i < attempt.getScore() ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 8, 0);
                    star.setLayoutParams(params);
                    starContainer.addView(star);
                }

                historyItem.setTag(attempt.getId());
                historyItem.setLongClickable(true);
                historyContainer.addView(historyItem);
            }
        }

        backToMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mContextMenuView = v;
        menu.add(0, 0, 0, "Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            View view = mContextMenuView;
            Long attemptId = (Long) view.getTag();

            AppDatabase db = AppDatabase.getInstance(this);
            QuizAttemptDao dao = db.quizAttemptDao();
            dao.deleteAttempt(attemptId);

            LinearLayout historyContainer = findViewById(R.id.history_container);
            historyContainer.removeView(view);

            if (historyContainer.getChildCount() == 0) {
                TextView noAttemptsText = new TextView(this);
                noAttemptsText.setText("Вы еще не проходили викторины");
                noAttemptsText.setTextSize(16);
                noAttemptsText.setPadding(16, 16, 16, 16);
                historyContainer.addView(noAttemptsText);
            }

            Toast.makeText(this, "Попытка удалена", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}