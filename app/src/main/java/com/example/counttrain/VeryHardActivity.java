package com.example.counttrain;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class VeryHardActivity extends AppCompatActivity {

    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitBtn;
    private Button newQuestionBtn;
    private int num1, num2, num3, correctAnswer;
    private char operator1, operator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_very_hard);

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
        num1 = 10 + random.nextInt(90); // 10-99
        num2 = 10 + random.nextInt(90); // 10-99
        num3 = 10 + random.nextInt(90); // 10-99
        char[] operators = {'+', '-', '*', '/'};
        operator1 = operators[random.nextInt(4)];
        operator2 = operators[random.nextInt(4)];
        if (operator1 == '/') {
            num2 = findDivisor(num1);
        }
        if (operator2 == '/') {
            num3 = findDivisor(operator1 == '/' ? num1/num2 :
                    operator1 == '*' ? num1*num2 :
                            operator1 == '+' ? num1+num2 : num1-num2);
        }
        correctAnswer = calculateResult(num1, num2, num3, operator1, operator2);
        questionTextView.setText(num1 + " " + operator1 + " " + num2 + " " + operator2 + " " + num3 + " = ?");
    }

    private int findDivisor(int dividend) {
        Random random = new Random();
        List<Integer> divisors = new ArrayList<>();
        for (int i = 1; i <= dividend; i++) {
            if (dividend % i == 0) {
                divisors.add(i);
            }
        }
        return divisors.get(random.nextInt(divisors.size()));
    }

    private int calculateResult(int n1, int n2, int n3, char op1, char op2) {
        int firstResult;
        switch (op1) {
            case '+': firstResult = n1 + n2; break;
            case '-': firstResult = n1 - n2; break;
            case '*': firstResult = n1 * n2; break;
            case '/': firstResult = n1 / n2; break;
            default: firstResult = 0;
        }

        switch (op2) {
            case '+': return firstResult + n3;
            case '-': return firstResult - n3;
            case '*': return firstResult * n3;
            case '/': return firstResult / n3;
            default: return 0;
        }
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
                Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }
}