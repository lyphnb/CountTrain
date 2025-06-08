package com.example.counttrain;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
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
    private StatsModel statsModel = new StatsModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_very_hard);

        questionTextView = findViewById(R.id.questionTextView);
        answerEditText = findViewById(R.id.answerEditText);
        submitBtn = findViewById(R.id.submitBtn);
        newQuestionBtn = findViewById(R.id.newQuestionBtn);
        statsModel.loadStats(this);
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
    @Override
    protected void onPause() {
        super.onPause();
        // 保存统计记录
        statsModel.saveStats(this);
    }
    private void generateNewQuestion() {
        Random random = new Random();
        num1 = 10 + random.nextInt(90);
        num2 = 10 + random.nextInt(90);
        num3 = 10 + random.nextInt(90);
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
            Toast.makeText(this, "请输入答案", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int userAnswer = Integer.parseInt(userAnswerStr);
            boolean isCorrect = (userAnswer == correctAnswer);
            statsModel.recordAnswer(isCorrect);

            if (isCorrect) {
                Toast.makeText(this, "回答正确", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请再试一次", Toast.LENGTH_SHORT).show();
            }
            if (statsModel.shouldShowStats()) {
                showStatsDialog();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效数字", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStatsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("练习统计")
                .setMessage(statsModel.getStats())
                .setPositiveButton("确定", null)
                .show();
    }
    private class StatsModel {
        private int totalQuestions;
        private int correctAnswers;
        private long startTime;
        private long totalTime;

        public StatsModel() {
            reset();
        }

        public void reset() {
            totalQuestions = 0;
            correctAnswers = 0;
            startTime = System.currentTimeMillis();
            totalTime = 0;
        }
        public void recordAnswer(boolean isCorrect) {
            totalQuestions++;
            if (isCorrect) {
                correctAnswers++;
            }
            if (totalQuestions % 10 == 0) {
                long endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                startTime = endTime;
            }
        }

        public String getStats() {
            if (totalQuestions == 0) return "暂无统计数据";

            float accuracy = (float) correctAnswers / totalQuestions * 100;
            return String.format("总题数: %d\n正确数: %d\n正确率: %.1f%%\n最近10题用时: %d秒",
                    totalQuestions,
                    correctAnswers,
                    accuracy,
                    totalTime / 1000);
        }
        public boolean shouldShowStats() {
            return totalQuestions % 10 == 0 && totalQuestions > 0;
        }
        public void saveStats(Context context) {
            SharedPreferences prefs = context.getSharedPreferences("MathStats_VeryHard", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("totalQuestions", totalQuestions);
            editor.putInt("correctAnswers", correctAnswers);
            editor.apply();
        }
        public void loadStats(Context context) {
            SharedPreferences prefs = context.getSharedPreferences("MathStats_VeryHard", Context.MODE_PRIVATE);
            totalQuestions = prefs.getInt("totalQuestions", 0);
            correctAnswers = prefs.getInt("correctAnswers", 0);
            startTime = System.currentTimeMillis();
        }
    }
}