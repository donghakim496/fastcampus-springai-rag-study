package com.example.demo.record;

import com.example.demo.enums.Emotion;

import java.util.List;

public record EmotionEvaluation(Emotion emotion, List<String> reasons) {
}
