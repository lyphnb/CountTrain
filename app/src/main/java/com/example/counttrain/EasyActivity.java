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

public class EasyActivity extends AppCompatActivity {
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitBtn;
    private Button newQuestionBtn;
    private int num1, num2, correctAnswer;
    private char operator;
    private StatsModel statsModel = new StatsModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy);

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
        statsModel.saveStats(this);
    }

    private void generateNewQuestion() {
        Random random = new Random();
        num1 = random.nextInt(10);
        num2 = random.nextInt(10);
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
                startTime = endTime; // 重置开始时间
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
            SharedPreferences prefs = context.getSharedPreferences("MathStats", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("totalQuestions", totalQuestions);
            editor.putInt("correctAnswers", correctAnswers);
            editor.apply();
        }

        public void loadStats(Context context) {
            SharedPreferences prefs = context.getSharedPreferences("MathStats", Context.MODE_PRIVATE);
            totalQuestions = prefs.getInt("totalQuestions", 0);
            correctAnswers = prefs.getInt("correctAnswers", 0);
            startTime = System.currentTimeMillis();
        }
    }
}