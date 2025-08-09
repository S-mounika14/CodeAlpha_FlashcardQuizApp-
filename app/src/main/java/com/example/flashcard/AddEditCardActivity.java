package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddEditCardActivity extends AppCompatActivity {
    private EditText questionEditText;
    private EditText answerEditText;
    private Button saveButton;
    private Button cancelButton;
    private TextView characterCountQuestion;
    private TextView characterCountAnswer;

    private boolean isEditMode = false;
    private int cardIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_card);

        setupToolbar();
        initializeViews();
        setupFromIntent();
        setupClickListeners();
        setupTextWatchers();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeViews() {
        questionEditText = findViewById(R.id.questionEditText);
        answerEditText = findViewById(R.id.answerEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        characterCountQuestion = findViewById(R.id.characterCountQuestion);
        characterCountAnswer = findViewById(R.id.characterCountAnswer);
    }

    private void setupFromIntent() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("edit_mode", false);

        if (isEditMode) {
            getSupportActionBar().setTitle("Edit Flashcard");
            cardIndex = intent.getIntExtra("card_index", -1);
            questionEditText.setText(intent.getStringExtra("question"));
            answerEditText.setText(intent.getStringExtra("answer"));
            saveButton.setText("Update Card");
        } else {
            getSupportActionBar().setTitle("Add New Flashcard");
            saveButton.setText("Add Card");
        }
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveCard());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void setupTextWatchers() {
        questionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharacterCount(characterCountQuestion, s.length(), 500);
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        answerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharacterCount(characterCountAnswer, s.length(), 500);
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        updateCharacterCount(characterCountQuestion, questionEditText.getText().length(), 500);
        updateCharacterCount(characterCountAnswer, answerEditText.getText().length(), 500);
        updateSaveButtonState();
    }

    private void updateCharacterCount(TextView countView, int currentLength, int maxLength) {
        countView.setText(currentLength + "/" + maxLength);
        if (currentLength > maxLength * 0.8) {
            countView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            countView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void updateSaveButtonState() {
        boolean isValid = !TextUtils.isEmpty(questionEditText.getText().toString().trim()) &&
                !TextUtils.isEmpty(answerEditText.getText().toString().trim());

        saveButton.setEnabled(isValid);
        saveButton.setAlpha(isValid ? 1.0f : 0.5f);
    }

    private void saveCard() {
        String question = questionEditText.getText().toString().trim();
        String answer = answerEditText.getText().toString().trim();

        if (TextUtils.isEmpty(question)) {
            questionEditText.setError("Question cannot be empty");
            questionEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(answer)) {
            answerEditText.setError("Answer cannot be empty");
            answerEditText.requestFocus();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("question", question);
        resultIntent.putExtra("answer", answer);

        if (isEditMode) {
            resultIntent.putExtra("card_index", cardIndex);
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}