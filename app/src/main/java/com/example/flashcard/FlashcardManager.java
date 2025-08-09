package com.example.flashcard;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class FlashcardManager {
    private static final String PREFS_NAME = "FlashcardPrefs";
    private static final String FLASHCARDS_COUNT_KEY = "flashcards_count";
    private static final String QUESTION_PREFIX = "question_";
    private static final String ANSWER_PREFIX = "answer_";
    private static final String TIME_PREFIX = "time_";

    private SharedPreferences sharedPreferences;
    private Context context;

    public FlashcardManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<Flashcard> loadFlashcards() {
        int flashcardCount = sharedPreferences.getInt(FLASHCARDS_COUNT_KEY, -1);
        List<Flashcard> flashcards = new ArrayList<>();

        if (flashcardCount > 0) {
            for (int i = 0; i < flashcardCount; i++) {
                String question = sharedPreferences.getString(QUESTION_PREFIX + i, "");
                String answer = sharedPreferences.getString(ANSWER_PREFIX + i, "");
                long time = sharedPreferences.getLong(TIME_PREFIX + i, System.currentTimeMillis());

                if (!question.isEmpty() && !answer.isEmpty()) {
                    Flashcard card = new Flashcard(question, answer);
                    card.setCreatedTime(time);
                    flashcards.add(card);
                }
            }
        } else {
            flashcards = createDefaultFlashcards();
            saveFlashcards(flashcards);
        }

        return flashcards;
    }

    public void saveFlashcards(List<Flashcard> flashcards) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.putInt(FLASHCARDS_COUNT_KEY, flashcards.size());

        for (int i = 0; i < flashcards.size(); i++) {
            Flashcard card = flashcards.get(i);
            editor.putString(QUESTION_PREFIX + i, card.getQuestion());
            editor.putString(ANSWER_PREFIX + i, card.getAnswer());
            editor.putLong(TIME_PREFIX + i, card.getCreatedTime());
        }

        editor.apply();
    }

    private List<Flashcard> createDefaultFlashcards() {
        List<Flashcard> defaultCards = new ArrayList<>();
        defaultCards.add(new Flashcard("What is the capital of France?", "Paris"));
        defaultCards.add(new Flashcard("What is 2 + 2?", "4"));
        defaultCards.add(new Flashcard("Who wrote Romeo and Juliet?", "William Shakespeare"));
        defaultCards.add(new Flashcard("What is the largest planet in our solar system?", "Jupiter"));
        defaultCards.add(new Flashcard("What is the chemical symbol for gold?", "Au"));
        return defaultCards;
    }
}