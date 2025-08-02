package com.axine.dailyquiz.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Question implements Parcelable {
    private int id;
    private String text;
    private List<String> options;
    private int correctOptionIndex;
    private String category;
    private String difficulty;

    public Question(int id, String text, List<String> options, int correctOptionIndex, String category, String difficulty) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.category = category;
        this.difficulty = difficulty;
    }

    protected Question(Parcel in) {
        id = in.readInt();
        text = in.readString();
        options = in.createStringArrayList();
        correctOptionIndex = in.readInt();
        category = in.readString();
        difficulty = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public void setCorrectOptionIndex(int correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(text);
        dest.writeStringList(options);
        dest.writeInt(correctOptionIndex);
        dest.writeString(category);
        dest.writeString(difficulty);
    }
}