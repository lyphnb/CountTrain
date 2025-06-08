package com.example.counttrain;

public class StatsModel {
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
        return String.format("正确率: %.1f%%\n最近10题用时: %d秒",
                accuracy,
                totalTime / 1000);
    }

    public boolean shouldShowStats() {
        return totalQuestions % 10 == 0 && totalQuestions > 0;
    }
}