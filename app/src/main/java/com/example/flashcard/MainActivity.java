package com.example.flashcard;


import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_CARD = 1;
    private static final int REQUEST_EDIT_CARD = 2;

    private List<Flashcard> flashcards;
    private int currentCardIndex = 0;
    private boolean isShowingAnswer = false;
    private FlashcardManager flashcardManager;

    private CardView flashcardView;
    private TextView cardContent;
    private TextView cardCounter;
    private TextView cardLabel;
    private Button showAnswerButton;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private Button addCardButton;
    private Button editCardButton;
    private Button deleteCardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardManager = new FlashcardManager(this);

        initializeViews();
        loadFlashcards();
        setupClickListeners();
        displayCurrentCard();
    }

    private void initializeViews() {
        flashcardView = findViewById(R.id.flashcardView);
        cardContent = findViewById(R.id.cardContent);
        cardCounter = findViewById(R.id.cardCounter);
        cardLabel = findViewById(R.id.cardLabel);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        addCardButton = findViewById(R.id.addCardButton);
        editCardButton = findViewById(R.id.editCardButton);
        deleteCardButton = findViewById(R.id.deleteCardButton);
    }

    private void loadFlashcards() {
        flashcards = flashcardManager.loadFlashcards();
        if (currentCardIndex >= flashcards.size()) {
            currentCardIndex = flashcards.size() > 0 ? 0 : 0;
        }
    }

    private void setupClickListeners() {
        showAnswerButton.setOnClickListener(v -> toggleAnswer());

        flashcardView.setOnClickListener(v -> toggleAnswer());

        previousButton.setOnClickListener(v -> {
            if (currentCardIndex > 0) {
                currentCardIndex--;
                isShowingAnswer = false;
                displayCurrentCard();
                animateCardTransition();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentCardIndex < flashcards.size() - 1) {
                currentCardIndex++;
                isShowingAnswer = false;
                displayCurrentCard();
                animateCardTransition();
            }
        });

        addCardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditCardActivity.class);
            startActivityForResult(intent, REQUEST_ADD_CARD);
        });

        editCardButton.setOnClickListener(v -> {
            if (!flashcards.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, AddEditCardActivity.class);
                intent.putExtra("edit_mode", true);
                intent.putExtra("card_index", currentCardIndex);
                intent.putExtra("question", flashcards.get(currentCardIndex).getQuestion());
                intent.putExtra("answer", flashcards.get(currentCardIndex).getAnswer());
                startActivityForResult(intent, REQUEST_EDIT_CARD);
            }
        });

        deleteCardButton.setOnClickListener(v -> deleteCurrentCard());
    }

    private void toggleAnswer() {
        if (flashcards.isEmpty()) return;

        isShowingAnswer = !isShowingAnswer;
        animateCardFlip();
    }

    private void animateCardFlip() {
        flashcardView.animate()
                .scaleX(0f)
                .setDuration(150)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    displayCurrentCard();
                    flashcardView.animate()
                            .scaleX(1f)
                            .setDuration(150)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                })
                .start();
    }

    private void animateCardTransition() {
        flashcardView.animate()
                .translationX(-50f)
                .alpha(0.7f)
                .setDuration(100)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    flashcardView.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(100)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                })
                .start();
    }

    private void displayCurrentCard() {
        if (flashcards.isEmpty()) {
            cardContent.setText("No flashcards available.\n\nTap the + button to add your first flashcard!");
            cardCounter.setText("0 / 0");
            cardLabel.setText("Empty Deck");
            showAnswerButton.setText("Add First Card");
            showAnswerButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            editCardButton.setEnabled(false);
            deleteCardButton.setEnabled(false);
            flashcardView.setBackgroundResource(R.drawable.bg_card_front);
            return;
        }

        Flashcard currentCard = flashcards.get(currentCardIndex);

        if (isShowingAnswer) {
            cardContent.setText(currentCard.getAnswer());
            cardLabel.setText("Answer");
            showAnswerButton.setText("Show Question");
            flashcardView.setBackgroundResource(R.drawable.bg_card_back);
        } else {
            cardContent.setText(currentCard.getQuestion());
            cardLabel.setText("Question");
            showAnswerButton.setText("Show Answer");
            flashcardView.setBackgroundResource(R.drawable.bg_card_front);
        }

        cardCounter.setText((currentCardIndex + 1) + " / " + flashcards.size());

        previousButton.setEnabled(currentCardIndex > 0);
        nextButton.setEnabled(currentCardIndex < flashcards.size() - 1);
        showAnswerButton.setEnabled(true);
        editCardButton.setEnabled(true);
        deleteCardButton.setEnabled(true);

        previousButton.setAlpha(currentCardIndex > 0 ? 1.0f : 0.5f);
        nextButton.setAlpha(currentCardIndex < flashcards.size() - 1 ? 1.0f : 0.5f);
    }

    private void deleteCurrentCard() {
        if (flashcards.isEmpty()) return;

        flashcards.remove(currentCardIndex);
        flashcardManager.saveFlashcards(flashcards);

        if (flashcards.isEmpty()) {
            currentCardIndex = 0;
        } else if (currentCardIndex >= flashcards.size()) {
            currentCardIndex = flashcards.size() - 1;
        }

        isShowingAnswer = false;
        displayCurrentCard();
        Toast.makeText(this, "Card deleted successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String question = data.getStringExtra("question");
            String answer = data.getStringExtra("answer");

            if (requestCode == REQUEST_ADD_CARD) {
                flashcards.add(new Flashcard(question, answer));
                currentCardIndex = flashcards.size() - 1;
                Toast.makeText(this, "Card added successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_EDIT_CARD) {
                int cardIndex = data.getIntExtra("card_index", currentCardIndex);
                flashcards.set(cardIndex, new Flashcard(question, answer));
                Toast.makeText(this, "Card updated successfully", Toast.LENGTH_SHORT).show();
            }

            flashcardManager.saveFlashcards(flashcards);
            isShowingAnswer = false;
            displayCurrentCard();
        }
    }
}