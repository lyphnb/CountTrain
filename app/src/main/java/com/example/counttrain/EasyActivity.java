package com.example.counttrain;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
public class EasyActivity extends AppCompatActivity {
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitBtn;
    private Button newQuestionBtn;
    private int num1, num2, correctAnswer;
    private char operator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy);

        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        submitBtn = findViewById(R.id.submitBtn);
        newQuestionBtn = findViewById(R.id.newQuestionBtn);
        generateNewQuestion();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
        newQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNewQuestion();
                answerEditText.setText("");
            }
        });
    }
    private void generateNewQuestion() {
        Random random = new Random();
        num1 = random.nextInt(10); // 0-9
        num2 = random.nextInt(10); // 0-9
        operator = random.nextBoolean() ? '+' : '-';
        if (operator == '-' && num1 < num2) {
            int temp = num1;
            num1 = num2;
            num2 = temp;
        }
        correctAnswer = operator == '+' ? num1 + num2 : num1 - num2;
        questionTextView.setText(num1 + " " + operator + " " + num2 + " = ?");
    }
    private void checkAnswer() {
        String userAnswerStr = answerEditText.getText().toString().trim();
        if (userAnswerStr.isEmpty()) {
            Toast.makeText(this, "Please enter your answer", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int userAnswer = Integer.parseInt(userAnswerStr);
            if (userAnswer == correctAnswer) {
                Toast.makeText(this, "回答正确", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请再试一次", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }
}
