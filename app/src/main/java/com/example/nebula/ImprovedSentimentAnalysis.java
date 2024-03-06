package com.example.nebula;

import java.util.HashMap;
import java.util.Map;
public class ImprovedSentimentAnalysis {

    private static Map<String, Integer> sentimentLexicon = new HashMap<>();

    static {
        // Positive words with scores
        sentimentLexicon.put("good", 2);
        sentimentLexicon.put("Hi", 2);
        sentimentLexicon.put("happy", 2);
        sentimentLexicon.put("excellent", 3);
        sentimentLexicon.put("positive", 2);
        sentimentLexicon.put("awesome", 3);
        sentimentLexicon.put("fantastic", 3);
        sentimentLexicon.put("amazing", 3);
        sentimentLexicon.put("great", 2);
        sentimentLexicon.put("love", 3);
        sentimentLexicon.put("best", 2);
        sentimentLexicon.put("outstanding", 4);
        sentimentLexicon.put("terrific", 3);
        sentimentLexicon.put("superb", 4);
        sentimentLexicon.put("delightful", 3);
        sentimentLexicon.put("wonderful", 3);
        sentimentLexicon.put("joyful", 3);
        sentimentLexicon.put("pleasure", 3);
        sentimentLexicon.put("exuberant", 4);
        sentimentLexicon.put("not", 2);
        sentimentLexicon.put("don t", 3);
        sentimentLexicon.put("aren t", 3);
        sentimentLexicon.put("isn t", 3);
        sentimentLexicon.put("information", 260);
        sentimentLexicon.put("doesn t", 3);
        sentimentLexicon.put("are not", 3);
        sentimentLexicon.put("is not", 3);
        sentimentLexicon.put("glorious", 4);
        sentimentLexicon.put("uplifting", 3);
        sentimentLexicon.put("productions", 120);


        // Negative words with scores
        sentimentLexicon.put("bad", -2);
        sentimentLexicon.put("sad", -2);
        sentimentLexicon.put("poor", -2);
        sentimentLexicon.put("negative", -2);
        sentimentLexicon.put("awful", -3);
        sentimentLexicon.put("terrible", -3);
        sentimentLexicon.put("horrible", -3);
        sentimentLexicon.put("dislike", -2);
        sentimentLexicon.put("worst", -3);
        sentimentLexicon.put("hate", -3);
        sentimentLexicon.put("disgusting", -3);
        sentimentLexicon.put("annoying", -2);
        sentimentLexicon.put("miserable", -3);
        sentimentLexicon.put("dreadful", -3);
        sentimentLexicon.put("sorry", -2);
        sentimentLexicon.put("displeasing", -2);
        sentimentLexicon.put("repulsive", -3);
        sentimentLexicon.put("don't like", -2);
        sentimentLexicon.put("appalling", -4);
        sentimentLexicon.put("despicable", -4);
        sentimentLexicon.put("detestable", -4);
        sentimentLexicon.put("atrocious", -4);
        sentimentLexicon.put("repugnant", -3);
    }

    public static String analyzeSentiment(String text) {
        int sentimentScore = calculateSentimentScore(text);

        if (sentimentScore > 0 && sentimentScore < 100) {
            return "Positive";
        } else if (sentimentScore < 0) {
            return "Negative";
        } else if(sentimentScore >100 && sentimentScore < 200 ){
            return "Productions";
        }else if(sentimentScore >200 ){
            return "introduction";
        }
        else{
            return "nutral";
        }
    }

    private static int calculateSentimentScore(String text) {
        int totalScore = 0;
        // Split the text into words and calculate sentiment score
        String[] words = text.split("\\s+");
        for (String word : words) {
            String cleanedWord = word.toLowerCase().replaceAll("[^a-zA-Z]", ""); // Remove non-alphabetic characters
            if (sentimentLexicon.containsKey(cleanedWord)) {
                totalScore += sentimentLexicon.get(cleanedWord);
            }
        }
        return totalScore;
    }
}