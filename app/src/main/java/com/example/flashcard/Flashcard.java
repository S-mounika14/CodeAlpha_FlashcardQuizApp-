package com.example.flashcard;

public class Flashcard {
    private String question;
    private String answer;
    private long createdTime;

    public Flashcard() {
        this.createdTime = System.currentTimeMillis();
    }

    public Flashcard(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.createdTime = System.currentTimeMillis();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}