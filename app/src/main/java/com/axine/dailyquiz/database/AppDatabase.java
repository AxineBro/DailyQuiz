package com.axine.dailyquiz.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.axine.dailyquiz.model.QuizAttempt;

/**
 * Room database for storing quiz attempts in the DailyQuiz application.
 * Provides access to the QuizAttemptDao for managing quiz attempt data.
 */
@Database(entities = {QuizAttempt.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Provides access to the DAO for quiz attempt operations.
     * @return QuizAttemptDao instance for database operations
     */
    public abstract QuizAttemptDao quizAttemptDao();

    private static volatile AppDatabase instance;

    /**
     * Gets the singleton instance of the AppDatabase.
     * Uses double-checked locking to ensure thread-safe initialization.
     * @param context The application context
     * @return The singleton instance of AppDatabase
     */
    public static AppDatabase getInstance(android.content.Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "quiz_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return instance;
    }

    /**
     * Migration from version 1 to version 2.
     * Updates the quiz_attempt table to ensure compatibility with Long type for id.
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE quiz_attempt_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, score INTEGER NOT NULL, timestamp INTEGER NOT NULL)");
            database.execSQL("INSERT INTO quiz_attempt_new (id, score, timestamp) SELECT id, score, timestamp FROM quiz_attempt");
            database.execSQL("DROP TABLE quiz_attempt");
            database.execSQL("ALTER TABLE quiz_attempt_new RENAME TO quiz_attempt");
        }
    };
}